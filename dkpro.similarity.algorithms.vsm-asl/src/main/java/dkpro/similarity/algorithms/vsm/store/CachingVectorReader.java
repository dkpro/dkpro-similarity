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
package dkpro.similarity.algorithms.vsm.store;

import no.uib.cipr.matrix.Vector;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.vsm.util.StringKeyCache;

/**
 * Adds caching to a {@link VectorReader}.
 *
 * @author Richard Eckart de Castilho
 */
public class CachingVectorReader
	extends VectorReader
{
	private final VectorReader source;
	private final StringKeyCache<Vector> vectorCache;

	public CachingVectorReader(VectorReader aSource, int aCapacity)
	{
		source = aSource;
		vectorCache = new StringKeyCache<Vector>(aCapacity);
	}

	public StringKeyCache<Vector> getVectorCache()
	{
		return vectorCache;
	}

	public VectorReader getSource()
	{
		return source;
	}

	@Override
	public String getId()
	{
		return source.getId();
	}

	@Override
	public void close()
	{
		source.close();
	}

	@Override
	public int getConceptCount()
		throws SimilarityException
	{
		return source.getConceptCount();
	}

	@Override
	public Vector getVector(String aTerm)
		throws SimilarityException
	{
		Vector v = getVectorCache().get(aTerm);
		if (v != null) {
			return v;
		}

		v = getSource().getVector(aTerm);

		if (v != null) {
			getVectorCache().put(aTerm, v);
		}

		return v;
	}
}
