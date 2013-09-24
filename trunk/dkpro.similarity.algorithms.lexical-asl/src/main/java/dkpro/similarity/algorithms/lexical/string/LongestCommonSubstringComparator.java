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
import dkpro.similarity.algorithms.lexical.string.util.gst.GeneralizedSuffixTree;

public class LongestCommonSubstringComparator
	extends TextSimilarityMeasureBase
{
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
	    return getSimilarity(StringUtils.join(stringList1, " "), StringUtils.join(stringList2, " "));
	}
	
	/**
	 * Considers both parameters to be full texts, not individual terms.
	 * 
	 * Implementation based on generalized suffix trees:
	 * http://illya-keeplearning.blogspot.com/search/label/suffix%20tree
	 * 
	 * Direct download:
	 * http://illya.yolasite.com/resources/suffix-tree-6.zip
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
	
	private String getLCS(String string1, String string2)
	{
		GeneralizedSuffixTree gst = new GeneralizedSuffixTree(string1, string2);
		return gst.getLcsAsString();
	}

}
