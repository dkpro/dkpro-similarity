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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.vsm.NormalizedGoogleDistanceLikeComparator;
import dkpro.similarity.algorithms.vsm.VectorAggregation;
import dkpro.similarity.algorithms.vsm.VectorNorm;
import dkpro.similarity.algorithms.vsm.store.LuceneVectorReader;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeIdf;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeTf;

public class NormalizedGoogleDistanceLikeComparatorTest
{

    private static final double EPSILON = 0.00001;
    
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
    
    @Test
    public void ngdComparatorTest() throws SimilarityException {
        LuceneVectorReader vSrc = new LuceneVectorReader(new File("src/test/resources/vsm/test_index_token"));
        vSrc.setVectorAggregation(VectorAggregation.CENTROID);
        vSrc.setWeightingThreshold(0.0f);
        vSrc.setVectorLengthThreshold(0.0f);
        vSrc.setWeightingModeTf(WeightingModeTf.binary);
        vSrc.setWeightingModeIdf(WeightingModeIdf.constantOne);
        vSrc.setNorm(VectorNorm.NONE);

        NormalizedGoogleDistanceLikeComparator ngdComparator = new NormalizedGoogleDistanceLikeComparator(vSrc);
        
        assertEquals(0.0, ngdComparator.getSimilarity("another", "another"), EPSILON);
        assertEquals(0.63093, ngdComparator.getSimilarity("another", "example"), EPSILON);
        assertEquals(1.0, ngdComparator.getSimilarity("example", "sentence"), EPSILON);
        assertEquals(Double.POSITIVE_INFINITY, ngdComparator.getSimilarity("another", "just"), EPSILON);        
    }
}
