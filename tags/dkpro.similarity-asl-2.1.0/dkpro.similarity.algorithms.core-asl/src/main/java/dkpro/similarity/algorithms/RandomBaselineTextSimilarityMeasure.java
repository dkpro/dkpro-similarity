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
package dkpro.similarity.algorithms;

import java.util.Collection;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

/**
 * A random baseline measure which returns random scores in a
 * [0,1[ interval.
 */
public class RandomBaselineTextSimilarityMeasure
	extends TextSimilarityMeasureBase
{
	@Override
	public double getSimilarity(Collection<String> aDocument1,
			Collection<String> aDocument2)
		throws SimilarityException
	{
		return Math.random();
	}

	@Override
	public double getSimilarity(String aTerm1, String aTerm2)
		throws SimilarityException
	{
		return Math.random();
	}
}