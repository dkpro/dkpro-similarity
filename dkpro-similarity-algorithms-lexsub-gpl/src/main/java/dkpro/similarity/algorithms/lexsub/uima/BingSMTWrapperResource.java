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
package dkpro.similarity.algorithms.lexsub.uima;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;

import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexsub.BingSMTWrapper;
import dkpro.similarity.algorithms.lexsub.BingSMTWrapper.Language;
import dkpro.similarity.uima.resource.JCasTextSimilarityResourceBase;


public class BingSMTWrapperResource
	extends JCasTextSimilarityResourceBase
{
	public static final String PARAM_TEXT_SIMILARITY_RESOURCE = "TextSimilarityMeasure";
	@ExternalResource(key=PARAM_TEXT_SIMILARITY_RESOURCE)
	private TextSimilarityMeasure textSimilarityMeasure;
	
	public static final String PARAM_ORIGINAL_LANGUAGE = "OriginalLanguage";
	@ConfigurationParameter(name=PARAM_ORIGINAL_LANGUAGE, mandatory=true)
	private Language originalLanguage;
	
	public static final String PARAM_BRIDGE_LANGUAGE = "BridgeLanguage";
	@ConfigurationParameter(name=PARAM_BRIDGE_LANGUAGE, mandatory=true)
	private Language bridgeLanguage;

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
		throws ResourceInitializationException
	{
		super.afterResourcesInitialized();
		
		measure = new BingSMTWrapper(textSimilarityMeasure, originalLanguage, bridgeLanguage);
	}
}
