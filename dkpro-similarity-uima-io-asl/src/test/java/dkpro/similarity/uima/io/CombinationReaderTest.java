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
package dkpro.similarity.uima.io;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.jcas.JCas;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import dkpro.similarity.uima.io.CombinationReader.CombinationStrategy;

public class CombinationReaderTest
{
    // TODO this is currently failing as listFiles cannot work with the classpath argument in PARAM_INPUT_DIR when on jenkins
    @Ignore
    @Test
    public void combinationReaderTest() throws Exception {
        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(PlainTextCombinationReader.class,
                PlainTextCombinationReader.PARAM_INPUT_DIR, "classpath:/datasets/test/plaintext",
                PlainTextCombinationReader.PARAM_LANGUAGE, "en",
                PlainTextCombinationReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());

        AnalysisEngine engine = AnalysisEngineFactory.createEngine(
                XmiWriter.class,
                XmiWriter.PARAM_TARGET_LOCATION, "target/test"
        );
        
        int i = 0;
        for (JCas jcas : new JCasIterable(reader)) {
            engine.process(jcas);
            i++;
        }
        
        assertEquals(2, i);
    }
}