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

import de.tudarmstadt.ukp.wikipedia.api.CategoryGraph;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

public abstract class InformationContentBasedComparator
    extends WikipediaSimilarityMeasureBase
{

    protected CategoryGraph catGraph;
    protected RelatednessUtilities relatednessUtilities;

    public InformationContentBasedComparator(Wikipedia pWiki, CategoryGraph pCatGraph, Measure pMeasure, CombinationStrategy pStrategy) throws WikiApiException {
        super(pWiki, pMeasure, pStrategy);
        this.catGraph = pCatGraph;
        this.relatednessUtilities = new RelatednessUtilities();
    }

//    /**
//     * Resolves disambiguation pages into candidate pages and returns the best relatedness value out of all candidate pairs.
//     * @param token1 The first token.
//     * @param token2 The second token.
//     * @param strategy The combination strategy.
//     * @return The relatedness value between the two pages that belong to the given tokens.
//     * @throws WikiException
//     */
//    protected double getRelatednessValue(String token1, String token2, CombinationStrategy strategy) throws WikiException {
//        // Get the pages that belong to the tokens.
//        // Encode the search string as a wikipedia title before searching.
//        Page page1 = wiki.getPage(token1);
//        Page page2 = wiki.getPage(token2);
//        if (page1 == null || page2 == null) {
//            return NOT_IN_WIKIPEDIA;
//        }
//
//        // resolve disambiguation pages
//        Set<Integer> pageSet1 = getDisambiguationPageSet(page1);
//        Set<Integer> pageSet2 = getDisambiguationPageSet(page2);
//
//        List<Double> relatednessValues = this.getRelatednessValues(pageSet1, pageSet2, strategy);
//
//        return getBestRelatedness(relatednessValues);
//    }

//    /**
//     * Select the best value from the computed relatedness scores.
//     * Different measures may use different selection methods.
//     * Measures must override this abstract method.
//     * @return The best relatedness value of the given list.
//     */
//    protected abstract double getBestRelatedness(List<Double> relatednessValues);

//    /**
//     * Computes the relatedness values between a set of candidate pages (Candidate pages have been returned by the disambiguation process.)
//     * The measures must overwrite this abstract method with
//     * @param pageSet1 The set of candidate pages of the first page.
//     * @param pageSet2 The set of candidate pages of the second page.
//     * @param strategy The combination strategy.
//     * @return Returns the relatedness values between the given candidate pages.
//     * @throws WikiException
//     */
//    protected abstract List<Double> getRelatednessValues(Set<Integer> pageSet1, Set<Integer> pageSet2, CombinationStrategy strategy) throws WikiException;
}
