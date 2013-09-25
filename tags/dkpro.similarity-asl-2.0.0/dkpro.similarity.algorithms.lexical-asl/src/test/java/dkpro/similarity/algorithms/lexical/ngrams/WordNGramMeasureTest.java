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
package dkpro.similarity.algorithms.lexical.ngrams;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
   

public class WordNGramMeasureTest
{
	private static final double epsilon = 0.001;
	
    @Test
    public void jaccardTest()
		throws Exception
	{
    	Map<String,Double> idfValues = new HashMap<String,Double>();
    	idfValues.put("the", 0.01);
    	idfValues.put("ick", 0.3);
    	idfValues.put("fox", 2.5);
    	idfValues.put("dog", 2.1);
    	idfValues.put("laz", 0.28);
    	idfValues.put("azy", 0.27);
    	
		List<String> l1 = new ArrayList<String>();
		l1.add("The");
		l1.add("quick");
		l1.add("brown");
		l1.add("fox");
		l1.add("jumps");
		l1.add("over");
		l1.add("the");
		l1.add("lazy");
		l1.add("dog");
		
		List<String> l2 = new ArrayList<String>();
		l2.add("The");
		l2.add("quick");
		l2.add("brown");
		l2.add("fox");
		l2.add("jumps");
		l2.add("over");
		l2.add("the");
		l2.add("lazy");
		l2.add("dog");
		
		List<String> l3 = new ArrayList<String>();
		l3.add("The");
		l3.add("quick");
		l3.add("brown");
		l3.add("dog");
		l3.add("jumps");

		TextSimilarityMeasure measure = new WordNGramJaccardMeasure(3);

		assertEquals(1.0, measure.getSimilarity(l1, l2), epsilon);
		assertEquals(0.111, measure.getSimilarity(l1, l3), epsilon);
   }
    
    @Test
    public void containmentTest()
		throws Exception
	{
    	Map<String,Double> idfValues = new HashMap<String,Double>();
    	idfValues.put("the", 0.01);
    	idfValues.put("ick", 0.3);
    	idfValues.put("fox", 2.5);
    	idfValues.put("dog", 2.1);
    	idfValues.put("laz", 0.28);
    	idfValues.put("azy", 0.27);
    	
		List<String> l1 = new ArrayList<String>();
		l1.add("The");
		l1.add("quick");
		l1.add("brown");
		l1.add("fox");
		l1.add("jumps");
		l1.add("over");
		l1.add("the");
		l1.add("lazy");
		l1.add("dog");
		
		List<String> l2 = new ArrayList<String>();
		l2.add("The");
		l2.add("quick");
		l2.add("brown");
		l2.add("fox");
		l2.add("jumps");
		l2.add("over");
		l2.add("the");
		l2.add("lazy");
		l2.add("dog");
		
		List<String> l3 = new ArrayList<String>();
		l3.add("The");
		l3.add("quick");
		l3.add("brown");
		l3.add("dog");
		l3.add("jumps");

		TextSimilarityMeasure measure = new WordNGramContainmentMeasure(3);

		assertEquals(1.0, measure.getSimilarity(l1, l2), epsilon);
		assertEquals(0.142, measure.getSimilarity(l1, l3), epsilon);
   }
}
