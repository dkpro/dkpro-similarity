package de.tudarmstadt.ukp.similarity.experiments.semeval2013.example;

import java.util.Collection;

import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasureBase;

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
