/*******************************************************************************
 * Copyright 2011, 2012
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
 ******************************************************************************/
package dkpro.similarity.uima.resource.test;

import java.util.Collection;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;


public class TestSimilarityResource
    extends TextSimilarityResourceBase
{

    @SuppressWarnings("unchecked")
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }
        
        measure = new TestMeasure();
        
        return true;
    }
    
    class TestMeasure extends TextSimilarityMeasureBase {

        @Override
        public double getSimilarity(Collection<String> stringList1, Collection<String> stringList2)
            throws SimilarityException
        {
            int size1 = 0;
            for (String item : stringList1) {
                size1 += item.length();
            }

            int size2 = 0;
            for (String item : stringList2) {
                size2 += item.length();
            }
            return 1.0 - (double) Math.abs(size1 - size2) / Math.max(size1, size2);
        }        
    }
}