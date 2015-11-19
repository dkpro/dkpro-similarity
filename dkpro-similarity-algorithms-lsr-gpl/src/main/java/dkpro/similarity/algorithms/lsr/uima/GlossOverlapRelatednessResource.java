/*******************************************************************************
 * Copyright 2012
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
package dkpro.similarity.algorithms.lsr.uima;
 
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import dkpro.similarity.algorithms.lsr.gloss.GlossOverlapComparator;
import dkpro.similarity.algorithms.lsr.uima.path.LSRRelatednessResourceBase;

public final class GlossOverlapRelatednessResource
	extends LSRRelatednessResourceBase
{

    public static final String PARAM_USE_PSEUDO_GLOSSES = "UsePseudoGlosses";
    @ConfigurationParameter(name = PARAM_USE_PSEUDO_GLOSSES, mandatory = true, defaultValue="false")
    protected String usePseudoGlossesString;
    
    @SuppressWarnings({ "rawtypes" })
    @Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
		throws ResourceInitializationException
	{
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}

        try {
            boolean usePseudoGlosses = Boolean.parseBoolean(usePseudoGlossesString);
            measure = new GlossOverlapComparator(lsr, usePseudoGlosses);
        }
        catch (LexicalSemanticResourceException e) {
            throw new ResourceInitializationException(e);
        }
		
		return true;
	}
}