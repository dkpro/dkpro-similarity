package dkpro.similarity.algorithms.style;

import java.util.List;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * Computes the average number of tokens per sentence.
 *
 */
public class AvgTokensPerSentenceMeasure
	extends JCasTextSimilarityMeasureBase
{
	@Override
    public double getSimilarity(JCas jcas1, JCas jcas2,
    		Annotation coveringAnnotation1, Annotation coveringAnnotation2)
        throws SimilarityException
    {
		List<Sentence> s1 = JCasUtil.selectCovered(jcas1, Sentence.class, coveringAnnotation1);
		List<Sentence> s2 = JCasUtil.selectCovered(jcas2, Sentence.class, coveringAnnotation2);
		
		int noOfTokens = 0;
		
		for (Sentence sentence : s1) {
            noOfTokens += JCasUtil.selectCovered(jcas1, Token.class, sentence).size();
        }
		for (Sentence sentence : s2) {
            noOfTokens += JCasUtil.selectCovered(jcas1, Token.class, sentence).size();
        }
		
		return (double) noOfTokens / (s1.size() + s2.size());
    }
}