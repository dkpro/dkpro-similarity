package dkpro.similarity.algorithms.lexical.string;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dkpro.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubsequenceNormComparator;

public class LongestCommonSubsequenceComparatorTest
{

    private static final double EPSILON = 0.001;
    
    @Test
    public void testLCSC()
    	throws Exception
    {
        LongestCommonSubsequenceComparator lcsc = new LongestCommonSubsequenceComparator();
        
        assertEquals(1.0, lcsc.getSimilarity("This is a test", "This is a test"), EPSILON);
        assertEquals(0.6, lcsc.getSimilarity("This is a test", "a test"), EPSILON);
        assertEquals(0.0, lcsc.getSimilarity("This is a test", "nono"), EPSILON);
    }
    
    @Test
    public void testLCSCNorm()
    	throws Exception
    {
        LongestCommonSubsequenceComparator lcscN = new LongestCommonSubsequenceNormComparator();
        
        assertEquals(1.0, lcscN.getSimilarity("This is a test", "This is a test"), EPSILON);
        assertEquals(0.428, lcscN.getSimilarity("This is a test", "a test"), EPSILON);        
        assertEquals(0.0, lcscN.getSimilarity("This is a test", "nono"), EPSILON);
    }
}
