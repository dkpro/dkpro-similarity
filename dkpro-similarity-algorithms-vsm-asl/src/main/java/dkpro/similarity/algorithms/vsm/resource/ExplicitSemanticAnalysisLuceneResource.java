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
package dkpro.similarity.algorithms.vsm.resource;

import java.io.File;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.vsm.InnerVectorProduct;
import dkpro.similarity.algorithms.vsm.VectorAggregation;
import dkpro.similarity.algorithms.vsm.VectorComparator;
import dkpro.similarity.algorithms.vsm.VectorNorm;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeIdf;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeTf;
import dkpro.similarity.algorithms.vsm.store.LuceneVectorReader;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;

/**
 * This class is a replacement for @ExplicitSemanticAnalysisResource in case you want to work with the lucene index directly.
 * @author erbs@ukp.informatik.tu-darmstadt.de
 *
 */
public class ExplicitSemanticAnalysisLuceneResource
extends TextSimilarityResourceBase
{

    public static final String PARAM_LUCENE_PATH = "LucenePath";
    @ConfigurationParameter(name=PARAM_LUCENE_PATH, mandatory=true)
    private String lucenePath;

    public static final String PARAM_LUCENE_VECTOR_AGGREGATION = "VectorAggregation";
    @ConfigurationParameter(name=PARAM_LUCENE_VECTOR_AGGREGATION, mandatory=true, defaultValue="CENTROID")
    private VectorAggregation vectorAggregation;

    public static final String PARAM_LUCENE_VECTOR_NORM = "VectorNorm";
    @ConfigurationParameter(name=PARAM_LUCENE_VECTOR_NORM, mandatory=true, defaultValue="L2")
    private VectorNorm vectorNorm;

    public static final String PARAM_LUCENE_WEIGHTING_MODE_TF = "WeightingModeTf";
    @ConfigurationParameter(name=PARAM_LUCENE_WEIGHTING_MODE_TF, mandatory=true, defaultValue="normalized")
    private WeightingModeTf weightingModeTf;

    public static final String PARAM_LUCENE_WEIGHTING_MODE_IDF = "WeightingModeIdf";
    @ConfigurationParameter(name=PARAM_LUCENE_WEIGHTING_MODE_IDF, mandatory=true, defaultValue="constantOne")
    private WeightingModeIdf weightingModeIdf;

    public static final String PARAM_ESA_INNER_VECTOR_PRODUCT = "EsaInnerVectorProduct";
    @ConfigurationParameter(name=PARAM_ESA_INNER_VECTOR_PRODUCT, mandatory=true, defaultValue="AVERAGE_PRODUCT")
    private InnerVectorProduct innerVectorProduct;

    public static final String PARAM_ESA_NORMALIZATION = "EsaNormalization";
    @ConfigurationParameter(name=PARAM_ESA_NORMALIZATION, mandatory=true, defaultValue="NONE")
    private VectorNorm esaVectorNorm;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
            throws ResourceInitializationException
            {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }


        LuceneVectorReader wikipediaReader = new LuceneVectorReader(new File(lucenePath));
        wikipediaReader.setVectorAggregation(vectorAggregation);
        wikipediaReader.setNorm(vectorNorm);
        wikipediaReader.setWeightingModeTf(weightingModeTf);
        wikipediaReader.setWeightingModeIdf(weightingModeIdf);
        wikipediaReader.setWeightingThreshold(0.0f);
        wikipediaReader.setVectorLengthThreshold(1.0f);
        VectorComparator measure = new VectorComparator(wikipediaReader);
        measure.setInnerProduct(innerVectorProduct);
        measure.setNormalization(vectorNorm); // Vectors already normalized by vector reader

        this.measure = measure;
        
        mode = TextSimilarityResourceMode.text;
        
        return true;
            }

    @Override
    public double getSimilarity(String string1, String string2)
        throws SimilarityException
    {
        double similarity = super.getSimilarity(string1, string2);
        if(similarity<0){
            similarity=0;
        }
//        if(similarity>0){
//            System.out.println(string1 +"\t" + string2 +"\t" + similarity);
//        }
//        System.out.printf("%-12s %-12s %.2f %n",
//                string1,
//                string2,
//        super.getSimilarity(string1, string2));

        return similarity;
    }

}
