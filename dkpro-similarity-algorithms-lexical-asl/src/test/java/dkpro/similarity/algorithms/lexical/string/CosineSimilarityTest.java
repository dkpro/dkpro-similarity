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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
   
public class CosineSimilarityTest {

    private static final double epsilon = 0.0001;

    @Test
    public void tfBinaryL2()
		throws Exception
	{
		String[] a1 = "test String1".split(" ");
		String[] a2 = "test String2".split(" ");

		String[] b1 = "This is my string".split(" ");
		String[] b2 = "That should be your string".split(" ");

		TextSimilarityMeasure comparator = new CosineSimilarity(
		        CosineSimilarity.WeightingModeTf.BINARY,
		        CosineSimilarity.NormalizationMode.L2
		);

		assertEquals(0.5,    comparator.getSimilarity(a1, a2), epsilon);
		assertEquals(0.2236, comparator.getSimilarity(b1, b2), epsilon);

//		// Also compare with SimMetrics
//		// Note: SimMetrics Cosine is case-sensitive!
//		TermSimilarityMeasure simMetricsComparator = new CosineSimMetricComparator();
//
//		assertEquals(simMetricsComparator.getRelatedness(a1.toLowerCase(), a2.toLowerCase()), comparator.getRelatedness(a1, a2), epsilon);
//		assertEquals(simMetricsComparator.getRelatedness(b1, b2), comparator.getRelatedness(b1, b2), epsilon);
	}
    
    @Test
    public void tfBinaryL2Character()
        throws Exception
    {
        String a1 = "test String1";
        String a2 = "test String2";

        String b1 = "This is my string";
        String b2 = "That should be your string";

        TermSimilarityMeasure comparator = new CosineSimilarity(
                CosineSimilarity.WeightingModeTf.BINARY,
                CosineSimilarity.NormalizationMode.L2
        );

        assertEquals(0.89999,    comparator.getSimilarity(a1, a2), epsilon);
        assertEquals(0.73127, comparator.getSimilarity(b1, b2), epsilon);
    }

	@Test
	public void tfFrequencyL1L2()
		throws Exception
	{
		String[] a1 = "test String1".split(" ");
		String[] a2 = "test String2".split(" ");

		String[] b1 = "test String1 test".split(" ");
		String[] b2 = "string2 test".split(" ");

		TextSimilarityMeasure comparator = new CosineSimilarity(
		        CosineSimilarity.WeightingModeTf.FREQUENCY,
		        CosineSimilarity.NormalizationMode.L1
        );

		assertEquals(0.25,   comparator.getSimilarity(a1, a2), epsilon);
		assertEquals(0.3333, comparator.getSimilarity(b1, b2), epsilon);

        comparator = new CosineSimilarity(
                CosineSimilarity.WeightingModeTf.FREQUENCY,
                CosineSimilarity.NormalizationMode.L2
        );

		assertEquals(0.5,    comparator.getSimilarity(a1, a2), epsilon);
		assertEquals(0.6324, comparator.getSimilarity(b1, b2), epsilon);
	}


	@Test
	public void idfL2()
		throws Exception
	{
		Map<String,Double> idfScores = new HashMap<String,Double>();
		idfScores.put("test", 1.0 / 2.0);
		idfScores.put("String1", 1.0 / 2.0);
		idfScores.put("String2", 1.0 / 3.0);

		String[] a1 = "test String1".split(" ");
		String[] a2 = "test String2".split(" ");

		String[] b1 = "test String1 test".split(" ");
		String[] b2 = "String2 test".split(" ");

		TextSimilarityMeasure comparator = new CosineSimilarity(
				CosineSimilarity.WeightingModeTf.BINARY,
		        CosineSimilarity.WeightingModeIdf.PASSTHROUGH,
		        CosineSimilarity.NormalizationMode.L2,
                idfScores
        );

		assertEquals(0.5884, comparator.getSimilarity(a1, a2), epsilon);
		assertEquals(0.5884, comparator.getSimilarity(b1, b2), epsilon);
	}

	@Test
	public void tfidfL2()
		throws Exception
	{
		Map<String,Double> idfScores = new HashMap<String,Double>();
		idfScores.put("test", 1.0 / 2.0);
		idfScores.put("String1", 1.0 / 2.0);
		idfScores.put("String2", 1.0 / 3.0);

		String[] a1 = "test String1".split(" ");
		String[] a2 = "test String2".split(" ");

		String[] b1 = "test String1 test".split(" ");
		String[] b2 = "String2 test".split(" ");

		TextSimilarityMeasure comparator = new CosineSimilarity(
		        CosineSimilarity.WeightingModeTf.FREQUENCY,
		        CosineSimilarity.WeightingModeIdf.PASSTHROUGH,
		        CosineSimilarity.NormalizationMode.L2,
                idfScores
        );

		assertEquals(0.5883, comparator.getSimilarity(a1, a2), epsilon);
		assertEquals(0.7442, comparator.getSimilarity(b1, b2), epsilon);
	}
	
	@Test
	public void tfidfLogPlusOne()
		throws Exception
	{
		Map<String,Double> idfScores = new HashMap<String,Double>();
		idfScores.put("test", 1.0 / 2.0);
		idfScores.put("String1", 1.0 / 2.0);

		String[] a1 = "test String1".split(" ");
		String[] a2 = "test String2".split(" ");

		String[] b1 = "test String1 test".split(" ");
		String[] b2 = "String2 test".split(" ");

		TextSimilarityMeasure comparator1 = new CosineSimilarity(
		        CosineSimilarity.WeightingModeTf.FREQUENCY,
		        CosineSimilarity.WeightingModeIdf.LOG,
		        CosineSimilarity.NormalizationMode.L2,
                idfScores
        );

		TextSimilarityMeasure comparator2 = new CosineSimilarity(
		        CosineSimilarity.WeightingModeTf.FREQUENCY,
		        CosineSimilarity.WeightingModeIdf.LOGPLUSONE,
		        CosineSimilarity.NormalizationMode.L2,
                idfScores
        );

		assertEquals(0.499999, comparator1.getSimilarity(a1, a2), epsilon);
		assertEquals(0.499999, comparator2.getSimilarity(a1, a2), epsilon);
		assertEquals(0.632455, comparator1.getSimilarity(b1, b2), epsilon);
		assertEquals(0.632455, comparator2.getSimilarity(b1, b2), epsilon);
		
		// not test with "String2" in the map
		idfScores.put("String2", 1.0/2.0);
		assertEquals(0.499999, comparator1.getSimilarity(a1, a2), epsilon);
		assertEquals(0.499999, comparator2.getSimilarity(a1, a2), epsilon);
		assertEquals(0.632455, comparator1.getSimilarity(b1, b2), epsilon);
		assertEquals(0.632455, comparator2.getSimilarity(b1, b2), epsilon);		
	}
	
	@Test
	public void precisionTest() throws Exception {
	    CosineSimilarity measure = new CosineSimilarity();
	    List<String> tokens1 = Arrays.asList("This is a sentence with seven tokens".split(" "));    
	    List<String> tokens2 = Arrays.asList("This is a sentence with seven tokens".split(" "));    

	    assertEquals(1.0, measure.getSimilarity(tokens1, tokens2), 0.00000000000000001);
	          
	    List<String> tokens3 = Arrays.asList("This is a sentence which results in an invalid cosine similarity score .");     
        List<String> tokens4 = Arrays.asList("This is a sentence which results in an invalid cosine similarity score .");     

        assertEquals(1.0, measure.getSimilarity(tokens3, tokens4), 0.00000000000000001);
	}
	
    @Test
    public void test() throws Exception {
        CosineSimilarity measure = new CosineSimilarity();

        List<String> tokens1 = Arrays.asList("1 3 4 5 6 7 8 9 3 10 7 11 .".split(" "));    
        List<String> tokens2 = Arrays.asList("2 3 12 13 5 3 7 11 14 15 3 7 .".split(" "));    

        assertEquals(0.688033, measure.getSimilarity(tokens1, tokens2), 0.000001);
    }
    
    @Test
    public void emptyListTest() throws Exception {
        CosineSimilarity measure = new CosineSimilarity();

        List<String> tokens1 = new ArrayList<String>();    
        List<String> tokens2 = new ArrayList<String>();    

        assertEquals(1.0, measure.getSimilarity(tokens1, tokens2), 0.000001);
    }
}