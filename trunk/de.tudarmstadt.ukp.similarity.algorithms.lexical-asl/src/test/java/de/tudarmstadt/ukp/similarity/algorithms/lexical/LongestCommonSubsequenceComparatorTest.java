package de.tudarmstadt.ukp.similarity.algorithms.lexical;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;

public class LongestCommonSubsequenceComparatorTest
{

    private static final double EPSILON = 0.000001;
    
    @Test
    public void testLCSC() throws Exception {
        LongestCommonSubsequenceComparator lcsc = new LongestCommonSubsequenceComparator();
        
        assertEquals(1.0, lcsc.getSimilarity("This is a test", "This is a test"), EPSILON);
        assertEquals(0.0, lcsc.getSimilarity("This is a test", "nono"), EPSILON);
    }
}
