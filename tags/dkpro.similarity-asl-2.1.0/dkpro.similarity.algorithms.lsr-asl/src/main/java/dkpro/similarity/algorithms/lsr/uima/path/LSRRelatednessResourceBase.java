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
package dkpro.similarity.algorithms.lsr.uima.path;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.ResourceLoaderException;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;

/**
 * 
 * @author zesch
 *
 */
public abstract class LSRRelatednessResourceBase
	extends TextSimilarityResourceBase
{

    // Attention! Can only have String parameters in external resources.
    
    public static final String PARAM_RESOURCE_NAME = "LsrResourceName";
    @ConfigurationParameter(name = PARAM_RESOURCE_NAME, mandatory = true)
    protected String lsrResourceName;
    
    public static final String PARAM_RESOURCE_LANGUAGE = "LSRResourceLanguage";
    @ConfigurationParameter(name = PARAM_RESOURCE_LANGUAGE, mandatory = true)
    protected String lsrResourceLanguage;

    protected LexicalSemanticResource lsr;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
		throws ResourceInitializationException
	{
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}

		try {
            lsr = ResourceFactory.getInstance().get(lsrResourceName, lsrResourceLanguage);
        }
        catch (ResourceLoaderException e) {
            throw new ResourceInitializationException(e);
        }

		return true;
	}
}