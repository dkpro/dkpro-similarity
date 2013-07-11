package de.tudarmstadt.ukp.similarity.algorithms.style;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;

/**
 * This measure computes the ratio of number of sentences in the
 * suspicious text compared to the number of sentences in the original
 * text:
 * 
 * sim(T_1,T_2) = no_sentences(T_1) / no_sentences(T_2)
 */
public class SentenceRatioComparator
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
		int no1 = JCasUtil.select(jcas1, Sentence.class).size(); 
		int no2 = JCasUtil.select(jcas2, Sentence.class).size();

		return computeSentenceRatio(no1, no2);
	}

    @Override
    public double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1,
            Annotation coveringAnnotation2)
        throws SimilarityException
    {
        int no1 = JCasUtil.selectCovered(jcas1, Sentence.class, coveringAnnotation1).size(); 
        int no2 = JCasUtil.selectCovered(jcas2, Sentence.class, coveringAnnotation2).size();

        return computeSentenceRatio(no1, no2);
    }

    private double computeSentenceRatio(int no1, int no2) {
        double sim = (no2 == 0) ? 0.0 : (double) no1 / no2;
        return sim;
    }
}