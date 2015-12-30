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

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.structure.StopwordNGramContainmentMeasure;

public class StopwordNGramContainmentMeasureTest
{
	static final double epsilon = 0.001; 
	
	@Test
	public void run()
		throws Exception
	{
		List<String> doc1 = new LinkedList<String>();
		List<String> doc2 = new LinkedList<String>();
		
		doc1.add("the");
		doc1.add("quick");
		doc1.add("will");
		doc1.add("fox");
		doc1.add("be");
		doc1.add("over");
		doc1.add("i");
		doc1.add("lazy");
		doc1.add("dog");
		
		doc2.add("the");
		doc2.add("quick");
		doc2.add("brown");
		doc2.add("will");
		doc2.add("jumps");
		doc2.add("be");
		doc2.add("the");
		doc2.add("i");
		doc2.add("this");
		
		// [the, will, be]
		// [will, be, i]
		
		// [the, will, be]
		// [will, be, the]
		// [be, the, i]
		// [the, i, this]
		
		TextSimilarityMeasure comparator = new StopwordNGramContainmentMeasure(3, "classpath:/stopwords/stopwords-bnc-stamatatos.txt");
		assertEquals(0.25, comparator.getSimilarity(doc1, doc2), epsilon);
	}
}