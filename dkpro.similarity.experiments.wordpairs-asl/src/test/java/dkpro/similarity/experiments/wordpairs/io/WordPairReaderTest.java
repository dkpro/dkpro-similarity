package dkpro.similarity.experiments.wordpairs.io;

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
import dkpro.similarity.experiments.wordpairs.io.WordPairReader;
import dkpro.similarity.type.SemRelWordPair;

public class WordPairReaderTest
{

    @Test
    public void wordpairTest()
        throws Exception
    {
        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                WordPairReader.class,
                WordPairReader.PARAM_SOURCE_LOCATION, "src/test/resources/datasets/wordpairs/",
                WordPairReader.PARAM_PATTERNS, new String[] {
                    ResourceCollectionReaderBase.INCLUDE_PREFIX + "test.srd"
                }
        );

        for (JCas jcas : new JCasIterable(reader)) {
            DocumentMetaData md = DocumentMetaData.get(jcas);
            System.out.println(md.getDocumentUri());

            assertEquals(1, JCasUtil.select(jcas, DocumentAnnotation.class).size());

            int i = 0;
            for (SemRelWordPair pair : JCasUtil.select(jcas, SemRelWordPair.class)) {
                if (i == 0) {
                    assertEquals("car", pair.getWord1());
                    assertEquals("automobile", pair.getWord2());
                    assertEquals("n", pair.getPos1().getPosValue());
                    assertEquals("n", pair.getPos2().getPosValue());
                }
                else if (i == 1) {
                    assertEquals("go", pair.getWord1());
                    assertEquals("fast", pair.getWord2());
                    assertEquals("v", pair.getPos1().getPosValue());
                    assertEquals("a", pair.getPos2().getPosValue());
                }
                i++;
            }
            assertEquals(2, i);
        }
    }

    @Test
    public void separatorTest()
        throws Exception
    {

        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                WordPairReader.class,
                WordPairReader.PARAM_SOURCE_LOCATION, "src/test/resources/datasets/wordpairs/",
                WordPairReader.PARAM_COMMENT, "%",
                WordPairReader.PARAM_SEPARATOR, ";", 
                WordPairReader.PARAM_PATTERNS, new String[] {
                    ResourceCollectionReaderBase.INCLUDE_PREFIX + "separator.srd"
                }
        );

        for (JCas jcas : new JCasIterable(reader)) {
            DocumentMetaData md = DocumentMetaData.get(jcas);
            System.out.println(md.getDocumentUri());

            assertEquals(1, JCasUtil.select(jcas, DocumentAnnotation.class).size());

            int i = 0;
            for (SemRelWordPair pair : JCasUtil.select(jcas, SemRelWordPair.class)) {
                assertEquals("go", pair.getWord1());
                assertEquals("fast", pair.getWord2());
                assertEquals("v", pair.getPos1().getPosValue());
                assertEquals("a", pair.getPos2().getPosValue());
                i++;
            }
            assertEquals(1, i);
        }
    }
}
