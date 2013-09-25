/*******************************************************************************
 * Copyright 2013
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
 ******************************************************************************/
package dkpro.similarity.experiments.wordpairs.io;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import dkpro.similarity.type.SemRelWordPair;

/**
 * Reads semantic relatedness word pair datasets.
 * 
 * @author zesch
 *
 */
public class WordPairReader extends TextReader {
    
    /**
     * Character that separates entries of a word pair.
     */
    public static final String PARAM_SEPARATOR = "SeparatorCharacter";
    @ConfigurationParameter(name = PARAM_SEPARATOR, mandatory = true, defaultValue = ":")
    private String separatorChar;
    
    /**
     * Character that indicates a comment.
     */
    public static final String PARAM_COMMENT = "CommentCharacter";
    @ConfigurationParameter(name = PARAM_COMMENT, mandatory = true, defaultValue = "#")
    private String commentChar;
    
    private static final String LINE_SEP = "\n";

	@Override
    public void getNext(CAS cas) throws IOException, CollectionException {
		super.getNext(cas);
	    
        JCas jcas = null;
        try {
            jcas = cas.getJCas();
        } catch (CASException e) {
            throw new CollectionException(e);
        }

        String document = cas.getDocumentText();
        if (document.length() > 0) {
            processDocument(jcas, document);
        }
        else {
            this.getLogger().log(Level.WARNING, "Document is empty");
        }
    }

    /**
     * Processes a text containing a word pair list.
     * Expected format of the list:
     *   - each pair on a single line
     *   - words separated by a the value indicated by the separator parameter (default ":"), e.g. 'car:automobile'
     *   - followed by the gold standard value and two pos tags from the set (n,v,a)
     *   - everything starting with the value indicated by the comment parameter (default "#") is ignored
     * @param cas The CAS.
     * @param document The document text to process.
     * @throws AnalysisEngineProcessException
     */
    private void processDocument(JCas jcas, String document) throws CollectionException {

        // split document into lines
        String[] lines = document.split(LINE_SEP);

        int i = 0;
        int startOffset = 0;
        for (String line : lines) {
            i++;

            if (line.startsWith(commentChar)) {
                startOffset += line.length() + LINE_SEP.length();
                continue;
            }

            String[] parts = line.split(separatorChar);
            if (parts.length < 2 || parts.length > 5) {
                this.getLogger().log(Level.SEVERE, "Wrong file format:  " + line);
                throw new CollectionException (
                		new Throwable("Wrong file format on line '" + i + " " + line 
                				+ "'. It should be word1:word2:gold[:pos1:pos2]")
                 );
            }

            Token token1 = new Token(jcas);
            token1.setBegin(startOffset);
            token1.setEnd((startOffset + parts[0].length()));
            token1.addToIndexes();

            Token token2 = new Token(jcas);
            token2.setBegin(startOffset + parts[0].length() + 1);
            token2.setEnd(startOffset + parts[0].length() + 1 + parts[1].length());
            token2.addToIndexes();

            String goldValueString = parts[2];
            double goldValue = -1.0;
            try {
                goldValue = new Double(goldValueString);
            }
            catch (NumberFormatException e) {
                this.getLogger().log(Level.INFO, "Wrong number format: " + goldValueString);
                startOffset += line.length() + LINE_SEP.length();
                continue;
            }

            POS pos1 = new POS(
            		jcas,
            		token1.getBegin(),
            		token1.getEnd()
            );
            pos1.setPosValue(parts[3]);
            pos1.addToIndexes();

            POS pos2 = new POS(
            		jcas,
            		token2.getBegin(),
            		token2.getEnd()
            );
            pos2.setPosValue(StringUtils.chomp(parts[4]));
            pos2.addToIndexes();

            SemRelWordPair wordPairAnnotation = new SemRelWordPair(jcas);
            wordPairAnnotation.setBegin(token1.getBegin());
            wordPairAnnotation.setEnd(token2.getEnd());
            wordPairAnnotation.setWord1(token1.getCoveredText());
            wordPairAnnotation.setWord2(token2.getCoveredText());
            wordPairAnnotation.setToken1(token1);
            wordPairAnnotation.setToken2(token2);
            wordPairAnnotation.setPos1(pos1);
            wordPairAnnotation.setPos2(pos2);
            wordPairAnnotation.setGoldValue(goldValue);

            wordPairAnnotation.addToIndexes();

            startOffset += line.length() + LINE_SEP.length();
        }
    }
}