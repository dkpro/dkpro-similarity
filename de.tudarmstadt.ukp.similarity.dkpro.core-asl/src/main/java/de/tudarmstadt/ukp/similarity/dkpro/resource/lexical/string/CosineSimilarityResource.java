package de.tudarmstadt.ukp.similarity.dkpro.resource.lexical.string;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.CosineSimilarity;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.CosineSimilarity.NormalizationMode;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.CosineSimilarity.WeightingModeIdf;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.CosineSimilarity.WeightingModeTf;
import de.tudarmstadt.ukp.similarity.dkpro.resource.TextSimilarityResourceBase;


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
