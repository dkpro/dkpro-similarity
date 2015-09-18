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


public enum VectorAggregation
{
	/**
	 * Aggregates multiple vectors into one by simple addition.
	 *
	 * @see {@code SumVectorAggregation}
	 */
	SUM,

	/**
	 * Aggregates multiple vectors into one by addition and then dividing each element by the number
	 * of aggregated vectors.
	 *
	 * @see {@code CentroidVectorAggregation}
	 */
	CENTROID;

	public VectorAggregationStrategy newInstance()
	{
		switch (this) {
		case SUM:
			return VectorAggregationStrategy.getSum();
		case CENTROID:
			return VectorAggregationStrategy.getCentroid();
		default:
			throw new IllegalStateException("Vector aggregation ["+this+"] not supported");
		}
	}
}
