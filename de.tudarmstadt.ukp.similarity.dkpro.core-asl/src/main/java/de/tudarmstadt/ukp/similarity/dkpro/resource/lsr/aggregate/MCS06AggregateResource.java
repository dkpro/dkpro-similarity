package de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.aggregate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lsr.aggregate.MCS06AggregateComparator;
import de.tudarmstadt.ukp.similarity.dkpro.resource.TextSimilarityResourceBase;

public class MCS06AggregateResource
	extends TextSimilarityResourceBase
{
	public static final String PARAM_TERM_SIMILARITY_RESOURCE = "TermSimilarityMeasure";
	@ConfigurationParameter(name=PARAM_TERM_SIMILARITY_RESOURCE, mandatory=true)
	private TextSimilarityMeasure termSimilarityMeasure;
	
	public static final String PARAM_IDF_VALUES_FILE = "IdfValuesFile";
	@ConfigurationParameter(name=PARAM_IDF_VALUES_FILE, mandatory=true)
	private File idfValuesFile;
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map<String, Object> aAdditionalParams)
		throws ResourceInitializationException
	{
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
	        return false;
	    }
		
		try
		{
			measure = new MCS06AggregateComparator(termSimilarityMeasure, idfValuesFile);
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		
		return true;
	}
}
