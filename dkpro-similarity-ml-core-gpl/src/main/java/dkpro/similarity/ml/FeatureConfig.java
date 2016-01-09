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
