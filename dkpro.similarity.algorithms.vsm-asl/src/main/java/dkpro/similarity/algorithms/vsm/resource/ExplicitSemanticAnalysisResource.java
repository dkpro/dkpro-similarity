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
import dkpro.similarity.algorithms.vsm.store.vectorindex.VectorIndexReader;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;

public class ExplicitSemanticAnalysisResource
extends TextSimilarityResourceBase
{

    public static final String PARAM_ESA_PATH = "EsaPath";
    @ConfigurationParameter(name=PARAM_ESA_PATH, mandatory=true)
    private String esaPath;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
            throws ResourceInitializationException
            {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        VectorIndexReader vectorIndexReader = new VectorIndexReader(new File(esaPath));
        vectorIndexReader.setVectorAggregation(VectorAggregation.CENTROID);

        VectorComparator measure = new VectorComparator(vectorIndexReader);
        measure.setInnerProduct(InnerVectorProduct.AVERAGE_PRODUCT);
        measure.setNormalization(VectorNorm.NONE);
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
        return similarity;
    }

}
