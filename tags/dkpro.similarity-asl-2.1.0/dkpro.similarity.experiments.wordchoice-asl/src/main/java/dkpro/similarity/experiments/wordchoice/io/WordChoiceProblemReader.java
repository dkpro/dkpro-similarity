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
package dkpro.similarity.experiments.wordchoice.io;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import dkpro.similarity.type.WordChoiceProblem;

/**
 * Reads word choice problem datasets.
 * 
 * Annotates WordChoiceProblems in the source document text and writes token annotations.
 * Each candidate is also a sentence of its own.
 * 
 * @author zesch
 *
 */
public class WordChoiceProblemReader extends TextReader {
    
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
     * Read a file containing a list of word choice problems.
     * Expected format of the list:
     * - each problem on a single line
     * - candidates separated by a the value indicated by the
     *   separator parameter (default ":"), e.g. 'car:automobile'
     * - followed by an integer indicating the correct answer
     * For example: "target:cand1:cand2:cand3:cand4:correct"
     *
     * @param infile The file containing the word choice problems
     * @param aJCas The jcas.
     * @throws AnalysisEngineProcessException
     * @throws CollectionException 
     */
    private void processDocument(JCas jcas, String document) throws CollectionException {

        // split document into lines
        String[] lines = document.split(System.getProperty("line.separator"));

        int i = 0;
        int offset = 0;
        for (String line : lines) {
            i++;

            if (line.startsWith(commentChar)) {
                offset += line.length() + System.getProperty("line.separator").length();
                continue;
            }

            int wordChoiceProblemBegin = offset;
            int wordChoiceProblemEnd   = offset + line.length();

            String[] parts = line.split(separatorChar);
            if (parts.length != 6) {
                this.getLogger().log(Level.SEVERE, "Wrong file format:  " + line);
                throw new CollectionException(new Throwable("Wrong file format on line '" + i + " " + line + "'. It should be target:cand1:cand2:cand3:cand4:correct"));
            }

            // add token annotation and adjust start offset
            offset = addTokenAnnotations(jcas, parts, offset);

            String correctValueString = parts[5];
            int correct = 0;
            try {
                correct = new Integer(correctValueString);
            }
            catch (NumberFormatException e) {
                this.getLogger().log(Level.INFO, "Wrong number format: " + correctValueString);
                offset += line.length() + System.getProperty("line.separator").length();
                continue;
            }
            offset += correctValueString.length();

            WordChoiceProblem wordChoiceProblem = new WordChoiceProblem(
                    jcas,
                    wordChoiceProblemBegin,
                    wordChoiceProblemEnd
            );
            wordChoiceProblem.setTarget(parts[0]);
            wordChoiceProblem.setCandidate1(parts[1]);
            wordChoiceProblem.setCandidate2(parts[2]);
            wordChoiceProblem.setCandidate3(parts[3]);
            wordChoiceProblem.setCandidate4(parts[4]);
            wordChoiceProblem.setCorrectAnswer(correct);

            wordChoiceProblem.addToIndexes();

            offset += System.getProperty("line.separator").length();
        }
    }

    private int addTokenAnnotations(JCas jcas, String[] parts, int offset) {
        for (int i=0; i<=4; i++) {
            Sentence s = new Sentence(jcas,offset,offset+parts[i].length());
            s.addToIndexes();

            String[] tokens = parts[i].split(" ");
            for (String token : tokens) {
                int begin = offset;
                int end = begin + token.length();

                Token tokenAnnotation = new Token(jcas, begin, end);

                tokenAnnotation.addToIndexes();

                offset = end + 1;

                if (!tokenAnnotation.getCoveredText().equals(token)) {
                    System.out.println(tokenAnnotation.getCoveredText());
                    throw new RuntimeException("ups - " + tokenAnnotation.getCoveredText());
                }
            }
        }
        return offset;
    }
}