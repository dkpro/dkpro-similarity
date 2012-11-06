package de.tudarmstadt.ukp.similarity.experiments.semeval2013.example;

import java.util.Collection;

import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasureBase;

public class MyTextSimilarityMeasure
	extends TextSimilarityMeasureBase
{
	int n;
	
	public MyTextSimilarityMeasure(int n)
	{
		this.n = n;
	}
	
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
