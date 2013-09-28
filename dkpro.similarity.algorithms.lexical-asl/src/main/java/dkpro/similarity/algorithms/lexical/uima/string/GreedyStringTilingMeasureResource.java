package dkpro.similarity.algorithms.lexical.uima.string;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.lexical.string.GreedyStringTiling;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;

public class GreedyStringTilingMeasureResource
	extends TextSimilarityResourceBase
{
	public static final String PARAM_MIN_MATCH_LENGTH = "MinMatchLength";
	@ConfigurationParameter(name=PARAM_MIN_MATCH_LENGTH, mandatory=true)
	private int minMatchLength;
	
    @SuppressWarnings("unchecked")
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }
        
        this.mode = TextSimilarityResourceMode.text;
        
		measure = new GreedyStringTiling(minMatchLength);
        
        return true;
    }
}
