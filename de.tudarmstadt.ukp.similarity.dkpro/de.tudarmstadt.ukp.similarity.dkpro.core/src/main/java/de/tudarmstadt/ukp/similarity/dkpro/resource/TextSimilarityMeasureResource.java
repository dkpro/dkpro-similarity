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
package de.tudarmstadt.ukp.similarity.dkpro.resource;

import java.util.Collection;

import org.uimafit.component.Resource_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;


public class TextSimilarityMeasureResource
	extends Resource_ImplBase
	implements TextSimilarityMeasure
{
	public static final String MSG_CANNOT_INITIALIZE = "Cannot initialize TextRelatednessMeasureResource. Base measure not found.";
	
	public static final String PARAM_TEXT_RELATEDNESS_MEASURE_NAME = "TextRelatednessMeasureName";
    @ConfigurationParameter(name = PARAM_TEXT_RELATEDNESS_MEASURE_NAME)
    private String textRelatednessMeasureName;
    private TextSimilarityMeasure textRelatednessMeasure;

    
    @Override
    public void afterResourcesInitialized()
    {
    	super.afterResourcesInitialized();
    	
    	try {
			textRelatednessMeasure = (TextSimilarityMeasure) Class.forName(textRelatednessMeasureName).newInstance();
		}
		catch (InstantiationException e) {
			getLogger().error(MSG_CANNOT_INITIALIZE);
		}
		catch (IllegalAccessException e) {
			getLogger().error(MSG_CANNOT_INITIALIZE);
		}
		catch (ClassNotFoundException e) {
			getLogger().error(MSG_CANNOT_INITIALIZE);
		}
    }
    
    @Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		return textRelatednessMeasure.getSimilarity(stringList1, stringList2);
	}

	@Override
	public double getSimilarity(String string1, String string2)
		throws SimilarityException
	{
		return textRelatednessMeasure.getSimilarity(string1, string2);
	}
    
	@Override
	public void beginMassOperation()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void endMassOperation()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	public boolean isDistanceMeasure()
	{
		return false;
	}
}
