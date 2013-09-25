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
package dkpro.similarity.uima.annotator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathException;
import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathFactory;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasure;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.uima.api.type.ExperimentalTextSimilarityScore;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;


/**
 * Main component which integrates similarity computation with a
 * DKPro pipeline, implemented as a UIMA {@link AnalysisComponent}.
 */
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
	
	public static final String PARAM_TEXT_SIMILARITY_RESOURCE = "TextRelatednessResource";
	@ExternalResource(key=PARAM_TEXT_SIMILARITY_RESOURCE, mandatory=true)
	private TextSimilarityResourceBase textSimilarityResource;

	
	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);
		
		getLogger().debug(textSimilarityResource.getName());
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
			
			getLogger().debug("Getting similarity: " + md1.getDocumentId() + " / " + md2.getDocumentId());
			
			double similarity;
			switch (textSimilarityResource.getMode()) {
			    case text:
			    	similarity = textSimilarityResource.getSimilarity(view1.getDocumentText(), view2.getDocumentText());
			        break;
			    case jcas:
			    	similarity = ((JCasTextSimilarityMeasure) textSimilarityResource).getSimilarity(view1, view2);
			        break;
                default: 
                    List<String> f1 = getFeatures(view1);
                    List<String> f2 = getFeatures(view2);
                    
                    // Remove "_" tokens
                    for (int i = f1.size() - 1; i >= 0; i--)
                    {
                   		if (f1.get(i) == null || f1.get(i).equals("_")) {
                            f1.remove(i);
                        }
                    }
                    for (int i = f2.size() - 1; i >= 0; i--)
                    {
                   		if (f2.get(i) == null || f2.get(i).equals("_")) {
                            f2.remove(i);
                        }
                    }
                    
                    similarity = textSimilarityResource.getSimilarity(f1, f2);
			}
			
			ExperimentalTextSimilarityScore score = new ExperimentalTextSimilarityScore(jcas);
			score.setScore(similarity);
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