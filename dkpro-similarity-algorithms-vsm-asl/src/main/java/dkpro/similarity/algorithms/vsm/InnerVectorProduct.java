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
package dkpro.similarity.algorithms.vsm;

import static java.lang.Math.min;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.SparseVector;

public enum InnerVectorProduct
{
	/**
	 * Calculates the dot product between two vectors A and B (res = a1*b1 + ... + an*bn). If the
	 * input vectors are L2-normalized, this corresponds to the cosine. Otherwise the result
	 * of this inner product has to be normalized as cos = res / l2norm(A)*l2norm(B) to yield
	 * the actual cosine.
	 */
	COSINE,

	/**
	 * Alternate implementation of {@link #COSINE} that uses an optimized algorithm depending on
	 * whether sparse or dense vectors are compared.
	 * <p>
	 * The optimized implementations should be faster, but we didn't really check that yet.
	 */
	FAST_COSINE,
	LESK_OVERLAP,
	MIN_OVERLAP,
	LANGUAGE_MODEL,
	COVERAGE,
	AVERAGE_PRODUCT,

	/**
	 * Modified Dice's coefficient that can be used to compare two sets A and B when one is
	 * interested that A is a subset of B, but B may be significantly larger than A. As for {@link
	 * #DICE}, the sets can be crisp or fuzzy and are expected to be represented as vectors only
	 * consisting of non-negative elements.
	 * <p>
	 * Range: 0.0 - 1.0
	 *
	 * @see #DICE
	 */
	LEFT_DICE,

	/**
	 * Dice's coefficient. Calculates the similarity between two sets based on the size of the
	 * intersection compared to the sizes of the two sets. The two sets are represented as vectors.
	 * If the vectors elements only assume the values 0 or 1, the implementation calculates the actual
	 * Dice's coefficient. If the vector elements are non-negative values (best between 0 and 1), the
	 * vector is assumed to represent a fuzzy set where each vector element is the degree of
	 * membership. Vector elements must not be negative.
	 * <p>
	 * Range: 0.0 - 1.0
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Dice_coefficient">Dice coefficient (Wikipedia)</a>
	 * @see <a href="http://en.wikipedia.org/wiki/Fuzzy_set">Fuzzy sets (Wikipedia)</a>
	 */
	DICE;

	public double apply(Vector vec1, Vector vec2)
	{
		if (vec1 == null || vec2 == null) {
			throw new IllegalArgumentException("Compared vectors must not be null");
		}

		switch(this) {
		case COSINE: return getCosineRelatedness(vec1, vec2);
		case FAST_COSINE: return getFastCosineRelatedness(vec1, vec2);
		case LESK_OVERLAP: return getLeskOverlap(vec1, vec2);
		case MIN_OVERLAP: return getMinOverlap(vec1, vec2);
		case LANGUAGE_MODEL: return getLanguageModelRelatedness(vec1, vec2);
		case COVERAGE: return getCoverage(vec1, vec2);
		case AVERAGE_PRODUCT: return getAverageProduct(vec1, vec2);
		case LEFT_DICE: return getLeftDice(vec1, vec2);
		case DICE: return getDice(vec1, vec2);
			default:
			throw new IllegalStateException("Unknown inner product ["+this+"]");
		}
	}

	private static double getCosineRelatedness(Vector vec1, Vector vec2)
	{
		Vector v1;
		Vector v2;

		if (length(vec1) < length(vec2)) {
			v1 = vec1;
			v2 = vec2;
		}
		else {
			v2 = vec1;
			v1 = vec2;
		}

		double vectorProduct = 0.0;

		for (VectorEntry e : v1) {
			vectorProduct += e.get() * v2.get(e.index());
		}

		return vectorProduct;
	}

	// TODO REC 2010-02-16 Have to check if this is really faster
	private static double getFastCosineRelatedness(Vector v1, Vector v2)
	{
		double score;

		if ((v1 instanceof SparseVector) && (v2 instanceof SparseVector)) {
			score = getFastCosineRelatednessSparse((SparseVector) v1, (SparseVector) v2);
		}
		else if ((v1 instanceof DenseVector) && (v2 instanceof DenseVector)) {
			score = getFastCosineRelatednessDense((DenseVector) v1, (DenseVector) v2);
		}
		else {
//			if (v1 instanceof SparseVector) {
//				v1 = new DenseVector(v1);
//			}
//			if (v2 instanceof SparseVector) {
//				v2 = new DenseVector(v2);
//			}
//			score = getFastCosineRelatednessDense((DenseVector) v1, (DenseVector) v2);
			score = getCosineRelatedness(v1, v2);
		}
		return score;
	}

	/**
	 * Computes the cosine. (faster than scoreDense())
	 */
	private static double getFastCosineRelatednessSparse(SparseVector vec1, SparseVector vec2)
	{
		double score = 0;

		if (vec1 != null && vec2 != null) {
			// int[] index1 = vec1.getIndex();
			// int[] index2 = vec2.getIndex();
			double[] data1 = vec1.getData();
			double[] data2 = vec2.getData();

			score = vec1.dot(vec2);

			double norm1 = 0;
			double norm2 = 0;
			for (int i = 0; i < data1.length; i++) {
				double v = data1[i];
				norm1 += v * v;
			}
			for (int i = 0; i < data2.length; i++) {
				double v = data2[i];
				norm2 += v * v;
			}
			norm1 = Math.sqrt(norm1);
			norm2 = Math.sqrt(norm2);
			score = score / (norm1 * norm2);
		}

		if (Double.isNaN(score)) {
			score = 0;
		}

		return score;
	}

	/**
	 * Computes the cosine.
	 */
	private static double getFastCosineRelatednessDense(DenseVector vec1, DenseVector vec2)
	{
		double score = 0;

		if (vec1 != null && vec2 != null) {
			score = vec1.dot(vec2);

			double norm1 = 0;
			double norm2 = 0;
			for (int i = 0; i < vec1.size(); i++) {
				double v1 = vec1.get(i);
				double v2 = vec2.get(i);
				norm1 += v1 * v1;
				norm2 += v2 * v2;
			}

			norm1 = Math.sqrt(norm1);
			norm2 = Math.sqrt(norm2);

			score = score / (norm1 * norm2);
		}

		if (Double.isNaN(score)) {
			score = 0;
		}

		return score;
	}

	private static double getLeskOverlap(Vector vec1, Vector vec2)
	{
		double vectorOverlap = 0.0;

		for (VectorEntry e : vec1) {
			double n = vec2.get(e.index());
			if (n != 0.0) {
				vectorOverlap += e.get() + n;
			}
		}

		return vectorOverlap;
	}

	private static double getMinOverlap(Vector vec1, Vector vec2)
	{
		double vectorOverlap = 0.0;

		for (VectorEntry e : vec1) {
			double n = vec2.get(e.index());
			if (n != 0.0) {
				vectorOverlap += min(e.get(), n);
			}
		}

		return vectorOverlap;
	}

	private static double getLanguageModelRelatedness(Vector vec1, Vector vec2)
	{
		double coverage1_2 = getCoverage(vec1, vec2);
		double coverage2_1 = getCoverage(vec2, vec1);

		return (coverage1_2 + coverage2_1) / 2;
	}

	private static double getCoverage(Vector vec1, Vector vec2)
	{

		double denominator = 0;
		for (VectorEntry e : vec2) {
			denominator += e.get();
		}

		if (denominator == 0) {
			return 0.0;
		}

		double nominator = 0;
		for (VectorEntry e : vec1) {
			nominator += vec2.get(e.index());
		}

		return nominator / denominator;
	}

	private static double getLeftDice(Vector vec1, Vector vec2)
	{
		if (vec1.size() != vec2.size()) {
			throw new IllegalArgumentException("Argument vectors have different sizes ("
					+ vec1.size() + " vs. " + vec2.size() + ")");
		}

		for (VectorEntry e : vec1) {
			if (e.get() < 0.0) {
				throw new IllegalArgumentException("Argument vector contains negative values.");
			}
		}

		for (VectorEntry e : vec2) {
			if (e.get() < 0.0) {
				throw new IllegalArgumentException("Argument vector contains negative values.");
			}
		}

		// The first support vector is for the set A
		double fCardA = fuzzyCardinality(vec1);

		// The second support vector is for the set B
		double fCardB = fuzzyCardinality(vec2);

		// Now we calculate the cardinality of the fuzzy intersection of A and B
		double fIntersetCard = fuzzyIntersetCardinality(vec1, vec2);

		double wRight = fCardA / Math.max(fCardA, fCardB);

        // return modified Dices coefficient rewarding all strings in list 1 to be matched
        double sim = ((wRight + 1.0) * fIntersetCard) / (fCardA + (wRight * Math.max(fCardA, fCardB)));
        if (Double.isNaN(sim)) {
        	sim = 0.0;
        }

        return sim;
	}

	private static double getDice(Vector vec1, Vector vec2)
	{
		if (vec1.size() != vec2.size()) {
			throw new IllegalArgumentException("Argument vectors have different sizes ("
					+ vec1.size() + " vs. " + vec2.size() + ")");
		}

		for (VectorEntry e : vec1) {
			if (e.get() < 0.0) {
				throw new IllegalArgumentException("Argument vector contains negative values.");
			}
		}

		for (VectorEntry e : vec2) {
			if (e.get() < 0.0) {
				throw new IllegalArgumentException("Argument vector contains negative values.");
			}
		}

		// The first support vector is for the set A
		double fCardA = fuzzyCardinality(vec1);

		// The second support vector is for the set B
		double fCardB = fuzzyCardinality(vec2);

		// Now we calculate the cardinality of the fuzzy intersection of A and B
		double fIntersetCard = fuzzyIntersetCardinality(vec1, vec2);

        //return Dices coefficient = (2*Common Terms) / (Number of terms in String1 + Number of terms in String2)
        double sim = (2.0 * fIntersetCard) / (fCardA + fCardB);
        if (Double.isNaN(sim)) {
        	sim = 0.0;
        }
        return sim;
	}

	/**
	 * The fuzzy intersection of two sets is defined as the set containing the intersection of the
	 * elements of both sets, with their support being the minimum support for the element in
	 * either set.
	 */
	private static double fuzzyIntersetCardinality(Vector vec1, Vector vec2)
	{
		double sum = 0.0;
		for (VectorEntry entry : vec1) {
			double aSupport = entry.get();
			double bSupport = vec2.get(entry.index());
			sum += Math.min(aSupport, bSupport);
		}
		return sum;
	}

	private static double fuzzyCardinality(Vector aSV)
	{
		double d = 0.0;
		for (VectorEntry e : aSV) {
			d += e.get();
		}
		return d;
	}

	private static double getAverageProduct(Vector vec1, Vector vec2)
	{
		if (vec1 instanceof DenseVector && vec2 instanceof DenseVector) {
			return getAverageProductDense((DenseVector) vec1, (DenseVector) vec2);
		}
		else {
			return getAverageProductGeneric(vec1, vec2);
		}
	}

	private static double getAverageProductGeneric(Vector vec1, Vector vec2)
	{
		Vector v1;
		Vector v2;

		// Make sure we iterate over the shortest vector
		if (length(vec1) < length(vec2)) {
			v1 = vec1;
			v2 = vec2;
		}
		else {
			v2 = vec1;
			v1 = vec2;
		}

		double vectorProduct = 0.0;
		for (VectorEntry e : v1) {
			double value1 = e.get();
			double value2 = v2.get(e.index());
			vectorProduct += ((value1 + value2) / 2.0) * value1 * value2;
		}
		return vectorProduct;
	}

	private static double getAverageProductDense(DenseVector vec1, DenseVector vec2)
	{
		double vectorProduct = 0.0;
		double data1[] = vec1.getData();
		double data2[] = vec2.getData();
		for (int i = 0; i < data1.length; i++) {
			double value1 = data1[i];
			double value2 = data2[i];
			vectorProduct += ((value1 + value2) / 2.0) * value1 * value2;
		}
		return vectorProduct;
	}

	private static int length(Vector vec)
	{
		if (vec instanceof SparseVector) {
			return ((SparseVector) vec).getUsed();
		}
		else {
			return vec.size();
		}
	}
}
