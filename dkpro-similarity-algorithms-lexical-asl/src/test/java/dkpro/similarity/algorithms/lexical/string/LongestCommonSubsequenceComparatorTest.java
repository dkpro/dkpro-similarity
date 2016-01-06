/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
