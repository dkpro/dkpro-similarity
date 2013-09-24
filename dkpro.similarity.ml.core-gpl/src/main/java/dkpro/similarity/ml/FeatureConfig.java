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
package dkpro.similarity.ml;

import org.apache.uima.resource.ExternalResourceDescription;


/**
 * Convenience class for setting up text similarity measures for use
 * in a preconfigured experimental setup.
 */
public class FeatureConfig
{
	private ExternalResourceDescription resource;
	private String segmentFeaturePath;
	private boolean filterStopwords;
	private String targetPath;
	private String fileNameSuffix;
	private String measureName;
	
	public FeatureConfig(ExternalResourceDescription resource, String segmentFeaturePath, boolean filterStopwords,
			String targetPath, String measureName)
	{
		init(resource, segmentFeaturePath, filterStopwords, targetPath, "", measureName);
	}
	
	public FeatureConfig(ExternalResourceDescription resource, boolean filterStopwords,
			String targetPath, String fileNameSuffix, String measureName)
	{
		init(resource, null, filterStopwords, targetPath, fileNameSuffix, measureName);
	}
	
	public FeatureConfig(ExternalResourceDescription resource, String targetPath, String measureName)
	{
		init(resource, null, false, targetPath, "", measureName);
	}
	
	public FeatureConfig(ExternalResourceDescription resource, String targetPath, String fileNameSuffix, String measureName)
	{
		init(resource, null, false, targetPath, fileNameSuffix, measureName);
	}
	
	private void init(ExternalResourceDescription resource, String segmentFeaturePath, boolean filterStopwords,
			String targetPath, String fileNameSuffix, String measureName)
	{
		this.resource = resource;
		
		if (segmentFeaturePath != null)
			this.segmentFeaturePath = segmentFeaturePath;
		else
			this.segmentFeaturePath = "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token";
		
		this.filterStopwords = filterStopwords;
		this.targetPath = targetPath;
		this.fileNameSuffix = fileNameSuffix;
		this.measureName = measureName;
	}
	
	public ExternalResourceDescription getResource()
	{
		return resource;
	}
	
	public String getSegmentFeaturePath()
	{
		return segmentFeaturePath;
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
	
	public String getMeasureName()
	{
		return measureName;
	}
}
