package de.tudarmstadt.ukp.similarity.dkpro.resource;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasure;

/**
 * This resource wraps any parameter-free {@link JCasTextSimilarityMeasure}}.
 */
public class SimpleJCasTextSimilarityResource
	extends JCasTextSimilarityResourceBase
{
    public static final String PARAM_TEXT_SIMILARITY_MEASURE = "TextSimilarityMeasure";
	@ConfigurationParameter(name=PARAM_TEXT_SIMILARITY_MEASURE, mandatory=true)
	private String textSimilarityMeasureName;
	
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier,
    		Map<String, Object> aAdditionalParams)
    	throws ResourceInitializationException
    {
    	if (!super.initialize(aSpecifier, aAdditionalParams)) {
	        return false;
	    }
    	
    	this.mode = TextSimilarityResourceMode.jcas;

    	try{
    		measure = (JCasTextSimilarityMeasure) Class.forName(textSimilarityMeasureName).newInstance();
        }
        catch (InstantiationException e) {
            throw new ResourceInitializationException(e);
        }
        catch (IllegalAccessException e) {
            throw new ResourceInitializationException(e);
        }
        catch (ClassNotFoundException e) {
            throw new ResourceInitializationException(e);
        }

	    return true;
    }
}
