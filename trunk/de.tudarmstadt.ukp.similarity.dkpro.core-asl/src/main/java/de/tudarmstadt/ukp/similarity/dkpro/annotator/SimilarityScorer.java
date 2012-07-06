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
package de.tudarmstadt.ukp.similarity.dkpro.annotator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathException;
import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathFactory;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.dkpro.api.type.ExperimentalTextSimilarityScore;


public class SimilarityScorer
	extends JCasAnnotator_ImplBase
{
	public static final String PARAM_NAME_VIEW_1 = "NameView1";
	@ConfigurationParameter(name=PARAM_NAME_VIEW_1, mandatory=true, defaultValue="View1")
	private String nameView1;

	public static final String PARAM_NAME_VIEW_2 = "NameView2";
	@ConfigurationParameter(name=PARAM_NAME_VIEW_2, mandatory=true, defaultValue="View2")
	private String nameView2;

	public static final String PARAM_SEGMENT_FEATURE_PATH = "SegmentFeaturePath";
	@ConfigurationParameter(name=PARAM_SEGMENT_FEATURE_PATH, mandatory=true, defaultValue="de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token")
	private String segmentFeaturePath;
	
	public static final String PARAM_TEXT_SIMILARITY_MEASURE = "TextRelatednessMeasure";
	@ExternalResource(key=PARAM_TEXT_SIMILARITY_MEASURE, mandatory=true)
	private TextSimilarityMeasure textSimilarityMeasure;

	
	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);
		
		getLogger().info(textSimilarityMeasure.getName());
	}
	
	@Override
	public void process(JCas jcas)
		throws AnalysisEngineProcessException
	{		
		try {
			JCas view1 = jcas.getView(nameView1);
			JCas view2 = jcas.getView(nameView2);
			
			DocumentMetaData md1 = JCasUtil.selectSingle(view1, DocumentMetaData.class);
			DocumentMetaData md2 = JCasUtil.selectSingle(view2, DocumentMetaData.class);
			
			getLogger().debug("Getting relatedness: " + md1.getDocumentId() + " / " + md2.getDocumentId());
			
			List<String> f1 = getFeatures(view1);
			List<String> f2 = getFeatures(view2);
			
			double relatedness = textSimilarityMeasure.getSimilarity(f1, f2);
			
			ExperimentalTextSimilarityScore score = new ExperimentalTextSimilarityScore(jcas);
			score.setScore(relatedness);
			score.addToIndexes();						
		}
		catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
		catch (FeaturePathException e) {
			throw new AnalysisEngineProcessException(e);
		}
		catch (SimilarityException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
	
	private List<String> getFeatures(JCas view)
		throws FeaturePathException
	{
		List<String> features = new ArrayList<String>();
		
		for (Map.Entry<AnnotationFS, String> entry : 
			FeaturePathFactory.select(view.getCas(), segmentFeaturePath))
		{
			features.add(entry.getValue());
		}
		
		return features;
	}
}
