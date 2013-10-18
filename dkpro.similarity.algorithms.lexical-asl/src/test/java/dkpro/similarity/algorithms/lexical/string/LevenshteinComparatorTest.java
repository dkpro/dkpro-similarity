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

import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;

public class LevenshteinComparatorTest
{
    private static final double epsilon = 0.0001;

    @Test
    public void test()
        throws Exception
    {
        String a1 = "test String1";
        String a2 = "test String2";

        String b1 = "This is my string";
        String b2 = "That is your string";

        TermSimilarityMeasure measure = new LevenshteinComparator();

        assertEquals(1, measure.getSimilarity(a1, a2), epsilon);
        assertEquals(6, measure.getSimilarity(b1, b2), epsilon);
    }

    @Test
    public void testCollection()
        throws Exception
    {
        String[] a1 = "test String1".split(" ");
        String[] a2 = "test String2".split(" ");

        String[] b1 = "This is my string".split(" ");
        String[] b2 = "That is your string".split(" ");

        TextSimilarityMeasure measure = new LevenshteinComparator();

        assertEquals(1, measure.getSimilarity(a1, a2), epsilon);
        assertEquals(6, measure.getSimilarity(b1, b2), epsilon);
    }
}
