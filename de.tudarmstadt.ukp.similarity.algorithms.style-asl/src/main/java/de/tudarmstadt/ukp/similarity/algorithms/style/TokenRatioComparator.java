package de.tudarmstadt.ukp.similarity.algorithms.style;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;

/**
 * This measure computes the ratio of number of tokens in the
 * suspicious text compared to the number of tokens in the original
 * text:
 * 
 * sim(T_1,T_2) = no_tokens(T_1) / no_tokens(T_2)
 */
public class TokenRatioComparator
	extends JCasTextSimilarityMeasureBase
{	
	@Override
	public boolean isDistanceMeasure()
	{
		return true;
	}
	
	@Override
	public double getSimilarity(JCas jcas1, JCas jcas2)
		throws SimilarityException
	{		
		double no1 = new Integer(JCasUtil.select(jcas1, Token.class).size()).doubleValue(); 
		double no2 = new Integer(JCasUtil.select(jcas2, Token.class).size()).doubleValue();
		
		double sim = (no2 == 0.0) ? 0.0 : no1 / no2;
		
		return sim;
	}
}