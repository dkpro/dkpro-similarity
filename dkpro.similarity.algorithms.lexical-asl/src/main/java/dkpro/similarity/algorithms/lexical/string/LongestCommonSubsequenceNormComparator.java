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
 * Computes the similarity between two strings based on the longest
 * common subsequence. The implementation normalizes by the first
 * string, as proposed by Clough and Stevenson (2011).
 * 
 * Paul Clough and Mark Stevenson. 2011. In Language Resources and
 * Evaluation: Special Issue on Plagiarism and Authorship Analysis,
 * 45(1):5-24.
 */
public class LongestCommonSubsequenceNormComparator
	extends LongestCommonSubsequenceComparator
{
	@Override
	public double getSimilarity(String string1, String string2)
		throws SimilarityException
	{
		String lcs = getLCS(string1.toLowerCase(), string2.toLowerCase());
		
		return (double) lcs.length() / string1.length();
	}
}
