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

import org.apache.uima.resource.ExternalResourceDescription;


public class FeatureConfig
{
	private ExternalResourceDescription resource;
	private boolean filterStopwords;
	private String targetPath;
	private String fileNameSuffix;
	
	public FeatureConfig(ExternalResourceDescription resource, boolean filterStopwords,
			String targetPath)
	{
		init(resource, filterStopwords, targetPath, "");
	}
	
	public FeatureConfig(ExternalResourceDescription resource, boolean filterStopwords,
			String targetPath, String fileNameSuffix)
	{
		init(resource, filterStopwords, targetPath, fileNameSuffix);
	}
	
	public FeatureConfig(ExternalResourceDescription resource, String targetPath)
	{
		init(resource, false, targetPath, "");
	}
	
	public FeatureConfig(ExternalResourceDescription resource, String targetPath, String fileNameSuffix)
	{
		init(resource, false, targetPath, fileNameSuffix);
	}
	
	private void init(ExternalResourceDescription resource, boolean filterStopwords,
			String targetPath, String fileNameSuffix)
	{
		this.resource = resource;
		this.filterStopwords = filterStopwords;
		this.targetPath = targetPath;
		this.fileNameSuffix = fileNameSuffix;
	}
	
	public ExternalResourceDescription getResource()
	{
		return resource;
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
