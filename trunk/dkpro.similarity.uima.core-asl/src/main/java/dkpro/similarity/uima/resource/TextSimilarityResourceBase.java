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

import java.util.Collection;

import org.apache.uima.fit.component.Resource_ImplBase;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;


public abstract class TextSimilarityResourceBase
    extends Resource_ImplBase
    implements TextSimilarityMeasure
{
    public enum TextSimilarityResourceMode {
        jcas,
        text,
        list
    }
    
    protected TextSimilarityMeasure measure;
    protected TextSimilarityResourceMode mode;
    
    @Override
    public double getSimilarity(Collection<String> stringList1, Collection<String> stringList2)
        throws SimilarityException
    {
        return measure.getSimilarity(stringList1, stringList2);
    }

    @Override
    public double getSimilarity(String[] strings1, String[] strings2)
        throws SimilarityException
    {
        return measure.getSimilarity(strings1, strings2);
    }

    @Override
    public double getSimilarity(String string1, String string2)
        throws SimilarityException
    {
        return measure.getSimilarity(string1, string2);
    }

    @Override
    public String getName()
    {
        return measure.getName();
    }

    @Override
    public boolean isDistanceMeasure()
    {
        return measure.isDistanceMeasure();
    }
    
    @Override
    public void beginMassOperation()
    {
    	measure.beginMassOperation();
    }
    
    @Override
    public void endMassOperation()
    {
    	measure.endMassOperation();
    }

    public TextSimilarityResourceMode getMode()
    {
        return mode;
    }
    
    public void setMode(TextSimilarityResourceMode mode)
    {
    	this.mode = mode;
    }
}