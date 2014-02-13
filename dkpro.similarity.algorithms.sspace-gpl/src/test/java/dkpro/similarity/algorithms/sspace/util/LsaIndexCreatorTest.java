/*******************************************************************************
 * Copyright 2013 Mateusz Parzonka
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
package dkpro.similarity.algorithms.sspace.util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.SemanticSpaceIO;

public class LsaIndexCreatorTest {
	
    private final static String targetPath = "target/lsa/";
    
    //FIXME fails on jenkins - I have no idea why
    @Ignore
    @Test
    public void testIndexCreation()
        throws Exception
    {
     
        CollectionReader reader = createReader(
                TextReader.class,
                TextReader.PARAM_SOURCE_LOCATION, "src/test/resources/input/",
                TextReader.PARAM_PATTERNS, "*.txt");
        
        AnalysisEngine segmenter = createEngine(
                BreakIteratorSegmenter.class,
                BreakIteratorSegmenter.PARAM_LANGUAGE, "en");
        
        AnalysisEngine indexTermGenerator = createEngine(
                LsaIndexer.class,
                LsaIndexer.PARAM_INDEX_PATH, targetPath,
                LsaIndexer.PARAM_FEATURE_PATH, Token.class.getName());


        SimplePipeline.runPipeline(reader, segmenter, indexTermGenerator);
        
        SemanticSpace sspace = SemanticSpaceIO.load(new File(targetPath, "test.sspace"));
        assertEquals(1, sspace.getVectorLength());
        assertEquals(7, sspace.getWords().size());

        
    }
}