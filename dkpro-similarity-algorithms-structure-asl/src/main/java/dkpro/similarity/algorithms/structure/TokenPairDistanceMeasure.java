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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

/**
 * This measure corresponds to the Composite Feature "Distance" as described in:
 * 
 * ﻿Hatzivassiloglou, V., Klavans, J., & Eskin, E. (1999)
 * Detecting text similarity over short passages: Exploring linguistic feature
 * combinations via machine learning. Proceedings of the Joint SIGDAT Conference
 * on Empirical Methods in Natural Language Processing and Very Large Corpora. 
 * 
 * Two feature vector of distances are computed, one for each text. Then they
 * are compared using Pearson correlation. The correlation score is returned
 * as similarity score.
 */
public class TokenPairDistanceMeasure
	extends TextSimilarityMeasureBase
{	
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		
		// Transform input lists into lowercase string lists
		List<String> sl1 = new LinkedList<String>();
		for (String s : stringList1) {
            sl1.add(s.toLowerCase());
        }
		
		List<String> sl2 = new LinkedList<String>();
		for (String s : stringList2) {
            sl2.add(s.toLowerCase());
        }	
		
		// Get word sets
		Set<String> strings1 = new LinkedHashSet<String>(sl1);		
		Set<String> strings2 = new LinkedHashSet<String>(sl2);
		
		// Get a common word list
		List<String> commonStrings = new LinkedList<String>(strings1);
		commonStrings.retainAll(strings2);
		
		// Build up pairs (ignoring order, i.e. a-b or b-a)
		Set<Pair> pairs = new LinkedHashSet<Pair>();
		for (String s1 : commonStrings) {
			for (String s2 : commonStrings)
			{
				if (!s1.equals(s2))
				{
					Pair p = new Pair(s1,s2);
					pairs.add(p);
				}
			}
		}
		
		if (pairs.size() > 1)
		{		
			double[] v1 = new double[pairs.size()];
			double[] v2 = new double[pairs.size()];
			
			List<Pair> pairList = new LinkedList<Pair>(pairs);
			
			for (int i = 0; i < pairList.size(); i++)
			{
				Pair p = pairList.get(i);
				
				int idx1a = sl1.indexOf(p.getString1());
				int idx1b = sl1.indexOf(p.getString2());
				int idx1diff = transform(idx1a - idx1b);
				
				int idx2a = sl2.indexOf(p.getString1());
				int idx2b = sl2.indexOf(p.getString2());
				int idx2diff = transform(idx2a - idx2b);
				
				v1[i] = idx1diff;
				v2[i] = idx2diff;
			}
			
			PearsonsCorrelation pearson = new PearsonsCorrelation();
			return pearson.correlation(v1, v2);
		}

		return 0.0;
	}
	
	public int transform(int diff)
	{
		// Pass through
		return diff;
	}
	
	private class Pair
	{
		String s1;
		String s2;
		
		public Pair(String s1, String s2)
		{
			this.s1 = s1;
			this.s2 = s2;
		}
		
		@Override
		public int hashCode()
		{
			return s1.hashCode() + s2.hashCode();
		}
		
		
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) {
				return true;
			}
            if (obj == null) {
                return false;
            }
			if (this.getClass().equals(obj.getClass()))
			{
				Pair otherObj = (Pair)obj;
				if ((s1.equals(otherObj.getString1()) && s2.equals(otherObj.getString2())) || 
					(s2.equals(otherObj.getString1()) && s1.equals(otherObj.getString2())))
				{
					return true;
				}
			}
			return false;
		}
		
		public String getString1()
		{
			return s1;
		}
		
		public String getString2()
		{
			return s2;
		}
	}
}
