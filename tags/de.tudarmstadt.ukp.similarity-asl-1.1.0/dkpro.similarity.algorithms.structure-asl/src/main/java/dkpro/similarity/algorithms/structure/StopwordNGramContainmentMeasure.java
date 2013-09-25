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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;


/**
 * This comparator models the stopword n-gram containment measure
 * as described in
 * 
 * E. Stamatatos: Plagiarism Detection Using Stopword n-grams,
 * in: Journal of the American Society for Information Science
 * and Technology, 2011.
 *
 */
public class StopwordNGramContainmentMeasure
	extends TextSimilarityMeasureBase
{
	int n;
	List<String> stopwords;
	
	public StopwordNGramContainmentMeasure(int n, String stopwordList)
		throws IOException
	{
		this.n = n;
	
        stopwords = new ArrayList<String>();
        InputStream is = null;
        try {
            URL url = ResourceUtils.resolveLocation(stopwordList, this, null);
            is = url.openStream();
            String content = IOUtils.toString(is, "UTF-8");
            for (String line : Arrays.asList(content.split("\n"))) {
                if (line.length() > 0)
                    stopwords.add(line);
            }
        }
        finally{
            IOUtils.closeQuietly(is);
        }
	}
	
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		// Get n-grams (retain stopwords only)
		Set<List<String>> ngrams1 = getNGrams(retainStopwords(stringList1));
		Set<List<String>> ngrams2 = getNGrams(retainStopwords(stringList2));
		
		// Compare using the containment measure (Broder, 1997)
		Set<List<String>> commonNGrams = new HashSet<List<String>>();
		commonNGrams.addAll(ngrams1);
		commonNGrams.retainAll(ngrams2);
		
		double norm = Math.max(ngrams1.size(), ngrams2.size());
		double sim = 0.0;
		
		if (norm > 0.0)
			sim = commonNGrams.size() / norm;
		
		return sim;
	}
	
	private List<String> retainStopwords(Collection<String> stringList)
	{
		List<String> inputTokens = Arrays.asList(stringList.toArray(new String[] {}));
		List<String> outputTokens = new ArrayList<String>();
		
		for (String token : inputTokens)
		{
			if (stopwords.contains(token))
				outputTokens.add(token);
		}
		
		return outputTokens;
		
	}
	
	private Set<List<String>> getNGrams(List<String> stringList)
	{
		Set<List<String>> ngrams = new HashSet<List<String>>();
		
		for (int i = 0; i < stringList.size() - (n - 1); i++)
		{
			// Generate n-gram at index i
			List<String> ngram = new ArrayList<String>();
			for (int k = 0; k < n; k++)
			{
				String token = stringList.get(i + k).toLowerCase();
				ngram.add(token);
			}
			
			// Add
			ngrams.add(ngram);
		}
		
		return ngrams;
	}

	@Override
	public String getName()
	{
		return this.getClass().getSimpleName() + "_" + n + "grams";
	}
}
