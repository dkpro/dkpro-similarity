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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.ngrams.CharacterNGramMeasure;
   

public class CharacterNGramMeasureTest
{
	private static final double epsilon = 0.001;
	
    @Test
    public void test()
		throws Exception
	{
    	Map<String,Double> idfValues = new HashMap<String,Double>();
    	idfValues.put("the", 0.01);
    	idfValues.put("ick", 0.3);
    	idfValues.put("fox", 2.5);
    	idfValues.put("dog", 2.1);
    	idfValues.put("laz", 0.28);
    	idfValues.put("azy", 0.27);
    	
		String a1 = "The quick brown fox jumps over the lazy dog.";
		String a2 = "The quick brown dog jumps.";
		
		String b1 = "The quick brown fox jumps over the lazy dog.";
		String b2 = "The quick brown fox jumps.";

		TermSimilarityMeasure measure = new CharacterNGramMeasure(3, idfValues);

		assertEquals(0.117, measure.getSimilarity(a1, a2), epsilon);
		assertEquals(0.988, measure.getSimilarity(b1, b2), epsilon);
   }
}
