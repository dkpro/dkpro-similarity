package dkpro.similarity.uima.resource.style;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.style.TokenRatioComparator;
import dkpro.similarity.uima.resource.JCasTextSimilarityResourceBase;


public class TokenRatioResource
	extends JCasTextSimilarityResourceBase
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        this.mode = TextSimilarityResourceMode.jcas;
        
        measure = new TokenRatioComparator();
        
        return true;
    }
}
