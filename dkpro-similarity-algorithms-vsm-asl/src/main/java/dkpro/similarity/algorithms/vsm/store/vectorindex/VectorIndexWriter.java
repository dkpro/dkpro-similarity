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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import no.uib.cipr.matrix.Vector;

import org.apache.commons.io.IOUtils;

import com.sleepycat.bind.tuple.TupleBinding;

/**
 * Writes an ESA vector index.
 *
 * @author Richard Eckart de Castilho
 */
public class VectorIndexWriter
	implements VectorIndexContract
{
	private File path;
	private BerkeleyDbEnvironment dbEnv;
	private BerkeleyDbDatabase db;
	private long dbCacheSize = DEFAULT_DB_CACHE;
	private int nConcepts;

	public VectorIndexWriter(File aPath, int aNConcepts)
	{
		path = aPath;
		nConcepts = aNConcepts;
	}

	public void setDbCacheSize(long aDbCacheSize)
	{
		dbCacheSize = aDbCacheSize;
	}

	public void put(String aTerm, Vector aVector)
		throws IOException
	{
		if (aVector == null || aTerm == null || aTerm.length() == 0) {
			return;
		}
		// It is important to cast the term to Object here, otherwise the wrong method is called
		getDb().put(aTerm, aVector);
	}

	public int getConceptCount()
	{
		return nConcepts;
	}

	public void close()
	{
		if (db != null) {
			db.close();
		}

		if (dbEnv != null) {
			dbEnv.close();
		}
	}

	private BerkeleyDbDatabase getDb()
		throws IOException
	{
		if (db == null) {
			dbEnv = new BerkeleyDbEnvironment(path.getAbsolutePath(), false, true, dbCacheSize);

			// init conceptVectorIndex
			TupleBinding<?> binding = new VectorBinding(nConcepts);
			db = new BerkeleyDbDatabase(dbEnv, CONCEPT_VECTOR_DB_NAME, false, true, binding);

			Writer out = null;
			try {
				Properties props = new Properties();
				props.setProperty(CONFIG_PROP_N_CONCEPTS, String.valueOf(nConcepts));
				out = new OutputStreamWriter(new FileOutputStream(new File(path, CONFIG_FILE_NAME)),
						CONFIG_FILE_ENCODING);
				props.store(out, null);
			}
			finally {
				IOUtils.closeQuietly(out);
			}
		}
		return db;
	}
}
