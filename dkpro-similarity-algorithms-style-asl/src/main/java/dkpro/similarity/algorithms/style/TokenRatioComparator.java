/*******************************************************************************
 * Copyright 2013
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
package dkpro.similarity.algorithms.style;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * This measure computes the ratio of number of tokens in the
 * suspicious text compared to the number of tokens in the original
 * text:
 * 
 * sim(T_1,T_2) = no_tokens(T_1) / no_tokens(T_2)
 */
public class TokenRatioComparator
	extends JCasTextSimilarityMeasureBase
{	
	@Override
	public boolean isDistanceMeasure()
	{
		return true;
	}
	
    @Override
    public double getSimilarity(JCas jcas1, JCas jcas2)
        throws SimilarityException
    {       
        int no1 = JCasUtil.select(jcas1, Token.class).size(); 
        int no2 = JCasUtil.select(jcas2, Token.class).size();

        return computeSentenceRatio(no1, no2);
    }

    @Override
    public double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1,
            Annotation coveringAnnotation2)
        throws SimilarityException
    {
        int no1 = JCasUtil.selectCovered(jcas1, Token.class, coveringAnnotation1).size(); 
        int no2 = JCasUtil.selectCovered(jcas2, Token.class, coveringAnnotation2).size();

        return computeSentenceRatio(no1, no2);
    }

    private double computeSentenceRatio(int no1, int no2) {
        double sim = (no2 == 0) ? 0.0 : (double) no1 / no2;
        return sim;
    }
}