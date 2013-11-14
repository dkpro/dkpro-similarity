package dkpro.similarity.uima.vsm.esaindexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import dkpro.similarity.algorithms.vsm.store.LuceneVectorReader;

public class SenseEnabledLuceneIndexer
    extends LuceneIndexer
{
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        final List<String> terms = new ArrayList<String>();
        // aggregate relevant terms
        for (Token token : JCasUtil.select(jCas, Token.class)) {
            final String term = token.getCoveredText();
            if (isRelevant(term)) {
                terms.add(term);
            }
        }
        // index all terms if the document is long enough
        if (terms.size() > minTermsPerDocument) {
            final Document doc = new Document();
            for (String term : terms) {
                doc.add(new Field(LuceneVectorReader.FIELD_NAME, term, Field.Store.YES, Field.Index.NOT_ANALYZED));
            }
            doc.add(new Field("id", DocumentMetaData.get(jCas).getDocumentTitle(), Field.Store.YES,
                    Field.Index.NOT_ANALYZED));
            try {
                indexWriter.addDocument(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected boolean isRelevant(String term) {
        return true;
    }


}
