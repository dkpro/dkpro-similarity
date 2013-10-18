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

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;


/**
 * SecondString comparator fails for some texts. Used the implementation from
 * http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
 */
public class LevenshteinComparator
    extends TextSimilarityMeasureBase
{
    public LevenshteinComparator()
    {
    	// nothing yet
    }

	@Override
	public double getSimilarity(String s1, String s2)
		throws SimilarityException
	{
		return computeLevenshteinDistance(s1, s2);
	}
	
	 private static int minimum(int a, int b, int c) {
         return Math.min(Math.min(a, b), c);
	 }
	
	 private int computeLevenshteinDistance(CharSequence str1, CharSequence str2)
	 {
         int[][] distance = new int[str1.length() + 1][str2.length() + 1];

         for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
         for (int j = 0; j <= str2.length(); j++) {
            distance[0][j] = j;
        }

         for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                distance[i][j] = minimum(
                                 distance[i - 1][j] + 1,
                                 distance[i][j - 1] + 1,
                                 distance[i - 1][j - 1]
                                                 + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                                                 : 1));
            }
        }

         return distance[str1.length()][str2.length()];
	 }

	@Override
	public double getSimilarity(Collection<String> strings1, Collection<String> strings2)
		throws SimilarityException
	{
        if (strings1.size() == 0 || strings2.size() == 0) {
            return 0.0;
        }

        String concatenatedString1 = StringUtils.join(strings1, " ");
        String concatenatedString2 = StringUtils.join(strings2, " ");

        if (concatenatedString1.length() == 0 || concatenatedString2.length() == 0) {
            return 0.0;
        }

        double distance = getSimilarity(concatenatedString1,  concatenatedString2);

        return distance;
    }
}