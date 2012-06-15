package de.tudarmstadt.ukp.similarity.algorithms.style;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;

/**
 * This measure computes TTR for both texts, and compares them as follows:
 * sim(T_1,T_2) = 1 - |ttr(T_1) - ttr(T_2)|
 */
public class TypeTokenRatioComparator
	extends JCasTextSimilarityMeasureBase
{
	@Override
	public double getSimilarity(JCas jcas1, JCas jcas2)
		throws SimilarityException
	{				
		double ttr1 = getTTR(jcas1);
		double ttr2 = getTTR(jcas2);
		
		double distance;
		if (ttr1 > ttr2)
			distance = ttr1 - ttr2;
		else
			distance = ttr2 - ttr1;
		
		return 1.0 - distance;
	}
	
	private double getTTR(JCas jcas)
	{
		DocumentAnnotation doc1 = new ArrayList<DocumentAnnotation>(JCasUtil.select(jcas, DocumentAnnotation.class)).get(0);
		List<Sentence> sentences = JCasUtil.selectCovered(jcas, Sentence.class, doc1);
		
		List<String> tokens = new ArrayList<String>();
		Set<String> types = new HashSet<String>();
		
		for (Sentence sentence : sentences)
		{
			List<Token> theseTokens = JCasUtil.selectCovered(jcas, Token.class, sentence);
			List<Lemma> theseLemmas = JCasUtil.selectCovered(jcas, Lemma.class, sentence);
			
			for (Token token : theseTokens)
				tokens.add(token.getCoveredText().toLowerCase());
			
			for (Lemma lemma : theseLemmas)
				types.add(lemma.getValue().toLowerCase());
		}
		
		// Compute property
		double ttr = new Double(types.size()) / new Double(tokens.size());
		
		return ttr;
	}
}
