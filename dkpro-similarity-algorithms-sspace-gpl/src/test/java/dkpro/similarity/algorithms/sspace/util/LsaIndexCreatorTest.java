/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package dkpro.similarity.algorithms.sspace.util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.SemanticSpaceIO;

public class LsaIndexCreatorTest {
	    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
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
                LsaIndexer.PARAM_INDEX_PATH, folder.getRoot(),
                LsaIndexer.PARAM_FEATURE_PATH, Token.class.getName());


        SimplePipeline.runPipeline(reader, segmenter, indexTermGenerator);
        

        File tmpFile = new File(folder.getRoot(), "test.sspace");
        SemanticSpace sspace = SemanticSpaceIO.load(tmpFile);
        assertEquals(11, sspace.getVectorLength());
        assertEquals(513, sspace.getWords().size());    
    }
}