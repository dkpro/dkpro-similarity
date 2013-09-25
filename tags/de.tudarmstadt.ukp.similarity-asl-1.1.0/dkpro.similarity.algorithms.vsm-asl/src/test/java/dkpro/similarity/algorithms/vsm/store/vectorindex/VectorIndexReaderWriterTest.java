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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;
import no.uib.cipr.matrix.sparse.SparseVector;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import dkpro.similarity.algorithms.vsm.store.vectorindex.VectorIndexReader;
import dkpro.similarity.algorithms.vsm.store.vectorindex.VectorIndexWriter;

public class VectorIndexReaderWriterTest
{
	@Test
	public void vectorBehaviour()
		throws Exception
	{
		SparseVector vi = new SparseVector(1000);
		vi.set(10, 0.1);
		assertEquals(1, vi.getUsed());
		assertEquals(1, vi.getData().length);
	}


	@Test
	public void test()
		throws Exception
	{
		// Test sparse sparse vector
		SparseVector vi1 = new SparseVector(1000);
		vi1.set(10, 0.1);
		testVector(vi1);

		// Test sparse dense vector
		DenseVector vi3 = new DenseVector(1000);
		vi3.set(10, 0.1);
		testVector(vi3);

		// Test full sparse vector
		SparseVector vi2 = new SparseVector(1000);
		for (int i = 0; i < vi2.size(); i++) {
			vi2.set(i, 0.1);
		}
		testVector(vi2);

		// Test sparse dense vector
		DenseVector vi4 = new DenseVector(1000);
		for (int i = 0; i < vi4.size(); i++) {
			vi4.set(i, 0.1);
		}
		testVector(vi4);
}

	private void testVector(Vector aVector)
		throws Exception
	{
		File path = new File("target/test");
		FileUtils.deleteQuietly(path);
		path.mkdirs();

		String term = "DaveApiWebService#DrmRulesGet";

		VectorIndexWriter writer = new VectorIndexWriter(path, aVector.size());
		writer.put(term, aVector);
		writer.close();

		VectorIndexReader reader = new VectorIndexReader(path);
		Vector vo = reader.getVector(term);
		reader.close();

		assertNotNull(vo);

		vo.add(-1, aVector);

		assertEquals(0, vo.norm(Norm.TwoRobust), 0.00001);
	}
}
