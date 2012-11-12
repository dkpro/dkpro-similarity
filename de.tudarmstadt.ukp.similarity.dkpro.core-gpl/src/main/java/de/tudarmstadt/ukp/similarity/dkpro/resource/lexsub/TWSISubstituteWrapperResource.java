package de.tudarmstadt.ukp.similarity.dkpro.resource.lexsub;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ExternalResource;

import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexsub.TWSISubstituteWrapper;
import de.tudarmstadt.ukp.similarity.dkpro.resource.JCasTextSimilarityResourceBase;


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
	{
		super.afterResourcesInitialized();
		
		measure = new TWSISubstituteWrapper(textSimilarityMeasure);
	}
}
