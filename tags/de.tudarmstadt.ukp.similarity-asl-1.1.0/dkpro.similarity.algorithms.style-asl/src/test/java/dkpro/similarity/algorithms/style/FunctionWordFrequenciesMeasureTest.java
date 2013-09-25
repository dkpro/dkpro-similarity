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
package dkpro.similarity.algorithms.style;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasBuilder;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasure;
import dkpro.similarity.algorithms.style.FunctionWordFrequenciesMeasure;

public class FunctionWordFrequenciesMeasureTest
{
    static final double epsilon = 0.001;

    @Test
    public void run()
        throws Exception
    {
        JCasTextSimilarityMeasure comparator = new FunctionWordFrequenciesMeasure();

        AnalysisEngine ae = AnalysisEngineFactory.createPrimitive(BreakIteratorSegmenter.class);

        // First document
        JCasBuilder cb1 = new JCasBuilder(ae.newJCas());
        cb1.add("a", Token.class);
        cb1.add("your", Token.class);
        cb1.add("were", Token.class);
        cb1.close();

        // Second document
        JCasBuilder cb2 = new JCasBuilder(ae.newJCas());
        cb2.add("this", Token.class);
        cb2.add("is", Token.class);
        cb2.add("a", Token.class);
        cb2.close();

        assertEquals(0.303, comparator.getSimilarity(cb1.getJCas(), cb2.getJCas()), epsilon);
    }
}
