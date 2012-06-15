package de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.resource;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.api.resource.TextSimilarityResourceBase;


public class WordNGramResource
	extends TextSimilarityResourceBase
{
	public static final String PARAM_TEXT_SIMILARITY_MEASURE = "TextSimilarityMeasure";
	@ConfigurationParameter(name=PARAM_TEXT_SIMILARITY_MEASURE, mandatory=true)
	private String textSimilarityMeasureName;
	
	public static final String PARAM_N = "N";
	@ConfigurationParameter(name=PARAM_N, mandatory=true)
	private String nValue;
	private int n;
	
	@SuppressWarnings("unchecked")
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }
        
        try {
        	n = Integer.parseInt(nValue);     	
			measure = (TextSimilarityMeasure) Class.forName(textSimilarityMeasureName).
				getConstructor(new Class[] { Integer.TYPE }).
				newInstance(n);
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
		catch (IllegalArgumentException e) {
			throw new ResourceInitializationException(e);
		}
		catch (SecurityException e) {
			throw new ResourceInitializationException(e);
		}
		catch (InvocationTargetException e) {
			throw new ResourceInitializationException(e);
		}
		catch (NoSuchMethodException e) {
			throw new ResourceInitializationException(e);
		}
        
        return true;
    }
}
