package de.tudarmstadt.ukp.similarity.experiments.semeval2013.example;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.similarity.dkpro.resource.TextSimilarityResourceBase;


public class MyTextSimilarityResource
	extends TextSimilarityResourceBase
{
	public static final String PARAM_N = "N";
	@ConfigurationParameter(name=PARAM_N, mandatory=true)
	private int n;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier specifier, Map additionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(specifier, additionalParams)) {
            return false;
        }
        
        this.mode = TextSimilarityResourceMode.list;
        
		measure = new MyTextSimilarityMeasure(n);
        
        return true;
    }
}
