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
 *******************************************************************************/
package dkpro.similarity.algorithms.lexical.uima.string;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import dkpro.similarity.algorithms.lexical.string.CosineSimilarity;
import dkpro.similarity.algorithms.lexical.string.CosineSimilarity.NormalizationMode;
import dkpro.similarity.algorithms.lexical.string.CosineSimilarity.WeightingModeIdf;
import dkpro.similarity.algorithms.lexical.string.CosineSimilarity.WeightingModeTf;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;


public class CosineSimilarityResource
	extends TextSimilarityResourceBase
{
	public static final String PARAM_WEIGHTING_TF = "WeightingTf";
	@ConfigurationParameter(name=PARAM_WEIGHTING_TF, mandatory=true, defaultValue="FREQUENCY")
	private WeightingModeTf weightingTf;

	public static final String PARAM_WEIGHTING_IDF = "WeightingIdf";
	@ConfigurationParameter(name=PARAM_WEIGHTING_IDF, mandatory=false)
	private WeightingModeIdf weightingIdf;

	public static final String PARAM_NORMALIZATION = "Normalization";
	@ConfigurationParameter(name=PARAM_NORMALIZATION, mandatory=true, defaultValue="L2")
	private NormalizationMode normalization;

//	public static final String PARAM_IDF_SCORES = "IdfScores";
//	@ConfigurationParameter(name=PARAM_IDF_SCORES, mandatory=false)
//	private Map<String, Double> idfScores;

	public static final String PARAM_IDF_VALUES_FILE = "IdfValuesFile";
	@ConfigurationParameter(name=PARAM_IDF_VALUES_FILE, mandatory=false)
	private String idfValuesFile;

    @SuppressWarnings("unchecked")
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        if (weightingIdf != null  && (idfValuesFile == null)) {
			throw new ResourceInitializationException(new IllegalArgumentException("IDF weighting scheme needs a map with (token, idf score)-pairs."));
		}

        this.mode = TextSimilarityResourceMode.text;
        measure = new CosineSimilarity(weightingTf, weightingIdf, normalization, idfValuesFile);

        return true;
    }
}
