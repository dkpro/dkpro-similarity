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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * This measure computes the measure of textual lexical diversity (MTLD) for
 * both texts, and compares them as follows:
 * 
 * distance(T_1,T_2) = |ttr(T_1) - ttr(T_2)|
 * 
 * MTLD is described here:
 * P. M. McCarthy and S. Jarvis (2010). MTLD, vocd-D, and HD-D: A validation
 * study of sophisticated approaches to lexical diversity assessment.
 * In: Behavior Research Methods, 42:2, pages 381-392.
 * 
 * as well as in his PhD thesis:
 * McCarthy, P. M. (2005). An assessment of the range and usefulness of lexical
 * diversity measures and the potential of the measure of textual, lexical
 * diversity (MTLD). University of Memphis: Doctoral dissertation.
 */
public class MTLDComparator
	extends JCasTextSimilarityMeasureBase
{
	private static final double FULL_FACTOR_SCORE = 0.72;
	
	@Override
	public boolean isDistanceMeasure()
	{
		return true;
	}
	
	@Override
	public double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1,
            Annotation coveringAnnotation2)
		throws SimilarityException
	{				
		double mtld1 = 0.5 * (getMTLD(jcas1, coveringAnnotation1, false) + getMTLD(jcas1, coveringAnnotation1, true));
		double mtld2 = 0.5 * (getMTLD(jcas2, coveringAnnotation2, false) + getMTLD(jcas2, coveringAnnotation2, true));
		
		double distance;
		if (mtld1 > mtld2) {
            distance = mtld1 - mtld2;
        }
        else {
            distance = mtld2 - mtld1;
        }
		
		return distance;
	}
	
	private double getMTLD(JCas jcas, Annotation coveringAnnotation, boolean reverse)
	{
		double factors = 0.0;
		
		List<Lemma> lemmas = new ArrayList<Lemma>(JCasUtil.selectCovered(jcas, Lemma.class, coveringAnnotation));

		// Initialize tokens and types
		List<String> tokens = new ArrayList<String>();
		Set<String> types = new HashSet<String>();
		
		// Reverse lemmas if flag is set
		if (reverse) {
            Collections.reverse(lemmas);
        }
		
		for (int i = 0; i < lemmas.size(); i++)
		{
			Lemma lemma = lemmas.get(i);
			
			try {
				types.add(lemma.getValue().toLowerCase());
				tokens.add(lemma.getCoveredText().toLowerCase());
			}
			catch (NullPointerException e)
			{
				System.out.println("Couldn't add token: " + lemma.getCoveredText());
			}
			
			double ttr = new Integer(types.size()).doubleValue() / new Integer(tokens.size()).doubleValue();
			
			if (ttr < FULL_FACTOR_SCORE)
			{
				// Reset types and tokens
				tokens.clear();
				types.clear();
				
				// Increment full factor count
				factors++;
			}
			else if (i == lemmas.size() - 1)
			{
				// If the end of lemma list is reached, and no full factor is reached,
				// add a incomplete factor score
				
				double ifs = (1.0 - ttr) / (1.0 - FULL_FACTOR_SCORE);
				factors += ifs;
			}
		}
		
		// mtld = number of tokens divided by factor count
		double mtld;
		if (factors == 0) {
		    mtld = 0.0;
		}
		else {
		    mtld = lemmas.size() / factors;

		}
		return mtld;
	}
}