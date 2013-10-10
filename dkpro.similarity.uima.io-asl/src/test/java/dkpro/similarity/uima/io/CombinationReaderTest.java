package dkpro.similarity.uima.io;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import dkpro.similarity.uima.io.CombinationReader.CombinationStrategy;

public class CombinationReaderTest
{
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
        
        assertEquals(i, 2);
    }
}