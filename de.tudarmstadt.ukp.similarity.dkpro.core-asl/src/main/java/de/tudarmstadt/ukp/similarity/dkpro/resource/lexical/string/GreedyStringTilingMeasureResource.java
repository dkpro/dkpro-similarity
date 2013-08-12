package de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.string;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.GreedyStringTiling;
import de.tudarmstadt.ukp.similarity.dkpro.resource.TextSimilarityResourceBase;

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
