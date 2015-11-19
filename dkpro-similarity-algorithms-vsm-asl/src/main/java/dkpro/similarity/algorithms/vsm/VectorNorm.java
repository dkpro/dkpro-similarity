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

import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;

/**
 * Vector normalization strategies.
 *
 * @author Richard Eckart de Castilho
 */
public enum VectorNorm
{
	/**
	 * Do not perform any normalization.
	 */
	NONE,

	/**
	 * Calculate the L1 norm (Manhattan norm).
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Norm_(mathematics)#Taxicab_norm_or_Manhattan_norm">Manhattan norm (Wikipedia)</a>
	 */
	L1,

	/**
	 * Calculate the L2 norm (euclidean norm).
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Norm_(mathematics)#Euclidean_norm">Euclidean Norm (Wikipedia)</a>
	 */
	L2;

	/**
	 * Calculates the norm of the given vector. If multiple vectors are given, calculates the
	 * product of the vector norms. If no vector is given, 1.0 is returned.
	 *
	 * @param aVectors a list of vectors.
	 * @return the vector norm or product of vector norms.
	 */
	public double apply(Vector... aVectors)
	{
		double result = 1.0;
		if (!this.equals(NONE)) {
			for (Vector v : aVectors) {
				switch (this) {
				case L1:
					result = result * v.norm(Norm.One);
					break;
				case L2:
					result = result * v.norm(Norm.TwoRobust);
					break;
				default:
					throw new IllegalStateException("Norm ["+this+"] not supported");
				}
			}
		}
		return result;
	}
}
