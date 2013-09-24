/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 ******************************************************************************/
package dkpro.similarity.experiments.sts2013baseline.example;

import java.util.Collection;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

public class MyTextSimilarityMeasure
	extends TextSimilarityMeasureBase
{
	@SuppressWarnings("unused")
	private int n;
	
	public MyTextSimilarityMeasure(int n)
	{
		// The configuration parameter is not used right now and intended for illustration purposes only.
		this.n = n;
	}
	
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		// Your similarity computation goes here.
		return 1.0;
	}

}
