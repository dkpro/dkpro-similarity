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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.util.CommonUtilities;

/**
 * Implements the distance measure by Lesk (1986)
 * lesk(c1,c2) = overlap(t1,t2)/min(length(t1),length(t2))
 *
 * @author zesch
 *
 */
public abstract class LeskComparator
    extends DefinitionBasedComparator
{

	private final Log logger = LogFactory.getLog(getClass());

    public LeskComparator(Wikipedia pWiki, Measure pMeasure, CombinationStrategy pStrategy) {
        super(pWiki, pMeasure, pStrategy);
    }

    /**
     * Implements the distance measure by Lesk (1986)
     * lesk(c1,c2) = overlap(t1,t2)/max(length(t1),length(t2))
     *
     * The overlap is normalized by the maximum length of two texts to fall into [0,1].
     * There can only be max(length(t1),length(t2) overlaps.
     * @param page1 The first page.
     * @param page2 The second page.
     * return The Lesk relatedness value as computed between the two pages.
     */
    @Override
	protected List<Double> computeRelatedness(Page page1, Page page2) throws WikiApiException {
        List<String> tokens1 = tokenizeText( getTextToTokenize(page1) );
        List<String> tokens2 = tokenizeText( getTextToTokenize(page2) );

        Map<String, Integer> tokenMap1 = getTokenMap(tokens1);
        Map<String, Integer> tokenMap2 = getTokenMap(tokens2);

        logger.debug("Token map sizes: " + tokenMap1.size() + " " + tokenMap2.size());

        double relatedness;
        // if one of the articles is empty, there cannot be an overlap
        if (tokenMap1.size() == 0 || tokenMap2.size() == 0) {
            relatedness = 0.0;
        }
        else {
            logger.debug(CommonUtilities.getMapContents(tokenMap1));
            logger.debug(CommonUtilities.getMapContents(tokenMap2));

            double overlap = getOverlap(tokenMap1, tokenMap2);

            List<Double> lengthList = new ArrayList<Double>();
            lengthList.add( (double) tokenMap1.size() );
            lengthList.add( (double) tokenMap2.size() );
            relatedness = overlap / getMinimum(lengthList);
        }

        // abstract method computeRelatedness must return a list even if we only have a single value
        List<Double> returnList = new ArrayList<Double>();
        returnList.add(relatedness);
        return returnList;
    }

    /**
     * An overlap between two texts is each occurrence of a word that is in both texts.
     * Each word is only counted once, even if there are multiple occurrences.
     *
     * @param tokenMap1 A map containing the tokens of the first text and their frequency.
     * @param tokenMap2 A map containing the tokens of the second text and their frequency.
     * @return The number of overlaps between the tokens.
     */
    private double getOverlap(Map<String,Integer> tokenMap1, Map<String,Integer> tokenMap2) {
        int numberOfOverlaps = 0;

        for (String token1 : tokenMap1.keySet()) {
            if (tokenMap2.containsKey(token1)) {
                numberOfOverlaps++;
            }
        }

        return numberOfOverlaps;
    }

    /* (non-Javadoc)
     * @see org.tud.ukp.wikipedia.api.wiki.relatedness.DefinitionBasedComparator#getBestRelatedness(java.util.List)
     */
    @Override
	protected double getBestRelatedness(List<Double> relatednessValues) {
           // higher lesk values mean higher relatedness
           return getMaximum(relatednessValues);
    }


    /**
     * @param page The page object.
     * @return The text that should be tokenized. Depends on which method is calles. The first paragraph for LeskFirst and the full text for LeskFull.
     */
    protected abstract String getTextToTokenize(Page page) throws WikiApiException;
}