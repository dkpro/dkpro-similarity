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
package dkpro.similarity.algorithms.wikipedia.measures;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Title;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

public abstract class DefinitionBasedComparator
    extends WikipediaSimilarityMeasureBase
{

    public DefinitionBasedComparator(Wikipedia pWiki, Measure pMeasure, CombinationStrategy pStrategy) {
        super(pWiki, pMeasure, pStrategy);
    }

    /**
     * @param page The page object.
     * @return Returns the first paragraph of a given page.
     */
    public String getFirstParagraph(Page page)  throws WikiApiException {
        String fullText = getFullArticle(page);
        return findFirstParagraph(fullText, page.getTitle());
    }

    /**
     * @param page The page object.
     * @return Returns the full gloss (the full text) of a given page.
     */
    public String getFullArticle(Page page) {
        return page.getText();
    }

    /**
     * @param text The text to determine its length.
     * @return The length of the text (= the number of tokens in the text).
     */
    public int getLength(String text) {
        List<String> tokenList = tokenizeText(text);
        return tokenList.size();
    }

    /**
     * @param text The text to tokenize.
     * @return Returns a list with the tokens of the texts.
     */
    protected List<String> tokenizeText(String text) {
        List<String> tokens = new ArrayList<String>();

        BreakIterator wordIterator = BreakIterator.getWordInstance();
        wordIterator.setText(text);
        int start = wordIterator.first();
        int end = wordIterator.next();

        while (end != BreakIterator.DONE) {
            String token = text.substring(start, end);
            if (!Character.isWhitespace(token.charAt(0)) && !token.matches("[\\.|,|$|€]")) {
                tokens.add(token);
            }
            start = end;
            end = wordIterator.next();
        }

        return tokens;
    }


    /**
     * Produces a map of tokens and their frequency.
     * Also removes special characters that may be attached to tokens like parantheses, braces, punctuation etc.
     * @param tokens A list with string tokens.
     * @return A map of the tokens and their frequency.
     */
    protected Map<String,Integer> getTokenMap(List<String> tokens) {
        Map<String,Integer> tokenMap = new HashMap<String,Integer>();
        for (String token : tokens) {

            token = cleanToken(token);

            if (tokenMap.containsKey(token)) {
                tokenMap.put(token, tokenMap.get(token)+1);
            }
            else {
                tokenMap.put(token, 1);
            }
        }
        return tokenMap;
    }

    /**
     * Removes special characters that may be attached to tokens like parantheses, braces, punctuation etc.
     * @param token The token to clean.
     * @return The cleaned token.
     */
    private String cleanToken(String token) {
        // TODO this is implemented a bit inefficiently, I think
        token = token.replace("(", "");
        token = token.replace(")", "");
        token = token.replace("[", "");
        token = token.replace("]", "");
        token = token.replace("!", "");
        token = token.replace("?", "");
        token = token.replace(",", "");
        token = token.replace(".", "");
        token = token.replace(":", "");
        token = token.replace(";", "");
        return token;
    }

    /**
     *
     * @param text The text of the article.
     * @return A substring of text, containing the first paragraph of the article.
     */
    private String findFirstParagraph(String text, Title title) throws WikiApiException  {

        // the referenced entity without disambiguation text in parantheses
        String entity = title.getEntity();

        // skip pictures
        // skip disambiguation template
        // skip empty lines
        // return the first line that is not skipped, it should be the first paragraph
        String[] lines = text.split(System.getProperty("line.separator"));
        for (String line : lines) {
            if (line.contains("[[Bild:") || line.contains("[[Image:")) { // picture
                continue;
            }
            else if (line.contains("Weitere Bedeutungen unter")) {  // disambiguation
                continue;
            }
            else if (line.startsWith("{{Dieser Artikel")) { // disambiguation
                continue;
            }
            else if (line.startsWith("|")) {    // table
                continue;
            }
            else if (line.startsWith("!")) {    // table
                continue;
            }
            else if (line.equals("")) { // empty line
                continue;
            }
            else if (!line.contains(entity)) {   // should contain the page title
                continue;
            }
            else if (line.length() < 30) {  // should be reasonable long
                continue;
            }
            else {
                return line;
            }
        }
        return "";
    }
}
