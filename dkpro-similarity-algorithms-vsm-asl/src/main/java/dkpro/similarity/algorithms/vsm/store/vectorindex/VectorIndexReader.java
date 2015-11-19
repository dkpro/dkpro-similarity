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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

import no.uib.cipr.matrix.Vector;

import org.apache.commons.io.IOUtils;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.DatabaseException;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.vsm.store.VectorReader;

/**
 * Reads an ESA vector index.
 *
 * @author Richard Eckart de Castilho
 */
public class VectorIndexReader
	extends VectorReader
	implements VectorIndexContract
{
	private final File path;
	private BerkeleyDbEnvironment dbEnv;
	private BerkeleyDbDatabase db;
	private long dbCacheSize = DEFAULT_DB_CACHE;
	private int nConcepts;

	public VectorIndexReader(File aPath)
	{
		path = aPath;
	}

	@Override
	public String getId()
	{
		return path.getAbsolutePath();
	}

	@Override
	public Vector getVector(String aTerm)
		throws SimilarityException
	{
		try {
			return (Vector) getDb().get(aTerm);
		}
		catch (DatabaseException e) {
			throw new SimilarityException(e);
		}
	}

	@Override
	public int getConceptCount()
		throws SimilarityException
	{
		getDb();
		return nConcepts;
	}

	@Override
	public void close()
	{
		if (db != null) {
			db.close();
		}

		if (dbEnv != null) {
			dbEnv.close();
		}
	}

	public void setDbCacheSize(long aDbCacheSize)
	{
		dbCacheSize = aDbCacheSize;
	}

	public Iterator<String> getTermIterator()
		throws SimilarityException
	{
		return getDb().keyIterator();
	}

	public int getNConcepts()
		throws SimilarityException
	{
		getDb();
		return nConcepts;
	}

	private BerkeleyDbDatabase getDb()
		throws SimilarityException
	{
		if (db == null) {
			Reader in = null;
			try {
				Properties props = new Properties();
				in = new InputStreamReader(new FileInputStream(new File(path, CONFIG_FILE_NAME)), CONFIG_FILE_ENCODING);
				props.load(in);
				nConcepts = Integer.parseInt(props.getProperty(CONFIG_PROP_N_CONCEPTS));
			}
			catch (IOException e) {
				throw new SimilarityException(e);
			}
			finally {
				IOUtils.closeQuietly(in);
			}

			dbEnv = new BerkeleyDbEnvironment(path.getAbsolutePath(), true, false, dbCacheSize);

			// init conceptVectorIndex
			TupleBinding<?> binding = new VectorBinding(nConcepts);
			db = new BerkeleyDbDatabase(dbEnv, CONCEPT_VECTOR_DB_NAME, true, false, binding);
		}
		return db;
	}
}
