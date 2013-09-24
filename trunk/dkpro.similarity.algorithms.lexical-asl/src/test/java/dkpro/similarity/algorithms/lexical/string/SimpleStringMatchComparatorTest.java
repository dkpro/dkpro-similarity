/*******************************************************************************
 * Copyright 2012
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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.string.BoundedSubstringMatchComparator;
import dkpro.similarity.algorithms.lexical.string.ExactStringMatchComparator;
import dkpro.similarity.algorithms.lexical.string.SubstringMatchComparator;

public class SimpleStringMatchComparatorTest {

    private static final double epsilon = 0.00001;

    @Test
    public void comparatorTest() throws SimilarityException
    {
        String s1 = "Tester";
        String s2 = "Test";
        String s3 = "West";
        String s4 = "Vorname";
        String s5 = "Nachname";
        String s6 = "Name";
//        String s7 = "Abfahrt";
        String s8 = "Abflug";

        String m1 = "Test the West";
        String m2 = "This is a test";
        String m3 = "Ihr Abflughafen";
        String m4 = "Abflug";

        List<String> l1 = Arrays.asList(m1.split(" "));
        List<String> l2 = Arrays.asList(m2.split(" "));
        List<String> l3 = Arrays.asList(m3.split(" "));
        List<String> l4 = Arrays.asList(m4.split(" "));

        // Exact string match
        TextSimilarityMeasure exact = new ExactStringMatchComparator();

        assertEquals(1.0, exact.getSimilarity(s1, s1), epsilon);
        assertEquals(0.0, exact.getSimilarity(s1, s2), epsilon);
        assertEquals(0.0, exact.getSimilarity(s1, s3), epsilon);
        assertEquals(0.0, exact.getSimilarity(s2, s3), epsilon);
        assertEquals(0.0, exact.getSimilarity(s4, s5), epsilon);
        assertEquals(0.0, exact.getSimilarity(s4, s6), epsilon);
        assertEquals(0.0, exact.getSimilarity(s5, s6), epsilon);
        assertEquals(0.0, exact.getSimilarity(s6, s8), epsilon);
        assertEquals(0.0, exact.getSimilarity(m1, m2), epsilon);
        assertEquals(0.0, exact.getSimilarity(l1, l2), epsilon);
        assertEquals(1.0, exact.getSimilarity(m3, m3), epsilon);
        assertEquals(1.0, exact.getSimilarity(l3, l3), epsilon);

        // Substring match
        TextSimilarityMeasure substring = new SubstringMatchComparator();

        assertEquals(1.0, substring.getSimilarity(s1, s1), epsilon);
        assertEquals(1.0, substring.getSimilarity(s1, s2), epsilon);
        assertEquals(0.0, substring.getSimilarity(s1, s3), epsilon);
        assertEquals(0.0, substring.getSimilarity(s2, s3), epsilon);
        assertEquals(0.0, substring.getSimilarity(s4, s5), epsilon);
        assertEquals(0.0, substring.getSimilarity(s4, s6), epsilon);
        assertEquals(0.0, substring.getSimilarity(s5, s6), epsilon);
        assertEquals(0.0, substring.getSimilarity(s6, s8), epsilon);
        assertEquals(0.0, substring.getSimilarity(m1, m2), epsilon);
        assertEquals(0.0, substring.getSimilarity(l1, l2), epsilon);
        assertEquals(1.0, substring.getSimilarity(m3, m3), epsilon);
        assertEquals(1.0, substring.getSimilarity(l3, l3), epsilon);
        assertEquals(1.0, substring.getSimilarity(m3, m4), epsilon);
        assertEquals(1.0, substring.getSimilarity(l3, l4), epsilon);

        // Bounded substring match
        TextSimilarityMeasure bsubstring = new BoundedSubstringMatchComparator();

        assertEquals(1.0, bsubstring.getSimilarity(s1, s1), epsilon);
        assertEquals(1.0, bsubstring.getSimilarity(s1, s2), epsilon);
        assertEquals(0.0, bsubstring.getSimilarity(s1, s3), epsilon);
        assertEquals(0.0, bsubstring.getSimilarity(s2, s3), epsilon);
        assertEquals(0.0, bsubstring.getSimilarity(s4, s5), epsilon);
        assertEquals(1.0, bsubstring.getSimilarity(s4, s6), epsilon);
        assertEquals(1.0, bsubstring.getSimilarity(s5, s6), epsilon);
        assertEquals(0.0, bsubstring.getSimilarity(s6, s8), epsilon);
        assertEquals(0.0, bsubstring.getSimilarity(m1, m2), epsilon);

        // it also matches "is" and "this"!
        assertEquals(1.0, bsubstring.getSimilarity(l1, l2), epsilon);
        assertEquals(1.0, bsubstring.getSimilarity(m3, m3), epsilon);
        assertEquals(1.0, bsubstring.getSimilarity(l3, l3), epsilon);
        assertEquals(0.0, bsubstring.getSimilarity(m3, m4), epsilon);
        assertEquals(1.0, bsubstring.getSimilarity(l3, l4), epsilon);
    }
}