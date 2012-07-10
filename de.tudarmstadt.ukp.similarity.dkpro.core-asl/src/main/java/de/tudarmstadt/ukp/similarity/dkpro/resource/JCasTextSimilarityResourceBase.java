package de.tudarmstadt.ukp.similarity.dkpro.resource;

import java.util.Collection;

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;


public class JCasTextSimilarityResourceBase
	extends TextSimilarityResourceBase
	implements JCasTextSimilarityMeasure
{
	protected JCasTextSimilarityMeasure measure;
	 
	@Override
	public double getSimilarity(JCas jcas1, JCas jcas2)
		throws SimilarityException
	{
		return measure.getSimilarity(jcas1, jcas2);
	}

	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		return measure.getSimilarity(stringList1, stringList2);
	}

	@Override
	public void beginMassOperation()
	{
		measure.beginMassOperation();
	}

	@Override
	public void endMassOperation()
	{
		measure.endMassOperation();
	}

	@Override
	public double getSimilarity(String string1, String string2)
		throws SimilarityException
	{
		return measure.getSimilarity(string1, string2);
	}

	@Override
	public String getName()
	{
		return measure.getName();
	}

	@Override
	public boolean isDistanceMeasure()
	{
		return measure.isDistanceMeasure();
	}

}
