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
package dkpro.similarity.uima.vsm;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import no.uib.cipr.matrix.Vector;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.algorithms.vsm.store.vectorindex.VectorIndexReader;
import dkpro.similarity.uima.vsm.esaindexer.IndexInverter;
import dkpro.similarity.uima.vsm.esaindexer.LuceneIndexer;

public class EsaIndexCreatorTest {
	
    private final static String luceneIndexPath = "target/lucene";
    private final static String esaIndexPath = "target/esa";
    
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
        
        AnalysisEngine stemmer = createEngine(
                SnowballStemmer.class,
                SnowballStemmer.PARAM_LANGUAGE, "en",
                SnowballStemmer.PARAM_LOWER_CASE, true);
        
        AnalysisEngine indexTermGenerator = createEngine(
                LuceneIndexer.class,
                LuceneIndexer.PARAM_INDEX_PATH, luceneIndexPath,
                LuceneIndexer.PARAM_MIN_TERMS_PER_DOCUMENT, 1,
                LuceneIndexer.PARAM_FEATURE_PATH, Stem.class.getName() + "/value");


        SimplePipeline.runPipeline(reader, segmenter, stemmer, indexTermGenerator);        

        IndexInverter indexInverter = new IndexInverter(new File(luceneIndexPath), new File(esaIndexPath));
        indexInverter.createInvertedIndex();
        
        VectorIndexReader esaReader = new VectorIndexReader(new File(esaIndexPath));
        Vector vo = esaReader.getVector("test");
        reader.close();

        assertNotNull(vo);

        assertEquals(2, vo.size());
    }
}