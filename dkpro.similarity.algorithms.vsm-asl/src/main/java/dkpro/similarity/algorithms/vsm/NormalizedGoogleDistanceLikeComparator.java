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
package dkpro.similarity.algorithms.vsm;

import no.uib.cipr.matrix.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasureBase;
import dkpro.similarity.algorithms.vsm.store.VectorReader;


/**
 * Computes a distance value similar to the Normalized Google Distance (Cilibrasi & Vitanyi, 2004).

 * @author zesch
 *
 */
public class NormalizedGoogleDistanceLikeComparator
	extends TermSimilarityMeasureBase
{
	private final Log log = LogFactory.getLog(getClass());

    private final VectorReader sourceA;
    private final VectorReader sourceB;

	public NormalizedGoogleDistanceLikeComparator(VectorReader aIndex)
	{
		this(aIndex, aIndex);
	}

	public NormalizedGoogleDistanceLikeComparator(VectorReader aIndexA, VectorReader aIndexB)
	{
		sourceA = aIndexA;
		sourceB = aIndexB;
	}

	@Override
	public double getSimilarity(String term1, String term2)
		throws SimilarityException
	{
		Vector v1 = sourceA.getVector(term1);

		if (v1 == null) {
			// The first index usually contains the terms which have quite short concept
			// vectors. If a term concept vector is not found, there is no need to load
			// the heavy-weight document concept vector.
			if (log.isDebugEnabled()) {
				log.debug("Cutting short on term ["+term1+"] without concept vector");
			}
			return 0.0;
		}

		Vector v2 = sourceB.getVector(term2);
		if (v2 == null) {
			return 0.0;
		}

		long freq1 = 0;
		long freq2 = 0;
		long freq1and2 = 0;
		for (int i=0; i<v1.size(); i++) {
		    if (v1.get(i) > 0 && v2.get(i) > 0) {
		        freq1and2++;
                freq1++;
	            freq2++;
		    }
		    else if (v1.get(i) > 0) {
		        freq1++;
		    }
		    else if (v2.get(i) > 0) {
		        freq2++;
		    }
		}

		System.out.println(freq1);
        System.out.println(freq2);
        System.out.println(freq1and2);
		
		double logMax = Math.max(Math.log(freq1), Math.log(freq2));
		double logMin = Math.min(Math.log(freq1), Math.log(freq2));
		double logM = Math.log(v1.size());
		
        return (logMax - Math.log(freq1and2)) / (logM - logMin);
	}

	@Override
	public String getName()
	{
		return getClass().getSimpleName() + " (" + sourceA.getId() + ", " + sourceB.getId() + ")";
	}
}