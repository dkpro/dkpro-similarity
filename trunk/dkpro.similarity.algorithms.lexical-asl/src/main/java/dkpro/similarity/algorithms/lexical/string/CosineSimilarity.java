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
package dkpro.similarity.algorithms.lexical.string;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Function;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;
import dkpro.similarity.algorithms.vsm.InnerVectorProduct;
import dkpro.similarity.algorithms.vsm.VectorNorm;

public class CosineSimilarity
	extends TextSimilarityMeasureBase
{
	public enum WeightingModeTf
	{
		BINARY, FREQUENCY, FREQUENCY_LOG, FREQUENCY_LOGPLUSONE
	}

	public enum WeightingModeIdf
	{
		BINARY, LOG, PASSTHROUGH, LOGPLUSONE
	}

	public enum NormalizationMode
	{
		L1, L2
	}

	private enum WeightingMode
	{
		TF, IDF, TFIDF
	}

	private WeightingMode weightingMode;
	private WeightingModeTf weightingModeTf;
	private WeightingModeIdf weightingModeIdf;
	private NormalizationMode normalizationMode;
	private Map<String, Double> idfScores;

	public CosineSimilarity()
	{
		initialize(WeightingModeTf.FREQUENCY, null, NormalizationMode.L2, null);
	}

	public CosineSimilarity(WeightingModeTf modeTf, NormalizationMode normMode)
	{
		initialize(modeTf, null, normMode, null);
	}

	public CosineSimilarity(WeightingModeIdf modeIdf, NormalizationMode normMode,
			Map<String, Double> idfScores)
	{
		initialize(null, modeIdf, normMode, idfScores);
	}

	public CosineSimilarity(WeightingModeTf modeTf, WeightingModeIdf modeIdf,
			NormalizationMode normMode, Map<String, Double> idfScores)
	{
		initialize(modeTf, modeIdf, normMode, idfScores);
	}

	public CosineSimilarity(WeightingModeIdf modeIdf, NormalizationMode normMode,
			String idfScoresFile)
	{
		HashMap<String, Double> idfValues = null;
		if(idfScoresFile != null)
		{
		idfValues = new HashMap<String,Double>();
		try {
			for (String line : FileUtils.readLines(new File(idfScoresFile)))
			{
				if (line.length() > 0)
				{
					String[] cols = line.split("\t");
					idfValues.put(cols[0], Double.parseDouble(cols[1]));
				}
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		}
		else
		{
			idfValues = null;
		}
		initialize(null, modeIdf, normMode, idfValues);
	}

	public CosineSimilarity(WeightingModeTf modeTf, WeightingModeIdf modeIdf,
			NormalizationMode normMode, String idfScoresFile)
	{
		HashMap<String, Double> idfValues ;
		if(idfScoresFile != null)
		{
		idfValues = new HashMap<String,Double>();
		try {
			for (String line : FileUtils.readLines(new File(idfScoresFile)))
			{
				if (line.length() > 0)
				{
					String[] cols = line.split("\t");
					idfValues.put(cols[0], Double.parseDouble(cols[1]));
				}
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		}
		else
		{
			idfValues = null;
		}
		initialize(modeTf, modeIdf, normMode, idfValues);
	}


	private void initialize(WeightingModeTf modeTf, WeightingModeIdf modeIdf,
			NormalizationMode normMode, Map<String, Double> idfScores)
	{
		if (modeTf == null && modeIdf != null) {
			this.weightingMode = WeightingMode.IDF;
		}
		else if (modeTf != null && modeIdf == null) {
			this.weightingMode = WeightingMode.TF;
		}
		else {
			this.weightingMode = WeightingMode.TFIDF;
		}

		this.weightingModeTf = modeTf;
		this.weightingModeIdf = modeIdf;
		this.normalizationMode = normMode;
		this.idfScores = idfScores;
	}

	@Override
	public double getSimilarity(Collection<String> aTerms1, Collection<String> aTerms2)
		throws SimilarityException
	{
		// Convert terms to lower case
		//Collection<String> terms1 = Collections2.transform(aTerms1, toLowerCase);
		//Collection<String> terms2 = Collections2.transform(aTerms2, toLowerCase);
		Collection<String> terms1 = aTerms1;
		Collection<String> terms2 = aTerms2;

		// Get common term list
		Set<String> unionTermSet = new HashSet<String>(terms1);
		unionTermSet.addAll(terms2);

		// Get TF/IDF/TFIDF vectors
		Vector vector1 = getVector(unionTermSet, terms1);
		Vector vector2 = getVector(unionTermSet, terms2);

		// fix for issue #11
		int equalElements = 0;
		for (int i=0; i<vector1.size(); i++) {
		    if (vector1.get(i) == vector2.get(i)) {
		        equalElements++;
		    }
		}
		// all equal, return 1.0
		if (equalElements == unionTermSet.size()) {
		    return 1.0;
		}
		
		// Numerator
		double num = InnerVectorProduct.COSINE.apply(vector1, vector2);
		
		double norm;
		switch (normalizationMode) {
		case L1:
			norm = VectorNorm.L1.apply(vector1, vector2);
			break;
		case L2:
			norm = VectorNorm.L2.apply(vector1, vector2);
			break;
		default:
			throw new IllegalStateException("Unsupported norm: "+normalizationMode);
		}
		// Normalized score
		return num / norm;
	}

	@Override
	public double getSimilarity(String string1, String string2)
		throws SimilarityException
	{
		// Apply naive whitespace tokenization
		return getSimilarity(Arrays.asList(string1.split(" ")), Arrays.asList(string2.split(" ")));
	}

	private Vector getVector(Collection<String> aUnionTermSet, Collection<String> aTerms)
	{
		Vector vector = new DenseVector(aUnionTermSet.size());

		// TF
		if (weightingMode == WeightingMode.TF || weightingMode == WeightingMode.TFIDF) {
			Map<String, AtomicInteger> tf = getTermFrequencies(aTerms);

			int i = 0;
			for (String term : aUnionTermSet) {
				double score = (tf.get(term) != null) ? tf.get(term).get() : 0.0;

				// Weight score
				if (weightingModeTf == WeightingModeTf.BINARY) {
					score = score >= 1 ? 1 : 0;
				}
				if (weightingModeTf == WeightingModeTf.FREQUENCY_LOG) {
					score = Math.log(score);
				}
				if (weightingModeTf == WeightingModeTf.FREQUENCY_LOGPLUSONE) {
					score = Math.log(score + 1);
				}

				vector.set(i, score);
				i++;
			}
		}

		// IDF
		if (weightingMode == WeightingMode.IDF || weightingMode == WeightingMode.TFIDF) {
			int i = 0;;
			for (String term : aUnionTermSet) {
				double score = 0;

				if (aTerms.contains(term) && idfScores.containsKey(term)) {
					score = idfScores.get(term);
				}

				// Weight score
				if (weightingModeIdf == WeightingModeIdf.BINARY) {
					score = score >= 1 ? 1 : 0;
				}
				if (weightingModeIdf == WeightingModeIdf.LOG) {
					score = Math.log(score);
				}
				if (weightingModeIdf == WeightingModeIdf.LOGPLUSONE) {
					score = Math.log(1+score);
				}
				if (weightingMode == WeightingMode.TFIDF) {
					vector.set(i, vector.get(i) * score);
				}
				else {
					vector.set(i, score);
				}
				i++;
			}
		}

		return vector;
	}

	Function<String, String> toLowerCase = new Function<String, String>()
	{
		@Override
		public String apply(String string)
		{
			return string.toLowerCase();
		}
	};

	private Map<String, AtomicInteger> getTermFrequencies(Collection<String> terms)
	{
		Map<String, AtomicInteger> frequencies = new HashMap<String, AtomicInteger>();

		for (String term : terms) {
			AtomicInteger freq = frequencies.get(term);
			if (freq == null) {
				freq = new AtomicInteger();
				frequencies.put(term, freq);
			}
			freq.incrementAndGet();
		}

		return frequencies;
	}

}
