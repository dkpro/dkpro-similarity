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

/**
 * Vector aggregation strategies. Having this class allows to create custom aggregation strategies
 * without having to modifiy the framework sourcecode. You will usually want to use the more
 * convenient {@link VectorAggregation} enumeration to select the strategy.
 *
 * @author Richard Eckart de Castilho
 */
public abstract class VectorAggregationStrategy
{
	/**
	 * Initialize with the given vector.
	 */
	public abstract void init(Vector aVector);

	public abstract boolean isInitialized();

	/**
	 * Get the aggregated vector.
	 */
	public abstract Vector get();

	/**
	 * Add the specified vector.
	 */
	public abstract void add(Vector aVector);

	/**
	 * Add the specified vector x times.
	 */
	public abstract void add(int aTimes, Vector aVector);

	/**
	 * Create a fresh instance of the aggregator. This is useful if you want to create thread
	 * safe code and need to create throw-away aggregator instances from a template.
	 */
	public abstract VectorAggregationStrategy newInstance();

	/**
	 * Sums up vectors.
	 */
	public static VectorAggregationStrategy getSum()
	{
		return new SumVectorAggregation();
	}

	/**
	 * Sums up vectors and divides result by number of vectors.
	 */
	public static VectorAggregationStrategy getCentroid()
	{
		return new CentroidVectorAggregation();
	}

	public static class SumVectorAggregation extends VectorAggregationStrategy
	{
		private Vector vector;

		@Override
		public void init(Vector aVector)
		{
			vector = aVector;
		}

		@Override
		public boolean isInitialized()
		{
			return vector != null;
		}

		@Override
		public Vector get()
		{
			return vector.copy();
		}

		@Override
		public void add(Vector aVector)
		{
			add(1, aVector);
		}

		@Override
		public void add(int aTimes, Vector aVector)
		{
			vector.add(aTimes, aVector);
		}

		@Override
		public VectorAggregationStrategy newInstance()
		{
			return new SumVectorAggregation();
		}
	}

	public static class CentroidVectorAggregation extends SumVectorAggregation
	{
		private int count = 0;

		@Override
		public void add(int aTimes, Vector aVector)
		{
			count += aTimes;
			super.add(aTimes, aVector);
		}

		@Override
		public Vector get()
		{
			return super.get().scale(1./count);
		}


		@Override
		public VectorAggregationStrategy newInstance()
		{
			return new CentroidVectorAggregation();
		}
	}}
