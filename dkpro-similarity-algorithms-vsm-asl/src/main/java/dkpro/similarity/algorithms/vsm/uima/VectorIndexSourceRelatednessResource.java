/*******************************************************************************
 * Copyright 2011, 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package dkpro.similarity.algorithms.vsm.uima;

import java.io.File;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import dkpro.similarity.algorithms.vsm.VectorComparator;
import dkpro.similarity.algorithms.vsm.store.CachingVectorReader;
import dkpro.similarity.algorithms.vsm.store.vectorindex.VectorIndexReader;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;

public final class VectorIndexSourceRelatednessResource
	extends TextSimilarityResourceBase
{

    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = true)
    protected String modelLocation;
    
    public static final String PARAM_CACHE_SIZE = "CacheSize";
    @ConfigurationParameter(name = PARAM_CACHE_SIZE, mandatory = true, defaultValue="100")
    protected String cacheSize;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }
        
        this.mode = TextSimilarityResourceMode.list;

        measure = new VectorComparator(new CachingVectorReader(
                new VectorIndexReader(new File(modelLocation)),
                Integer.parseInt(cacheSize)
        ));
        
        return true;
    }
}