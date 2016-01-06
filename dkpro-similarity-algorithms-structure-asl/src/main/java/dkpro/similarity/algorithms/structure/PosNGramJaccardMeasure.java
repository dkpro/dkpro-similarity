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
package dkpro.similarity.algorithms.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * Computes the overlap of part-of-speech n-grams using the
 * Jaccard measure, similarly as for the word n-grams
 * in the {@code WordNGramJaccardMeasure}. 
 */
public class PosNGramJaccardMeasure
	extends JCasTextSimilarityMeasureBase
{
	int n;
	
	public PosNGramJaccardMeasure(int n)
	{
		this.n = n;
	}
	
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		throw new SimilarityException(new NotImplementedException());
	}
	
	@Override
    public double getSimilarity(JCas jcas1, JCas jcas2)
		throws SimilarityException
	{
		// Get POS
		Collection<POS> pos1 = JCasUtil.select(jcas1, POS.class);
		Collection<POS> pos2 = JCasUtil.select(jcas2, POS.class);
		
		return getPosNGramSimilarity(pos1, pos2);
	}
	
    @Override
    public double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1,
            Annotation coveringAnnotation2)
        throws SimilarityException
    {
        // Get POS
        Collection<POS> pos1 = JCasUtil.selectCovered(jcas1, POS.class, coveringAnnotation1);
        Collection<POS> pos2 = JCasUtil.selectCovered(jcas2, POS.class, coveringAnnotation2);
        
        return getPosNGramSimilarity(pos1, pos2);
    }
	
    private double getPosNGramSimilarity(Collection<POS> pos1, Collection<POS> pos2) {
        // Get n-grams
        Set<List<String>> ngrams1 = getPosNGrams(new ArrayList<POS>(pos1));
        Set<List<String>> ngrams2 = getPosNGrams(new ArrayList<POS>(pos2));
        
        double sim = getNormalizedSimilarity(ngrams1, ngrams2);

        return sim;
    }

    private Set<List<String>> getPosNGrams(List<POS> pos)
	{
		Set<List<String>> ngrams = new HashSet<List<String>>();
		
		for (int i = 0; i < pos.size() - (n - 1); i++)
		{
			// Generate n-gram at index i
			List<String> ngram = new ArrayList<String>();
			for (int k = 0; k < n; k++)
			{
				String token = pos.get(i + k).getPosValue();
				ngram.add(token);
			}
			
			// Add
			ngrams.add(ngram);
		}
		
		return ngrams;
	}
	
	protected double getNormalizedSimilarity(Set<List<String>> suspiciousNGrams, Set<List<String>> originalNGrams)
	{
		// Compare using the Jaccard similarity coefficient (Manning & Schütze, 1999)
		Set<List<String>> commonNGrams = new HashSet<List<String>>();
		commonNGrams.addAll(suspiciousNGrams);
		commonNGrams.retainAll(originalNGrams);
		
		Set<List<String>> unionNGrams = new HashSet<List<String>>();
		unionNGrams.addAll(suspiciousNGrams);
		unionNGrams.addAll(originalNGrams);
		
		double norm = unionNGrams.size();
		double sim = 0.0;
		
		if (norm > 0.0) {
            sim = commonNGrams.size() / norm;
        }
		
		return sim;
	}

	@Override
	public String getName()
	{
		return this.getClass().getSimpleName() + "_" + n + "grams";
	}
}
