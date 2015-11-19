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


public abstract class TermSimilarityMeasureBase
	implements TermSimilarityMeasure
{
	@Override
	public void beginMassOperation()
	{
		// Per default do nothing
	}

	@Override
	public void endMassOperation()
	{
		// Per default do nothing
	}

	protected static Double preScore(String term1, String term2)
	{
		if (term1.length() == 0 || term2 == null || term2.length() == 0) {
			return NOT_FOUND;
		}

		if (term1 == term2 || term1.equals(term2)) {
			return EQUALITY_SCORE;
		}

		return null;
	}

	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	public boolean isDistanceMeasure()
	{
		return false;
	}
}
