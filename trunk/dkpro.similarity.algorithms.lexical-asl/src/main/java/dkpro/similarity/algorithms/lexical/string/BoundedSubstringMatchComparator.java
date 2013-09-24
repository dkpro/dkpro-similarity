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
package dkpro.similarity.algorithms.lexical.string;

import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * BoundedSubstringMatch:
 * Returns 1.0 if the two strings to compare are exactly equal or if on of the strings starts or ends with the other string. Each of the strings must be at least two characters long.
 * Returns 0.0 otherwise.
 * 
 * @author zesch
 *
 */
public class BoundedSubstringMatchComparator
	extends SimpleStringMatchComparator_ImplBase
{

	public BoundedSubstringMatchComparator()
		throws SimilarityException
	{
		this.strategy = SimpleStringMatchStrategy.BoundedSubstringMatch;
    }
}