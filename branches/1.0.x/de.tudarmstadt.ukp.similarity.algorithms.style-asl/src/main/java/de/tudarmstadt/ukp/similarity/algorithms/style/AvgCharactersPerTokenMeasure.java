package de.tudarmstadt.ukp.similarity.algorithms.style;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;


/**
 * Computes the average number of characters per token.
 */
public class AvgCharactersPerTokenMeasure
	extends JCasTextSimilarityMeasureBase
{
    public double getSimilarity(JCas jcas1, JCas jcas2,
    		Annotation coveringAnnotation1, Annotation coveringAnnotation2)
        throws SimilarityException
    {
		List<Token> t1 = JCasUtil.selectCovered(jcas1, Token.class, coveringAnnotation1);
		List<Token> t2 = JCasUtil.selectCovered(jcas2, Token.class, coveringAnnotation2);
		
		int noOfCharacters = 0;
		
		for (Token token : t1)
			noOfCharacters += token.getCoveredText().length();
		for (Token token : t2)
			noOfCharacters += token.getCoveredText().length();
		
		return new Double(noOfCharacters) / new Double(t1.size() + t2.size());
    }
}