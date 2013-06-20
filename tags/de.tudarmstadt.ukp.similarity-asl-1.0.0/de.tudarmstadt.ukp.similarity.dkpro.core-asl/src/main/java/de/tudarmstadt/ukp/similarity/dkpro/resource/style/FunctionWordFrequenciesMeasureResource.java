package de.tudarmstadt.ukp.similarity.dkpro.resource.style;

import java.io.IOException;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.similarity.algorithms.style.FunctionWordFrequenciesMeasure;
import de.tudarmstadt.ukp.similarity.dkpro.resource.JCasTextSimilarityResourceBase;


public class FunctionWordFrequenciesMeasureResource
	extends JCasTextSimilarityResourceBase
{
	public static final String PARAM_FUNCTION_WORD_LIST_LOCATION = "FunctionWordListLocation";
	@ConfigurationParameter(name=PARAM_FUNCTION_WORD_LIST_LOCATION, mandatory=false)
	private String functionWordListLocation;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        this.mode = TextSimilarityResourceMode.jcas;
        
        try {
            if (functionWordListLocation != null) {
                measure = new FunctionWordFrequenciesMeasure(functionWordListLocation);
            }
            else {
                measure = new FunctionWordFrequenciesMeasure();
            }
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        
        return true;
    }
}
