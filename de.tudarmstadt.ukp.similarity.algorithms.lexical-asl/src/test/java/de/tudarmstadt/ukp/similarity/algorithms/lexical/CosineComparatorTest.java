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
package de.tudarmstadt.ukp.similarity.algorithms.lexical;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.tudarmstadt.ukp.similarity.algorithms.api.TermSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.CosineSimilarity;
   
public class CosineComparatorTest {

    private static final double epsilon = 0.0001;

    @Test
    public void tfBinaryL2()
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

		assertEquals(0.5,    comparator.getSimilarity(a1, a2), epsilon);
		assertEquals(0.2236, comparator.getSimilarity(b1, b2), epsilon);

//		// Also compare with SimMetrics
//		// Note: SimMetrics Cosine is case-sensitive!
//		TermRelatednessMeasure simMetricsComparator = new CosineSimMetricComparator();
//
//		assertEquals(simMetricsComparator.getRelatedness(a1.toLowerCase(), a2.toLowerCase()), comparator.getRelatedness(a1, a2), epsilon);
//		assertEquals(simMetricsComparator.getRelatedness(b1, b2), comparator.getRelatedness(b1, b2), epsilon);
	}

	@Test
	public void tfFrequencyL1L2()
		throws Exception
	{
		String a1 = "test String1";
		String a2 = "test String2";

		String b1 = "test String1 test";
		String b2 = "string2 test";

		TermSimilarityMeasure comparator = new CosineSimilarity(
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

		String a1 = "test String1";
		String a2 = "test String2";

		String b1 = "test String1 test";
		String b2 = "String2 test";

		TermSimilarityMeasure comparator = new CosineSimilarity(
		        CosineSimilarity.WeightingModeIdf.PASSTHROUGH,
		        CosineSimilarity.NormalizationMode.L2,
                idfScores
        );

		assertEquals(0.5883, comparator.getSimilarity(a1, a2), epsilon);
		assertEquals(0.5883, comparator.getSimilarity(b1, b2), epsilon);
	}

	@Test
	public void tfidfL2()
		throws Exception
	{
		Map<String,Double> idfScores = new HashMap<String,Double>();
		idfScores.put("test", 1.0 / 2.0);
		idfScores.put("String1", 1.0 / 2.0);
		idfScores.put("String2", 1.0 / 3.0);

		String a1 = "test String1";
		String a2 = "test String2";

		String b1 = "test String1 test";
		String b2 = "String2 test";

		TermSimilarityMeasure comparator = new CosineSimilarity(
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

		String a1 = "test String1";
		String a2 = "test String2";

		String b1 = "test String1 test";
		String b2 = "String2 test";

		TermSimilarityMeasure comparator1 = new CosineSimilarity(
		        CosineSimilarity.WeightingModeTf.FREQUENCY,
		        CosineSimilarity.WeightingModeIdf.LOG,
		        CosineSimilarity.NormalizationMode.L2,
                idfScores
        );

		TermSimilarityMeasure comparator2 = new CosineSimilarity(
		        CosineSimilarity.WeightingModeTf.FREQUENCY,
		        CosineSimilarity.WeightingModeIdf.LOGPLUSONE,
		        CosineSimilarity.NormalizationMode.L2,
                idfScores
        );

		assertEquals(Double.NaN, comparator1.getSimilarity(a1, a2), epsilon);
		assertEquals(0.89442d,   comparator2.getSimilarity(b1, b2), epsilon);
	}
}
