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
package dkpro.similarity.algorithms.sound;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.sound.DoubleMetaphoneComparator;

public class DoubleMetaphoneComparatorTest
{
    private static final double epsilon = 0.0001;

    @Test
    public void doubleMetaphoneTest()
        throws Exception
    {
        TermSimilarityMeasure comparator = new DoubleMetaphoneComparator();

        assertEquals(1.0, comparator.getSimilarity("knight", "knight"), epsilon);
        assertEquals(1.0, comparator.getSimilarity("knight", "night"), epsilon);
        assertEquals(0.5, comparator.getSimilarity("goal", "jail"), epsilon);
        assertEquals(0.0, comparator.getSimilarity("best", "car"), epsilon);
    }
}
