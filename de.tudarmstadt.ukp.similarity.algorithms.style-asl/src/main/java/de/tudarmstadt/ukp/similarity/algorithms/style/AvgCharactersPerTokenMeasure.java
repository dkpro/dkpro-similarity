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
 * This measure ignores the second parameters which are given to the getRelatedness
 * methods.
 * @author Daniel Bär
 *
 */
public class AvgCharactersPerTokenMeasure
	extends JCasTextSimilarityMeasureBase
{
	@Override
	public double getSimilarity(JCas jcas1, JCas jcas2)
		throws SimilarityException
	{		
		DocumentAnnotation doc1 = new ArrayList<DocumentAnnotation>(JCasUtil.select(jcas1, DocumentAnnotation.class)).get(0);
		List<Token> tokens = JCasUtil.selectCovered(jcas1, Token.class, doc1);
		
		int noOfCharacters = 0;
		
		for (Token token : tokens)
		{
			noOfCharacters += token.getCoveredText().length();
		}
		
		// Compute property
		double avgNoOfCharactersPerToken = new Double(noOfCharacters) / new Double(tokens.size());
		
		return avgNoOfCharactersPerToken;
	}

	// FIXME this should be properly implemented 
    @Override
    public double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1,
            Annotation coveringAnnotation2)
        throws SimilarityException
    {
        return getSimilarity(jcas1, jcas2);
    }
}