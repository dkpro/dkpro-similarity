package dkpro.similarity.algorithms.structure.uima;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.structure.TokenPairDistanceMeasure;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;


public class TokenPairDistanceResource
	extends TextSimilarityResourceBase
{
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        this.mode = TextSimilarityResourceMode.list;
        
        try {
            measure = new TokenPairDistanceMeasure();
        }
        catch (NumberFormatException e) {
            throw new ResourceInitializationException(e);
        }
        
        return true;
    }
}
