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
package dkpro.similarity.algorithms.lsr.aggregate;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;
import dkpro.similarity.algorithms.util.Cache;


/**
 * Implements the aggregation strategy by
 * <a href="http://www.cse.unt.edu/~rada/papers/mihalcea.aaai06.pdf">Mihalcea et al. (2006)</a>. 
 *
 */
public class MCS06AggregateComparator
	extends TextSimilarityMeasureBase
{
	private TextSimilarityMeasure measure;
	private Map<String,Double> idfValues;
	private Cache<Set<String>,Double> cache;
	
	/**
	 * The constructor ideally should take a _Term_SimilarityMeasure as parameter,
	 * not a _Text_SimilarityMeasure. However, as the LSR-based comparators are
	 * implemented as the latter, we stick to that here, but use only the word-
	 * relatedness function.   
	 * @param measure The word similarity measure to use.
	 * @throws IOException 
	 */
	public MCS06AggregateComparator(TextSimilarityMeasure measure, File idfValuesFile)
		throws IOException
	{
		initialize(measure);
		
		// Read idf values
		idfValues = new HashMap<String,Double>();
		
		for (String line : (List<String>) FileUtils.readLines(idfValuesFile))
		{
			if (line.length() > 0)
			{
				String[] cols = line.split("\t");
				idfValues.put(cols[0], Double.parseDouble(cols[1]));
			}
		}
	}
	
	public MCS06AggregateComparator(TextSimilarityMeasure measure, Map<String,Double> idfValues)
	{
		initialize(measure);
		
		this.idfValues = idfValues;
	}
	
	private void initialize(TextSimilarityMeasure measure)
	{
		this.measure = measure;
		this.cache = new Cache<Set<String>,Double>(50000);
	}
	
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		return 0.5 * (getDirectionalRelatedness(stringList1, stringList2) + 
					  getDirectionalRelatedness(stringList2, stringList1));
	}
	
	private double getDirectionalRelatedness(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		double weightedSum = 0.0;
		double idfSum = 0.0; 
	
		for (String w1 : stringList1)
		{
			try
			{
				w1 = w1.toLowerCase();
			}
			catch (NullPointerException e)
			{
				// Ignore
				continue;
			}
			
			Set<Double> subscores = new HashSet<Double>();
			
			for (String w2 : stringList2)
			{
				try
				{
					w2 = w2.toLowerCase();
	
					Set<String> wordset = new HashSet<String>();
					wordset.add(w1);
					wordset.add(w2);
					
					double score;
					if (cache.containsKey(wordset)) {
						score = cache.get(wordset);
					} else { 
						score = measure.getSimilarity(w1, w2);
						cache.put(wordset, score);
					}
					
					subscores.add(score);
				}
				catch (NullPointerException e)
				{
					// Ignore
				}
			}
			
			// Get best score for the pair (w1, w2)
			double bestSubscore = 0.0;
			
			if (stringList2.size() > 0)
			{
				if (measure.isDistanceMeasure()) {
                    bestSubscore = Collections.min(subscores);
                }
                else {
                    bestSubscore = Collections.max(subscores);
                }
				
				// Handle error cases such as "not found"
				if (bestSubscore < 0.0)
				{
					bestSubscore = 0.0;
				}
			}
			
			// Weight
			double weightedScore;
			if (idfValues.containsKey(w1))
			{
				weightedScore = bestSubscore * idfValues.get(w1);
				
				weightedSum += weightedScore;
				idfSum += idfValues.get(w1);
			}
			else
			{
				// Well, ignore this token.
				//System.out.println("Ignoring token: \"" + w1 + "\"");
			}
		}
		
		return weightedSum / idfSum;
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName() + "_" + measure.getName();
	}
	
	public TextSimilarityMeasure getSubmeasure()
	{
		return measure;
	}
}
