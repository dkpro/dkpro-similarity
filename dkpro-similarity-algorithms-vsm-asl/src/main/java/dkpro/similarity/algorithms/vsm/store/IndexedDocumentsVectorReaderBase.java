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
package dkpro.similarity.algorithms.vsm.store;

import java.util.Arrays;
import java.util.Set;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.SparseVector;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.vsm.VectorNorm;

/**
 * Abstract base class for vector readers using indexed documents from a traditional IR index.
 *
 * @author Richard Eckart de Castilho
 */
public abstract class IndexedDocumentsVectorReaderBase
	extends VectorReader
{
	public enum WeightingModeTf
	{
		binary, normal, log, logPlusOne, normalized
		// after Hassan & Mihalcea
	}

	public enum WeightingModeIdf
	{
		binary, constantOne, normal, log
	}

	private VectorNorm normalizationMode = VectorNorm.L2;
	private WeightingModeTf weightingModeTf = WeightingModeTf.logPlusOne;
	private WeightingModeIdf weightingModeIdf = WeightingModeIdf.constantOne;
	private float weightingThreshold = 0.0f;
	private float vectorLengthThreshold = 1.0f;

	/**
	 * Computes dense average concept vector by first mapping each term on its concept vector,
	 * then adding the vectors and finally scaling the vector with 1/terms.
	 */
	@Override
	public Vector getVector(String aTerm)
		throws SimilarityException
	{
		// get the document frequencies of term
		int df = getDocumentFrequency(aTerm);

		// if df is zero, then the term is not in the index
		if (df == 0) {
			return null;
		}

		Vector conceptMap = new SparseVector(getConceptCount());

		for (int docId : getDocuments(aTerm)) {
			int tf = getTermFrequency(docId, aTerm);

			double weight = getWeight(weightingModeTf, weightingModeIdf, tf, df,
					getDocumentCount(), docId);

			conceptMap.set(docId, weight);
		}

		// The vector is normalized, as the size of tfidf values are dependent on the size of a
		// document
		// This means, that we cannot specify a sensible threshold from a constant range [0,1].
		normalize(conceptMap); // normalize the vector
		applyThreshold(conceptMap); // apply the weighting threshold
		// After applying the threshold, the vector might be denormalized again.
		normalize(conceptMap); // normalize the vector

		return conceptMap;
	}

	public int getVectorLengthThresholdAbsolute()
		throws SimilarityException
	{
		return (int) Math.ceil(vectorLengthThreshold * getDocumentCount());
	}

	@Override
	public int getConceptCount()
		throws SimilarityException
	{
		return getDocumentCount();
	}

	public abstract int getDocumentLength(int aDocId)
		throws SimilarityException;

	public abstract int getDocumentFrequency(String aTerm)
		throws SimilarityException;

	public abstract int getDocumentCount()
		throws SimilarityException;

	public abstract Set<Integer> getDocuments(String aTerm)
		throws SimilarityException;

	public abstract int getTermFrequency(int aDocId, String aTerm)
		throws SimilarityException;

	public abstract int getIndexVocabularySize()
		throws SimilarityException;

	private void normalize(Vector scv)
	{
		double norm = normalizationMode.apply(scv);

		if (norm == 0.0) {
			return;
		}

		scv.scale(1 / norm);
	}

	/**
	 * @param modeTf
	 *            The model that should be used to compute tf.
	 * @param modeIdf
	 *            The model that should be used to compute idf.
	 * @param tf
	 *            The term frequency count.
	 * @param df
	 *            The document frequency count.
	 * @param n
	 *            The number of documents in the collection.
	 * @param docId
	 *            The id of the current document.
	 * @return The combined weight.
	 * @throws SimilarityException
	 */
	private double getWeight(WeightingModeTf modeTf, WeightingModeIdf modeIdf, int tf, int df,
			int n, int aDocId)
		throws SimilarityException
	{

		double tfModified = tf;
		if (modeTf.equals(WeightingModeTf.normal)) {
			// do nothing, take initial value
		}
		else if (modeTf.equals(WeightingModeTf.log)) {
			if (tf > 0) {
				tfModified = Math.log(tf);
			}
			else {
				tfModified = 0;
			}
		}
		else if (modeTf.equals(WeightingModeTf.logPlusOne)) {
			if (tf > 0) {
				tfModified = Math.log(tf + 1);
			}
			else {
				tfModified = 0;
			}
		}
		else if (modeTf.equals(WeightingModeTf.normalized)) {
			if (tf > 0) {
				tfModified = tf * Math.log((double) getIndexVocabularySize() / getDocumentLength(aDocId));
			}
			else {
				tfModified = 0;
			}
		}
		else if (modeTf.equals(WeightingModeTf.binary)) {
			if (tf > 0) {
				tfModified = 1;
			}
			else {
				tfModified = 0;
			}
		}

		double idfModified = (double) df / n;
		if (modeIdf.equals(WeightingModeIdf.constantOne)) {
			idfModified = 1.0;
		}
		else if (modeIdf.equals(WeightingModeIdf.normal)) {
			// do nothing, take initial value
		}
		else if (modeIdf.equals(WeightingModeIdf.log)) {
			if (df > 0) {
				idfModified = Math.log((double) n / df);
			}
			else {
				idfModified = 0;
			}
		}
		else if (modeIdf.equals(WeightingModeIdf.binary)) {
			if (df > 0) {
				idfModified = 1;
			}
			else {
				idfModified = 0;
			}
		}

		return tfModified * idfModified;
	}

	/**
	 * Apply the weighting threshold and the vector length threshold to the concept map. This first
	 * only keeps the number of elements in the index indicated by vectorLengthThreshold. It then
	 * removes all elements in the map with values lower or equal to the weightingThreshold.
	 *
	 * @param aVector the concept vector.
	 */
	private void applyThreshold(Vector aVector)
		throws SimilarityException
	{

		// determine the threshold value according to the vectorLengthThreshold
		// if we remove all elements from the vector that are below that value, the vector will have
		// desired length
		double weightThresholdFromVectorLength = -1.0;

		int nonZero = Matrices.cardinality(aVector);

		if (nonZero > getVectorLengthThresholdAbsolute()) {
			double[] weights;

			if (aVector instanceof SparseVector) {
				weights = ((SparseVector) aVector).getData();
			}
			else if (aVector instanceof DenseVector) {
				weights = ((DenseVector) aVector).getData();
			}
			else {
				throw new IllegalArgumentException("Only support SparseVector and DenseVector");
			}

			Arrays.sort(weights);

			int offset = weights.length - 1 - getVectorLengthThresholdAbsolute();
			if (offset >= 0) {
				weightThresholdFromVectorLength = weights[offset];
			}
		}

		for (VectorEntry e : aVector) {
			double weight = e.get();
			if (weight <= weightThresholdFromVectorLength) {
				e.set(0.0);
				continue;
			}
			if (weightingThreshold > 0.0 && weight <= weightingThreshold) {
				e.set(0.0);
				continue;
			}
		}
	}

	@Deprecated
	public Norm getNormalizationMode()
	{
		switch (normalizationMode) {
		case L1:
			return Norm.One;
		case L2:
			return Norm.TwoRobust;
		default:
			return null;
		}
	}

	@Deprecated
	public void setNorm(Norm aNormalizationMode)
	{
		switch (aNormalizationMode) {
		case One:
			setNorm(VectorNorm.L1);
			break;
		case Two:
		case TwoRobust:
			setNorm(VectorNorm.L2);
			break;
		default:
			throw new IllegalArgumentException("Norm ["+aNormalizationMode+"] not supported");
		}
	}

	public void setNorm(VectorNorm aNormalizationMode)
	{
		normalizationMode = aNormalizationMode;
	}

	public WeightingModeTf getWeightingModeTf()
	{
		return weightingModeTf;
	}

	public void setWeightingModeTf(WeightingModeTf aWeightingModeTf)
	{
		weightingModeTf = aWeightingModeTf;
	}

	public WeightingModeIdf getWeightingModeIdf()
	{
		return weightingModeIdf;
	}

	public void setWeightingModeIdf(WeightingModeIdf aWeightingModeIdf)
	{
		weightingModeIdf = aWeightingModeIdf;
	}

	public float getWeightingThreshold()
	{
		return weightingThreshold;
	}

	public void setWeightingThreshold(float aWeightingThreshold)
	{
		weightingThreshold = aWeightingThreshold;
	}

	public float getVectorLengthThreshold()
	{
		return vectorLengthThreshold;
	}

	public void setVectorLengthThreshold(float aVectorLengthThreshold)
	{
		vectorLengthThreshold = aVectorLengthThreshold;

        // 0.0 also means disabled, but a threshold of zero would completely empty the vector
        // thus, we set it to 1.0
        if ((vectorLengthThreshold - 0.0) < 0.0000001) {
            vectorLengthThreshold = 1.0f;
        }

        if (this.vectorLengthThreshold < 0.0 || vectorLengthThreshold > 1.0) {
            throw new IllegalArgumentException("Vector length threshold needs to be between 0.0 " +
            		"and 1.0. The absolute threshold is then computed as the product of this " +
            		"value and the size of the vector.");
        }
	}
}
