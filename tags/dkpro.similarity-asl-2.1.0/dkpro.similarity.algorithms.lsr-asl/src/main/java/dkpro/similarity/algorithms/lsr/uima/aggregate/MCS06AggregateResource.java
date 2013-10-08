package dkpro.similarity.algorithms.lsr.uima.aggregate;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lsr.aggregate.MCS06AggregateComparator;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;


public class MCS06AggregateResource
	extends TextSimilarityResourceBase
{
	public static final String PARAM_TERM_SIMILARITY_RESOURCE = "TermSimilarityMeasure";
	@ExternalResource(key=PARAM_TERM_SIMILARITY_RESOURCE)
	private TextSimilarityMeasure termSimilarityMeasure;
	
	public static final String PARAM_IDF_VALUES_FILE = "IdfValuesFile";
	@ConfigurationParameter(name=PARAM_IDF_VALUES_FILE, mandatory=true)
	private File idfValuesFile;
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map<String, Object> aAdditionalParams)
		throws ResourceInitializationException
	{
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
	        return false;
	    }
		
		this.mode = TextSimilarityResourceMode.list;

		return true;
	}
	
	@Override
	public void afterResourcesInitialized()
	        throws ResourceInitializationException
	{
		super.afterResourcesInitialized();
		
		try {
			measure = new MCS06AggregateComparator(termSimilarityMeasure, idfValuesFile);
		}
		catch (IOException e) {
			System.err.println("Term similarity measure could not be initialized!");
			e.printStackTrace();
		}
	}
}
