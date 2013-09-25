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
package dkpro.similarity.algorithms.vsm;

import static dkpro.similarity.algorithms.api.TermSimilarityMeasure.NOT_FOUND;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.vsm.InnerVectorProduct;
import dkpro.similarity.algorithms.vsm.VectorAggregation;
import dkpro.similarity.algorithms.vsm.VectorComparator;
import dkpro.similarity.algorithms.vsm.VectorNorm;
import dkpro.similarity.algorithms.vsm.store.LuceneVectorReader;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeIdf;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeTf;

public class VectorComparatorLuceneVectorSourceTest {

    // The test index:
    //       \ docId |  0  |  1  |  2  |
    //        \      |
    //   term  \     |  tf |  tf |  tf | df
    //   ---------------------------------------------------------
    //   another     |  0  |  1  |  0  | 1
    //   example     |  1  |  1  |  0  | 2
    //   funny       |  0  |  1  |  1  | 2
    //   just        |  0  |  0  |  1  | 1
    //   sentence    |  1  |  1  |  1  | 3

    private static final double EPSILON = 0.00001;

    private static final String term1 = "example";
    private static final String term2 = "sentence";
    private static final String term3 = "funny";

    ///////////////////////////////////
    // COSINE
    ///////////////////////////////////

    private TermSimilarityMeasure getComparator(float aWeightingThreshold, float aVectorLengthThreshold,
    	WeightingModeTf aWeightingModeTf, WeightingModeIdf aWeightingModeIdf, InnerVectorProduct aInnerProduct,
    	VectorNorm aNorm)
    {
		LuceneVectorReader vSrc = new LuceneVectorReader(new File(
				"src/test/resources/vsm/test_index_token"));
		vSrc.setVectorAggregation(VectorAggregation.CENTROID);
    	vSrc.setWeightingThreshold(aWeightingThreshold);
    	vSrc.setVectorLengthThreshold(aVectorLengthThreshold);
    	vSrc.setWeightingModeTf(aWeightingModeTf);
    	vSrc.setWeightingModeIdf(aWeightingModeIdf);
    	vSrc.setNorm(aNorm);

    	VectorComparator cmp = new VectorComparator(vSrc);
    	cmp.setInnerProduct(aInnerProduct);
    	cmp.setNormalization(VectorNorm.NONE);
    	return cmp;
    }

    @Test
    public void testEsa_cosine_Normal_Normal_L2_0_0() throws Exception {
        TermSimilarityMeasure c = getComparator(
				0.0f,
				0.0f,
				WeightingModeTf.normal,
				WeightingModeIdf.normal,
				InnerVectorProduct.COSINE,
				VectorNorm.L2);

        assertEquals(0.816497,  c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.5,       c.getSimilarity(term1, term3), EPSILON);
        assertEquals(NOT_FOUND, c.getSimilarity("humbegrumpf", "xfagagh"), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_cosine_Log_Log_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.log,
                WeightingModeIdf.log,
				InnerVectorProduct.COSINE,
				VectorNorm.L2);

        assertEquals(0.0, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.0, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_cosine_LogPlusOne_Log_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.logPlusOne,
                WeightingModeIdf.log,
				InnerVectorProduct.COSINE,
				VectorNorm.L2);

        assertEquals(0.0, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.5, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_cosine_bin_bin_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.binary,
                WeightingModeIdf.binary,
				InnerVectorProduct.COSINE,
				VectorNorm.L2);

        assertEquals(0.816497, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.5,      c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_cosine_normal_normal_L2_07_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.7f,
                0.0f,
                WeightingModeTf.normal,
                WeightingModeIdf.normal,
				InnerVectorProduct.COSINE,
				VectorNorm.L2);

        assertEquals(0.0, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.5, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_cosine_normal_normal_L2_0_1() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.01f,
                WeightingModeTf.normal,
                WeightingModeIdf.normal,
				InnerVectorProduct.COSINE,
				VectorNorm.L2);

        assertEquals(0.0, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.0, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    ///////////////////////////////////
    // LESK
    ///////////////////////////////////

    @Test
    public void testEsa_lesk_Normal_Normal_L1_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.normal,
                WeightingModeIdf.normal,
				InnerVectorProduct.LESK_OVERLAP,
				VectorNorm.L1);

        assertEquals(1.666666, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(1.0,      c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_lesk_Log_Log_L1_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.log,
                WeightingModeIdf.log,
				InnerVectorProduct.LESK_OVERLAP,
				VectorNorm.L2);

        assertEquals(0.0, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.0, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    @Ignore("See bug 159")
    public void testEsa_lesk_LogPlusOne_Log_L1_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.logPlusOne,
                WeightingModeIdf.log,
				InnerVectorProduct.LESK_OVERLAP,
				VectorNorm.L2);

        assertEquals(0.0, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.5, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_lesk_bin_bin_L1_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.binary,
                WeightingModeIdf.binary,
				InnerVectorProduct.LESK_OVERLAP,
				VectorNorm.L2);

        assertEquals(2.56891, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(1.41421, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    ///////////////////////////////////
    // MinOverlap
    ///////////////////////////////////

    @Test
    public void testEsa_minOverlap_Normal_Normal_L1_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.normal,
                WeightingModeIdf.normal,
        		InnerVectorProduct.MIN_OVERLAP,
        		VectorNorm.L1);

        assertEquals(0.666666, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.5,      c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_minOverlap_Log_Log_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.log,
                WeightingModeIdf.log,
        		InnerVectorProduct.MIN_OVERLAP,
        		VectorNorm.L2);

        assertEquals(0.0, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.0, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_minOverlap_LogPlusOne_Log_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.logPlusOne,
                WeightingModeIdf.log,
        		InnerVectorProduct.MIN_OVERLAP,
        		VectorNorm.L2);

        assertEquals(0.0,     c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.70711, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_minOverlap_bin_bin_L1_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.binary,
                WeightingModeIdf.binary,
        		InnerVectorProduct.MIN_OVERLAP,
        		VectorNorm.L1);

        assertEquals(0.666666, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.5,      c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    ///////////////////////////////////
    // AvgProd
    ///////////////////////////////////

    @Test
    public void testEsa_avgProd_Normal_Normal_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.normal,
                WeightingModeIdf.normal,
        		InnerVectorProduct.AVERAGE_PRODUCT,
        		VectorNorm.L2);

        assertEquals(0.52438, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.35355, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_avgProd_Log_Log_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.log,
                WeightingModeIdf.log,
        		InnerVectorProduct.AVERAGE_PRODUCT,
        		VectorNorm.L2);

        assertEquals(0.0, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.0, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_avgProd_LogPlusOne_Log_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.logPlusOne,
                WeightingModeIdf.log,
        		InnerVectorProduct.AVERAGE_PRODUCT,
        		VectorNorm.L2);

        assertEquals(0.0,     c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.35355, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    ///////////////////////////////////
    // LM
    ///////////////////////////////////

    @Test
    public void testEsa_LM_Normal_Normal_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.normal,
                WeightingModeIdf.normal,
        		InnerVectorProduct.LANGUAGE_MODEL,
        		VectorNorm.L2);

        assertEquals(0.833333, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.5,      c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_LM_Log_Log_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.log,
                WeightingModeIdf.log,
        		InnerVectorProduct.LANGUAGE_MODEL,
        		VectorNorm.L2);

        assertEquals(0.0, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.0, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

    @Test
    public void testEsa_LM_LogPlusOne_Log_L2_0_0() throws Exception {

        TermSimilarityMeasure c = getComparator(
                0.0f,
                0.0f,
                WeightingModeTf.logPlusOne,
                WeightingModeIdf.log,
        		InnerVectorProduct.LANGUAGE_MODEL,
        		VectorNorm.L2);

        assertEquals(0.5, c.getSimilarity(term1, term2), EPSILON);
        assertEquals(0.5, c.getSimilarity(term1, term3), EPSILON);
        testAlways(c);
    }

	private static void testAlways(TermSimilarityMeasure c)
		throws SimilarityException
	{
		// Not for all normalizations the self-comparison results in 1.0
		// See bug 692
		assertEquals("Self-comparison check failed", 1.0, c.getSimilarity(term1, term1), EPSILON);
		assertEquals("Commutativity check failed", c.getSimilarity(term1, term2), c.getSimilarity(term2, term1), EPSILON);
	}
}
