/*******************************************************************************
 * Copyright 2013
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
package dkpro.similarity.algorithms.api;

import static java.util.Arrays.asList;

public abstract class TextSimilarityMeasureBase
	extends TermSimilarityMeasureBase
	implements TextSimilarityMeasure
{
	@Override
	public double getSimilarity(final String aTerm1, final String aTerm2)
		throws SimilarityException
	{
		return getSimilarity(asList(aTerm1), asList(aTerm2));
	}
	
    @Override
    public double getSimilarity(final String[] strings1, final String[] strings2)
        throws SimilarityException
    {
        return getSimilarity(asList(strings1), asList(strings2));
    }
}
