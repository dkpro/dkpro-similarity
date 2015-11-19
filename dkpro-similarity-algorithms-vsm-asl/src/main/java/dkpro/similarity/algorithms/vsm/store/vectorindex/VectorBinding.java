/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package dkpro.similarity.algorithms.vsm.store.vectorindex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.stream.ImageInputStreamImpl;
import javax.imageio.stream.ImageOutputStreamImpl;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.SparseVector;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.bzip2.CBZip2OutputStream;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * This binding tries to optimize vector storage. Very sparse vectors are simply stored as sparse
 * vectors. Other vectors are compressed using GZip. Depending on its sparseness, a vector is stored
 * either using a sparse representation or a dense representation.
 *
 * @author Richard Eckart de Castilho
 */
public class VectorBinding
	extends TupleBinding<Vector>
{
	private static final int FLAG_COMPRESSED 	= 1;
	private static final int FLAG_SPARSE  		= 1 << 1;
	private static final int MASK_COMPRESSION 	= 0x000000f0;
	private static final int FLAG_GZIP    		= 0 << 4;
	private static final int FLAG_BZIP2   		= 1 << 4;
	private static final int FLAG_RES_1   		= 2 << 4;
	private static final int FLAG_RES_2   		= 3 << 4;

	private final int vectorSize;
	private int compressThreshold = 1024;
	private float sparseRatio = 8 / 12; // dense = 8, sparse = 12 per entry
	private int compressionMethod = FLAG_GZIP;

//	private int count;
//	private int sparse;
//	private int dense;
//	private int stored;

	public VectorBinding(int vectorSize)
	{
		this.vectorSize = vectorSize;
	}

	public void setCompressionMethod(int aCompressionMethod)
	{
		switch (aCompressionMethod) {
		case FLAG_BZIP2:
		case FLAG_GZIP:
			compressionMethod = aCompressionMethod;
			break;
		default:
			throw new IllegalArgumentException("Unknown compression method");
		}
	}

	/**
	 * Set the threshold of the vector usage below which a vector is always stored as a sparse
	 * vector without compression.
	 */
	public void setCompressThreshold(int aCompressThreshold)
	{
		compressThreshold = aCompressThreshold;
	}

	public int getCompressThreshold()
	{
		return compressThreshold;
	}

	@Override
	public Vector entryToObject(TupleInput ti)
	{
//		long start = System.currentTimeMillis();
//		count++;

		try {
			int flags = ti.readInt();

			if ((flags & FLAG_COMPRESSED) != 0) {
				// Read a compressed vector
				// Read the compressed data
				int byteLen = ti.readInt();

				// Read the de-compressed data
				DirectDataInputStream in = null;
                try {
                	InputStream zin = getDeCompressingOutputStream(flags, new ByteArrayInputStream(
                            ti.getBufferBytes(), ti.getBufferOffset(), ti.getBufferOffset()+byteLen));
                    in = new DirectDataInputStream(zin);

                    if ((flags & FLAG_SPARSE) != 0) {
    //					sparse++;
                        // Read sparse
                        int vecLen = in.readInt(); // Vector data size
                        int[] index = new int[vecLen];
                        double[] data = new double[vecLen];
                        in.readFully(index, 0, index.length);
                        in.readFully(data, 0, data.length);
                        return new SparseVector(vectorSize, index, data, false);
                    }
                    else {
    //					dense++;
                        // Read dense
                        int vecLen = in.readInt(); // Vector data size
                        double[] data = new double[vecLen];
                        in.readFully(data, 0, data.length);
                        return new DenseVector(data, false);
                    }
                }
                finally {
                	if (in != null) {
                		try {
                			in.close();
                		}
                		finally {
                			// Ignore
                		}
                	}
                }
			}
			else {
				// Read a stored sparse vector
//				stored++;
				int vecLen = ti.readInt(); // Vector data size

				int[] index = new int[vecLen];
				double[] data = new double[vecLen];

				for (int i = 0; i < vecLen; i++) {
					index[i] = ti.readInt();
				}

				for (int i = 0; i < vecLen; i++) {
					data[i] = ti.readDouble();
				}
				return new SparseVector(vectorSize, index, data, false);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
//		finally {
////			System.out.println("Read in "+(System.currentTimeMillis()-start));
//			if ((count % 10) == 0) {
//				System.out.println("Count "+count+" stored "+stored+" sparse "+sparse+" dense "+dense);
//			}
//		}
	}

	@Override
	public void objectToEntry(Vector vec, TupleOutput to)
	{
//		long start = System.currentTimeMillis();
//		count++;

		int used;

		// Determine the non-zero items of the vector
		if (vec instanceof SparseVector) {
			((SparseVector) vec).compact();
			used = ((SparseVector) vec).getUsed();
		}
		else if (vec instanceof DenseVector) {
			used = 0;
			for (double i : ((DenseVector) vec).getData()) {
				if (i != 0.0) {
					used++;
				}
			}
		}
		else {
			used = Matrices.cardinality(vec);
		}

		try {
			if (used > compressThreshold) {
				// Write compressed
				int flags = FLAG_COMPRESSED;
				byte[] bytes;

				// For each entry in a sparse vector, we need 12 byte, for each entry in a
				// dense vector 8. Thus starting at 8/12 = 0.66 fill ratio it makes sense to
				// store the vector as a dense vector. Actually this calculation is not completely
				// true since we also store everything compressed.
				if (used > vec.size() * sparseRatio) {
					// Write dense
					bytes = encodeDense(vec);
				}
				else {
					// Write sparse
					flags |= FLAG_SPARSE;
					bytes = encodeSparse(vec);
				}

				to.writeInt(flags);
				to.writeInt(bytes.length); // Compressed size in bytes
				to.write(bytes); // actual compressed data

//				System.out.println("SV-Binding: wrote length "+(index.length * 12)+" as "+(bytes.length));
			}
			else {
				// Store sparse without compression
				int flags = 0;
				flags |= FLAG_SPARSE;
				to.writeInt(flags);

				SparseVector sv = asSparse(vec);
				int[] index = sv.getIndex();
				double[] data = sv.getData();

				to.writeInt(index.length); // Vector data size
				for (int i = 0; i < index.length; i++) {
					to.writeInt(index[i]);
				}
				for (int i = 0; i < index.length; i++) {
					to.writeDouble(data[i]);
				}
//				stored++;
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
//		finally {
////			System.out.println("Stored in "+(System.currentTimeMillis()-start));
//			if ((count % 10) == 0) {
//				System.out.println("Count "+count+" stored "+stored+" sparse "+sparse+" dense "+dense);
//			}
//		}
	}

	private OutputStream getCompressingOutputStream(OutputStream aOs)
		throws IOException
	{
		switch (compressionMethod) {
		case FLAG_GZIP:
			return new GZIPOutputStream(aOs);
		case FLAG_BZIP2:
			return new CBZip2OutputStream(aOs);
		default:
			throw new IllegalStateException("Unknown compression method");
		}
	}

	private InputStream getDeCompressingOutputStream(int flags, InputStream aIs)
		throws IOException
	{
		switch ((flags & MASK_COMPRESSION)) {
		case FLAG_GZIP:
			return new GZIPInputStream(aIs);
		case FLAG_BZIP2:
			return new CBZip2InputStream(aIs);
		default:
			throw new IllegalStateException("Unknown compression method");
		}
	}

	private byte[] encodeDense(Vector vec) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStream zout = getCompressingOutputStream(bos);
		DirectDataOutputStream out = new DirectDataOutputStream(zout);
		DenseVector dv = asDense(vec);
		double[] dvd = dv.getData();
		out.writeInt(dvd.length); // Vector data size (to compressed data)
		out.writeDoubles(dvd, 0, dvd.length);
//		dense++;
		out.close(); // This does not close the underlying stream, only flush it...
		zout.close();
		return bos.toByteArray();
	}

	private byte[] encodeSparse(Vector vec) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStream zout = getCompressingOutputStream(bos);
		DirectDataOutputStream out = new DirectDataOutputStream(zout);
		SparseVector sv = asSparse(vec);
		int[] index = sv.getIndex();
		double[] data = sv.getData();
		out.writeInt(index.length); // Vector data size (to compressed data)
		out.writeInts(index, 0, index.length);
		out.writeDoubles(data, 0, index.length);
//		sparse++;
		out.close(); // This does not close the underlying stream, only flush it...
		zout.close();
		return bos.toByteArray();
	}

	private DenseVector asDense(Vector aVec)
	{
		if (aVec instanceof DenseVector) {
			return (DenseVector) aVec;
		}
		else {
			return new DenseVector(aVec);
		}
	}

	private SparseVector asSparse(Vector aVec)
	{
		if (aVec instanceof SparseVector) {
			return (SparseVector) aVec;
		}
		else if (aVec instanceof DenseVector ) {
			// This is more efficient than the strategy implemented in AbstractVector, which is used
			// as a fall-back when creating a SparseVector from anything that is not a SparseVector.
			DenseVector dv = (DenseVector) aVec;

			// Fast non-zero calculation. This is faster than using Matrices.cardinality(dv) which
			// uses many calls to VectorEntry.get() and RefVectorIterator.
//			int nz = Matrices.cardinality(dv);
			int nz = 0;
			for (double d : dv.getData()) {
				if (d != 0.0) {
					nz++;
				}
			}

			int[] outIndex = new int[nz];
			double[] outData = new double[nz];
			double[] inData = dv.getData();

			int iOut = 0;
			for (int iIn = 0; iIn < inData.length; iIn++) {
				if (inData[iIn] == 0) {
					continue;
				}

				outIndex[iOut] = iIn;
				outData[iOut] = inData[iIn];
				iOut++;
			}

			return new SparseVector(dv.size(), outIndex, outData, false);
		}
		else {
			return new SparseVector(aVec);
		}
	}

	private static final class DirectDataOutputStream
		extends ImageOutputStreamImpl
        implements Closeable
	{
		private OutputStream os;

		public DirectDataOutputStream(OutputStream aOs)
		{
			os = aOs;
		}

		@Override
		public void write(int aB)
			throws IOException
		{
			os.write(aB);
		}

		@Override
		public void write(byte[] aB, int aOff, int aLen)
			throws IOException
		{
			os.write(aB, aOff, aLen);
		}

		@Override
		public int read()
			throws IOException
		{
			throw new UnsupportedOperationException("read not supported");
		}

		@Override
		public int read(byte[] aB, int aOff, int aLen)
			throws IOException
		{
			throw new UnsupportedOperationException("read not supported");
		}

        @Override
        public void close()
            throws IOException
        {
            super.close();
            if (os != null) {
                os.close();
            }
        }
	}

	private static final class DirectDataInputStream
		extends ImageInputStreamImpl
        implements Closeable
	{
		private InputStream is;

		public DirectDataInputStream(InputStream aIs)
		{
			is = aIs;
		}

		@Override
		public int read()
			throws IOException
		{
			return is.read();
		}

		@Override
		public int read(byte[] aB, int aOff, int aLen)
			throws IOException
		{
			return is.read(aB, aOff, aLen);
		}

        @Override
        public void close()
            throws IOException
        {
            super.close();
            if (is != null) {
                is.close();
            }
        }
	}
}
