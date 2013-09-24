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

public class LongestCommonSubsequenceComparator
	extends TextSimilarityMeasureBase
{	

	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
	    return getSimilarity(StringUtils.join(stringList1,  " "), StringUtils.join(stringList2,  " "));
	}
	
	/**
	 * Considers both parameters to be full texts, not individual terms.
	 * 
	 * Code taken from:
	 * http://introcs.cs.princeton.edu/96optimization/LCS.java.html
	 */
	@Override
	public double getSimilarity(String string1, String string2)
		throws SimilarityException
	{
		String lcs = getLCS(string1, string2);
		
		// Normalize
		double numerator = string1.length() + string2.length() - 2 * lcs.length();
		double denominator = string1.length() + string2.length(); 
		double score = 1.0 - (numerator / denominator);
		
		return score;
	}
	
	protected String getLCS(String string1, String string2)
	{
		String x = string1;
        String y = string2;
        int M = x.length();
        int N = y.length();

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[M+1][N+1];

        // compute length of LCS and all subproblems via dynamic programming
        for (int i = M-1; i >= 0; i--) {
            for (int j = N-1; j >= 0; j--) {
                if (x.charAt(i) == y.charAt(j)) {
                    opt[i][j] = opt[i+1][j+1] + 1;
                }
                else {
                    opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
                }
            }
        }

        // recover LCS itself and print it to standard output
        StringBuffer sb = new StringBuffer();
        
        int i = 0, j = 0;
        while(i < M && j < N) {
            if (x.charAt(i) == y.charAt(j))
            {
            	sb.append(x.charAt(i));
                i++;
                j++;
            }
            else if (opt[i+1][j] >= opt[i][j+1]) {
                i++;
            }
            else {
                j++;
            }
        }
        
        return sb.toString();
	}
}
