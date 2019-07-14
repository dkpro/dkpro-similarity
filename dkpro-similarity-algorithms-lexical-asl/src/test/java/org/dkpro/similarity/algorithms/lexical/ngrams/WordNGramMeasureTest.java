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
package org.dkpro.similarity.algorithms.lexical.ngrams;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import org.junit.Test;
   
public class WordNGramMeasureTest
{
	private static final double epsilon = 0.001;
	
    @Test
    public void jaccardTest()
		throws Exception
	{
        List<String> l1 = asList("The quick brown fox jumps over the lazy dog".split(" "));
        List<String> l2 = asList("The quick brown fox jumps over the lazy dog".split(" "));
        List<String> l3 = asList("The quick brown dog jumps".split(" "));

		TextSimilarityMeasure measure = new WordNGramJaccardMeasure(3);

		assertEquals(1.0, measure.getSimilarity(l1, l2), epsilon);
		assertEquals(0.111, measure.getSimilarity(l1, l3), epsilon);
	}

    @Test
    public void containmentTest()
		throws Exception
	{
        List<String> l1 = asList("The quick brown fox jumps over the lazy dog".split(" "));
        List<String> l2 = asList("The quick brown fox jumps over the lazy dog".split(" "));
        List<String> l3 = asList("The quick brown dog jumps".split(" "));

		TextSimilarityMeasure measure = new WordNGramContainmentMeasure(3);

		assertEquals(1.0, measure.getSimilarity(l1, l2), epsilon);
		assertEquals(0.142, measure.getSimilarity(l1, l3), epsilon);
   }
}
