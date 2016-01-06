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
package dkpro.similarity.algorithms.structure;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Computes the overlap of part-of-speech n-grams using the
 * containment measure, similarly as for the word n-grams
 * in the {@code WordNGramContainmentMeasure}. 
 */
public class PosNGramContainmentMeasure
	extends PosNGramJaccardMeasure
{
	public PosNGramContainmentMeasure(int n)
	{
		super(n);
	}
	
	@Override
	protected double getNormalizedSimilarity(
			Set<List<String>> suspiciousNGrams, Set<List<String>> originalNGrams)
	{
		// Compare using the Containment measure (Broder, 1997)
		Set<List<String>> commonNGrams = new HashSet<List<String>>();
		commonNGrams.addAll(suspiciousNGrams);
		commonNGrams.retainAll(originalNGrams);
				
		double norm = suspiciousNGrams.size();
		double sim = 0.0;
		
		if (norm > 0.0)
			sim = commonNGrams.size() / norm;
		
		return sim;
	}
}
