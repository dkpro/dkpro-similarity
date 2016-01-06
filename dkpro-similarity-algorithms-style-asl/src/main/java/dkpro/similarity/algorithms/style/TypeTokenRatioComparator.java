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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * This measure computes TTR for both texts, and compares them as follows:
 * sim(T_1,T_2) = 1 - |ttr(T_1) - ttr(T_2)|
 */
public class TypeTokenRatioComparator
	extends JCasTextSimilarityMeasureBase
{
	@Override
    public double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1,
            Annotation coveringAnnotation2)
		throws SimilarityException
	{				
		double ttr1 = getTTR(jcas1, coveringAnnotation1);
		double ttr2 = getTTR(jcas2, coveringAnnotation2);
		
		double distance;
		if (ttr1 > ttr2) {
            distance = ttr1 - ttr2;
        }
        else {
            distance = ttr2 - ttr1;
        }
		
		return 1.0 - distance;
	}
	
	private double getTTR(JCas jcas, Annotation coveringAnnotation)
	{
		List<Sentence> sentences = new ArrayList<Sentence>(JCasUtil.selectCovered(jcas, Sentence.class, coveringAnnotation));
		
		List<String> tokens = new ArrayList<String>();
		Set<String> types = new HashSet<String>();
		
		for (Sentence sentence : sentences)
		{
			List<Token> theseTokens = JCasUtil.selectCovered(jcas, Token.class, sentence);
			List<Lemma> theseLemmas = JCasUtil.selectCovered(jcas, Lemma.class, sentence);
			
			for (Token token : theseTokens) {
                tokens.add(token.getCoveredText().toLowerCase());
            }
			
			for (Lemma lemma : theseLemmas) {
                types.add(lemma.getValue().toLowerCase());
            }
		}
		
		// Compute property
		double ttr = (double) types.size() / tokens.size();
		
		return ttr;
	}
}
