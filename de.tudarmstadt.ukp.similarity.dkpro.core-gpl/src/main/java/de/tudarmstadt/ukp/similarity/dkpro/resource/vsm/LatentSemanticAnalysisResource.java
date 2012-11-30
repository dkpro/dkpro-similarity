package de.tudarmstadt.ukp.similarity.dkpro.resource.vsm;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.similarity.algorithms.sspace.SSpaceVectorReader;
import de.tudarmstadt.ukp.similarity.algorithms.vsm.VectorComparator;
import de.tudarmstadt.ukp.similarity.algorithms.vsm.store.CachingVectorReader;
import de.tudarmstadt.ukp.similarity.dkpro.resource.TextSimilarityResourceBase;
import edu.ucla.sspace.common.SemanticSpace;


public class LatentSemanticAnalysisResource
	extends TextSimilarityResourceBase
{
	public static final String PARAM_INPUT_DIR = "InputDir";
    @ConfigurationParameter(name = PARAM_INPUT_DIR, mandatory = true)
    protected String inputDirName;
    
    public static final String PARAM_DIMENSIONS = "Dimensions";
    @ConfigurationParameter(name = PARAM_DIMENSIONS, mandatory = true, defaultValue="300")
    protected int dimensions;
    
    public static final String PARAM_CACHE_SIZE = "CacheSize";
    @ConfigurationParameter(name = PARAM_CACHE_SIZE, mandatory = true, defaultValue="100")
    protected String cacheSize;
    
	
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier,
			Map<String, Object> aAdditionalParams)
		throws ResourceInitializationException
	{
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
	        return false;
	    }
		
		this.mode = TextSimilarityResourceMode.list;
		
		URL inputUrl;
		try {
			inputUrl = ResourceUtils.resolveLocation(inputDirName);
			
			SemanticSpace sspace = SSpaceVectorReader.createSemanticSpace(
					new File(inputUrl.getFile()),
					dimensions);
			
			measure = new VectorComparator(new CachingVectorReader(
	                new SSpaceVectorReader(sspace),
	                Integer.parseInt(cacheSize))); 
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}

		return true;
	}
    
}
