package de.tudarmstadt.ukp.similarity.algorithms.lexical.string;

import hultig.sumo.Sentence;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

/**
 * Wrapper for the SUMO text similarity measure.
 * 
 * Cordeiro, J.P., Dias, G., Brazdil, P. (2007).
 * A Metric for Paraphrase Detection.
 * 2nd International Multi-Conference on Computing in the Global Information Technology.
 * IEEE Computer Society Press. Guadeloupe, France.
 *  
 * http://www.di.ubi.pt/~jpaulo/hultiglib/index.html
 * 
 * @author zesch
 *
 */
public class HultigSumoComparator
    extends TextSimilarityMeasureBase
{

    @Override
    public double getSimilarity(Collection<String> stringList1, Collection<String> stringList2)
        throws SimilarityException
    {
            Sentence s1 = new Sentence(StringUtils.join(stringList1, " "));
            Sentence s2 = new Sentence(StringUtils.join(stringList2, " "));

            double similarity = 1.0 - s1.dsumo(s2);
            
            return similarity;
    }

}
