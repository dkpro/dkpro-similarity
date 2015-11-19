package dkpro.similarity.algorithms.lexical.string;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.string.HultigSumoComparator;

public class HultigSumoComparatorTest
{

    @Test
    public void hultigSumoTest()
        throws Exception
    {
        String text1 = "This is a test .";
        String text2 = "This is a second different test .";

        List<String> tokens1 = Arrays.asList(text1.split(" "));
        List<String> tokens2 = Arrays.asList(text2.split(" "));
        
        TextSimilarityMeasure measure = new HultigSumoComparator();
        assertEquals(1.0, measure.getSimilarity(tokens1, tokens1), 0.000001);
        assertEquals(0.75729, measure.getSimilarity(tokens1, tokens2), 0.00001);
    }
}
