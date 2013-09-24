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
package dkpro.similarity.uima.resource;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasure;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;

/**
 * This resource wraps any parameter-free {@link TextSimilarityMeasure}}.
 */

public class SimpleTextSimilarityResource
    extends TextSimilarityResourceBase
{
    public static final String PARAM_TEXT_SIMILARITY_MEASURE = "TextSimilarityMeasure";
	@ConfigurationParameter(name=PARAM_TEXT_SIMILARITY_MEASURE, mandatory=true)
	private String textSimilarityMeasureName;
	
    public static final String PARAM_MODE= "Mode";
    @ConfigurationParameter(name=PARAM_MODE, mandatory=true, defaultValue="list")
    private TextSimilarityResourceMode modeParameter;

    @SuppressWarnings("unchecked")
	@Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
	    throws ResourceInitializationException
	{
	    if (!super.initialize(aSpecifier, aAdditionalParams)) {
	        return false;
	    }

	    this.setMode(modeParameter);
	    
        try {
            switch (mode) {
                case list:
                case text:
                    measure = (TextSimilarityMeasure) Class.forName(textSimilarityMeasureName).newInstance();
                    break;
                case jcas:
                    measure = (JCasTextSimilarityMeasure) Class.forName(textSimilarityMeasureName).newInstance();
                    break;
                default: 
                    measure = (TextSimilarityMeasure) Class.forName(textSimilarityMeasureName).newInstance();
                    break;
            }
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

	    return true;
	}
}