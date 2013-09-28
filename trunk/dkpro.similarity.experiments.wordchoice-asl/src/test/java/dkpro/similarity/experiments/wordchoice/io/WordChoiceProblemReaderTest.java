package dkpro.similarity.experiments.wordchoice.io;

import static org.junit.Assert.assertEquals;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import dkpro.similarity.type.WordChoiceProblem;

public class WordChoiceProblemReaderTest
{

    @Test
    public void wordchoiceTest()
        throws Exception
    {
        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                WordChoiceProblemReader.class,
                WordChoiceProblemReader.PARAM_SOURCE_LOCATION, "src/test/resources/datasets/wordchoice/",
                WordChoiceProblemReader.PARAM_PATTERNS, new String[] {
                    ResourceCollectionReaderBase.INCLUDE_PREFIX + "test.wcp"
                }
        );

        for (JCas jcas : new JCasIterable(reader)) {
            DocumentMetaData md = DocumentMetaData.get(jcas);
            System.out.println(md.getDocumentUri());

            assertEquals(1, JCasUtil.select(jcas, DocumentAnnotation.class).size());

            int i = 0;
            for (WordChoiceProblem wcp : JCasUtil.select(jcas, WordChoiceProblem.class)) {
                if (i == 0) {
                    assertEquals("Bijou",        wcp.getTarget());
                    assertEquals("Spitzbube",    wcp.getCandidate1());
                    assertEquals("Spielkarte",   wcp.getCandidate2());
                    assertEquals("Schmuckstück", wcp.getCandidate3());
                    assertEquals("Gaststätte",   wcp.getCandidate4());
                    assertEquals(3,              wcp.getCorrectAnswer());
                }
                i++;
            }
            assertEquals(3, i);
        }
    }
}