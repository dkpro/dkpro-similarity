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
package de.tudarmstadt.ukp.similarity.ml;

import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;


public class FeatureConfig
{
	public enum SimilaritySegments
	{
		TOKENS,
		LEMMAS,
		STRING,
		JCAS,
	}
	
	private TextSimilarityMeasure measure;
	private String segmentFeaturePath;
	private boolean filterStopwords;
	private String targetPath;
	private String fileNameSuffix;
	
	public FeatureConfig(TextSimilarityMeasure measure, SimilaritySegments segments, boolean filterStopwords,
			String targetPath)
	{
		init(measure, segments, filterStopwords, targetPath, "");
	}
	
	public FeatureConfig(TextSimilarityMeasure measure, SimilaritySegments segments, boolean filterStopwords,
			String targetPath, String fileNameSuffix)
	{
		init(measure, segments, filterStopwords, targetPath, fileNameSuffix);
	}
	
	public FeatureConfig(JCasTextSimilarityMeasure measure, String targetPath)
	{
		init(measure, null, false, targetPath, "");
	}
	
	public FeatureConfig(JCasTextSimilarityMeasure measure, String targetPath, String fileNameSuffix)
	{
		init(measure, null, false, targetPath, fileNameSuffix);
	}
	
	private void init(TextSimilarityMeasure measure, SimilaritySegments segments, boolean filterStopwords,
			String targetPath, String fileNameSuffix)
	{
		if (measure instanceof JCasTextSimilarityMeasure)
		{
			segmentFeaturePath = null;
		} else {		
			switch(segments)
			{
				case TOKENS:
					segmentFeaturePath = "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token";				
					break;
				case LEMMAS:
					segmentFeaturePath = "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma/getValue";
					break;
				case STRING:
					segmentFeaturePath = "org.apache.uima.jcas.tcas.DocumentAnnotation";
					break;
			}
		}
		
		this.measure = measure;
		this.filterStopwords = filterStopwords;
		this.targetPath = targetPath;
		this.fileNameSuffix = fileNameSuffix;
	}
	
	public String getSegmentFeaturePath()
	{
		return segmentFeaturePath;
	}

	public TextSimilarityMeasure getMeasure()
	{
		return measure;
	}

	public boolean filterStopwords()
	{
		return filterStopwords;
	}

	public String getTargetPath()
	{
		return targetPath;
	}

	public String getFileNameSuffix()
	{
		return fileNameSuffix;
	}
}
