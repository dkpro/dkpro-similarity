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

import dkpro.similarity.algorithms.vsm.store.LuceneVectorReader;
import dkpro.similarity.algorithms.vsm.store.VectorReader;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeIdf;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeTf;


/**
 * Convenience comparator that uses the more flexible {@link VectorComparator}<br/>
 * in order to reimplement ESA as closely as possible.
 * 
 * <pre>
 * Evgeniy Gabrilovich and Shaul Markovitch.
 * Computing semantic relatedness using Wikipedia-based Explicit Semantic Analysis.
 * In: Proceedings of the 20th International Joint Conference on Artifical Intelligence (IJCAI), pp. 1606-1611, 2007.
 * </pre>
 * 
 * Note that the original implementation of ESA uses sliding window pruning which is not implemented here.
 * Thus, results may vary a bit.
 * The performance of ESA also depends heavily on the underlying resource, as shown in:
 *
 * <pre>
 * Torsten Zesch and Christof Müller and Iryna Gurevych.
 * Using Wiktionary for Computing Semantic Relatedness.
 * In: Proceedings of the 23rd AAAI Conference on Artificial Intelligence, p. 861-867, July 2008. 
 * </pre>
 * 
 * @author zesch
 *
 */
public class ExplicitSemanticAnalysisComparator
    extends VectorComparator
{

    public ExplicitSemanticAnalysisComparator(VectorReader aIndex)
    {
        super(aIndex);
        
        // if the vector reader is not a lucence vector reader, we cannot set all the parameters
        //   to come as close to the original ESA implementation as possible
        if (!(aIndex instanceof LuceneVectorReader)) {
            throw new RuntimeException(
                    "VectorReader is not an instance of LuceneVectorReader. " +
                    "If you want to use ESA with other readers, please use it directly with a VectorComparator. "
            );
        }

        initialize((LuceneVectorReader) aIndex);

        this.setInnerProduct(InnerVectorProduct.COSINE);
        this.setNormalization(VectorNorm.NONE); // Vectors already normalized by vector reader
    }

    public ExplicitSemanticAnalysisComparator(VectorReader aIndexA, VectorReader aIndexB)
    {
        super(aIndexA, aIndexB);
    }
    
    private void initialize(LuceneVectorReader aIndex) {
        aIndex.setVectorAggregation(VectorAggregation.CENTROID);
        aIndex.setNorm(VectorNorm.L2);
        aIndex.setWeightingModeTf(WeightingModeTf.normalized);
        aIndex.setWeightingModeIdf(WeightingModeIdf.constantOne);
        aIndex.setWeightingThreshold(0.0f);
        aIndex.setVectorLengthThreshold(1.0f);
    }
}