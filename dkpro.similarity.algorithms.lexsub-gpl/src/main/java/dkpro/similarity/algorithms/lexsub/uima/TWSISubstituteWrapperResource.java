package dkpro.similarity.algorithms.lexsub.uima;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.fit.descriptor.ExternalResource;

import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexsub.TWSISubstituteWrapper;
import dkpro.similarity.uima.resource.JCasTextSimilarityResourceBase;


public class TWSISubstituteWrapperResource
	extends JCasTextSimilarityResourceBase
{
	public static final String PARAM_TEXT_SIMILARITY_RESOURCE = "TextSimilarityMeasure";
	@ExternalResource(key=PARAM_TEXT_SIMILARITY_RESOURCE)
	private TextSimilarityMeasure textSimilarityMeasure;

	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map<String, Object> aAdditionalParams)
		throws ResourceInitializationException
	{
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
	        return false;
	    }
		
		this.mode = TextSimilarityResourceMode.jcas;

		return true;
	}
	
	@Override
	public void afterResourcesInitialized()
		throws ResourceInitializationException
	{
		super.afterResourcesInitialized();
		
		measure = new TWSISubstituteWrapper(textSimilarityMeasure);
	}
}
