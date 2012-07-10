package de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.ngrams;

import java.io.IOException;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.CharacterNGramMeasure;
import de.tudarmstadt.ukp.similarity.dkpro.resource.TextSimilarityResourceBase;


public class CharacterNGramResource
	extends TextSimilarityResourceBase
{
	public static final String PARAM_N = "N";
	@ConfigurationParameter(name=PARAM_N, mandatory=true)
	private int n;
	
	public static final String PARAM_IDF_VALUES_FILE = "IdfValuesFile";
	@ConfigurationParameter(name=PARAM_IDF_VALUES_FILE, mandatory=true)
	private String idfValuesFile;
	
	@SuppressWarnings("unchecked")
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        try {
            measure = new CharacterNGramMeasure(n, idfValuesFile);
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        
        return true;
    }
}
