package dkpro.similarity.uima.resource.structure;

import java.io.IOException;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.structure.StopwordNGramContainmentMeasure;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;


public class StopwordNGramContainmentMeasureResource
	extends TextSimilarityResourceBase
{
	public static final String PARAM_STOPWORD_LIST_LOCATION = "StopwordListLocation";
	@ConfigurationParameter(name=PARAM_STOPWORD_LIST_LOCATION, mandatory=true)
	private String stopwordListLocation;
	
    public static final String PARAM_N = "N";
    @ConfigurationParameter(name=PARAM_N, mandatory=true)
    private String nString;

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
            int n = Integer.parseInt(nString); 
            measure = new StopwordNGramContainmentMeasure(n, stopwordListLocation);
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        catch (NumberFormatException e) {
            throw new ResourceInitializationException(e);
        }
        
        return true;
    }
}
