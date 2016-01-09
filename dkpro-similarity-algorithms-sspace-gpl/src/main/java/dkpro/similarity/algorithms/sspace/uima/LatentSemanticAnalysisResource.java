/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package dkpro.similarity.algorithms.sspace.uima;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.fit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import dkpro.similarity.algorithms.sspace.SSpaceVectorReader;
import dkpro.similarity.algorithms.vsm.VectorComparator;
import dkpro.similarity.algorithms.vsm.store.CachingVectorReader;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.SemanticSpaceIO;
import edu.ucla.sspace.common.SemanticSpaceIO.SSpaceFormat;


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
    protected int cacheSize;
    
    public static final String PARAM_PERSISTENTLY_STORE_MODEL = "PersistentlyStoreModel";
    @ConfigurationParameter(name = PARAM_PERSISTENTLY_STORE_MODEL, mandatory = true, defaultValue="false")
    protected boolean storeModelPersistently;
    
    public static final String PARAM_MODEL_DIR = "ModelDir";
    @ConfigurationParameter(name = PARAM_MODEL_DIR, mandatory = true, defaultValue="")
    protected File modelDir;
    
	
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
			
			File inputDir = new File(inputUrl.getFile());
			File modelFile = new File(modelDir + "/" + inputDir.getName() + ".sspace");
			
			SemanticSpace sspace;
			
			if (modelFile.exists())
			{
				// Load from persistent model
				sspace = SemanticSpaceIO.load(modelFile); 
			}
			else
			{
				// No persistent model found, so create one			
				sspace = SSpaceVectorReader.createSemanticSpace(
						inputDir,
						dimensions);
				
				// Store it persistently if requested
				if (storeModelPersistently)
				{
					modelDir.mkdirs();
					SemanticSpaceIO.save(sspace, modelFile, SSpaceFormat.BINARY);
				}
			}
				
			measure = new VectorComparator(new CachingVectorReader(
	                new SSpaceVectorReader(sspace),
	                cacheSize)); 
	}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}

		return true;
	}
    
}
