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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiPageNotFoundException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiRelatednessException;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasureBase;

public abstract class WikipediaSimilarityMeasureBase
    extends TermSimilarityMeasureBase
    implements Measures
{

	private final Log logger = LogFactory.getLog(getClass());

    protected Wikipedia wiki;
    protected Measure measure;
    protected CombinationStrategy strategy;
    protected boolean useCache; // indicates whether to use built-in cache or not

    public WikipediaSimilarityMeasureBase(Wikipedia pWiki, Measure pMeasure, CombinationStrategy pStrategy) {
        this.wiki             = pWiki;
        this.measure  = pMeasure;
        this.strategy = pStrategy;
        this.useCache = true;
    }

    @Override
    public double getSimilarity(String token1, String token2) throws SimilarityException {
        try {
            return getRelatednessValue(token1, token2);
        }
        catch (WikiException e) {
            throw new SimilarityException();
        }
    }

    /**
     * Resolves disambiguation pages into candidate pages and returns the best relatedness value out of all candidate pairs.
     * @param token1 The first token.
     * @param token2 The second token.
     * @return The relatedness value between the two pages that belong to the given tokens.
     * @throws WikiException
     */
    protected double getRelatednessValue(String token1, String token2) throws WikiException {
        // Get the pages that belong to the tokens.
        // Encode the search string as a wikipedia title before searching.
        Page page1;
        Page page2;
        try {
            page1 = wiki.getPage(token1);
            page2 = wiki.getPage(token2);
        } catch (WikiPageNotFoundException e) {
            return NOT_IN_WIKIPEDIA;
        }

        // resolve disambiguation pages
        Set<Page> pageSet1 = getDisambiguationPageSet(page1);
        Set<Page> pageSet2 = getDisambiguationPageSet(page2);

        List<Double> relatednessValues = this.getRelatednessValues(pageSet1, pageSet2);

        return getBestRelatedness(relatednessValues);
    }

    /**
     * Computes the relatedness values between a set of candidate pages (Candidate pages have been returned by the disambiguation process.)
     * @param pageSet1 The set of candidate pages of the first page.
     * @param pageSet2 The set of candidate pages of the second page.
     * @return Returns the relatedness values between the given candidate pages.
     * @throws WikiException
     */
    protected List<Double> getRelatednessValues(Set<Page> pageSet1, Set<Page> pageSet2) throws WikiException {
        List<Double> relatednessValues = new ArrayList<Double>();
        // iterate over all candidate page pairs
        for (Page page1 : pageSet1) {
            for (Page page2 : pageSet2) {
                double relatedness = -1.0;

                // if a relatedness value < 0 is returned, no cached value has been found => compute it
                if (relatedness < 0) {
                    // compute the relatedness values between the two pages
                    List<Double> computedValues = computeRelatedness(page1, page2);
                    logger.debug("Computed relatedness values: "+computedValues);

                    // Combines the relatedness values that have been computed between the categories of an article using a given combination strategy.
                    relatedness = combineRelatedness(computedValues);
                }

                logger.debug(measure + " - " + page1.getTitle() + " / " + page2.getTitle() + ": " + relatedness);

                relatednessValues.add(relatedness);
            }
        }
        return relatednessValues;
    }

    /**
     * Computes the relatedness between two pages.
     * This abstract method must be overwritten by the measures with their special way of computing the relatedness.
     * @param page1 The first page.
     * @param page2 The second page.
     * @return
     */
    protected abstract List<Double> computeRelatedness(Page page1, Page page2) throws WikiException;

    /**
     * Select the best value from the computed relatedness scores.
     * Different measures may use different selection methods.
     * Measures must override this abstract method.
     * @return The best relatedness value of the given list.
     */
    protected abstract double getBestRelatedness(List<Double> relatednessValues);

//    /**
//     * Loads the category and gets the selectivity of this category.
//     * @param catID The pageID of the category.
//     * @param strategy The combination strategy. Depending on the strategy, the method returns linear selectivity or logarithmic selectivity.
//     * @return The selectivity of the category.
//     * @throws WikiApiException
//     */
//    protected double getSelectivity(int catID, CombinationStrategy strategy) throws WikiApiException {
//        Category cat;
//        try {
//            cat = new Category(wiki, catID);
//        } catch (WikiPageNotFoundException e) {
//            throw new WikiApiException("Category not found");
//        }
//
//        double selectivity = 0.0;
//        if (strategy.equals(CombinationStrategy.SelectivityLinear)) {
//            selectivity = cat.getSelectivityLinear(wiki);
//        }
//        else if (strategy.equals(CombinationStrategy.SelectivityLog)) {
//            selectivity = cat.getSelectivityLog(wiki);
//        }
//        else {
//            throw new WikiApiException("Unknown combination strategy " + strategy);
//        }
//
//        return selectivity;
//    }


    /**
     * Applies the selectivity values on the original relatedness value and returns the weighted relatedness value.
     * @param relatedness The original relatedness value.
     * @param selectivityCat1 The selectivity of the first category.
     * @param selectivityCat2 The selectivity of the second category.
     * @return Returns the relatedness value weighted with the selectivity values.
     */
    protected double applySelectivity(double relatedness, double selectivityCat1, double selectivityCat2) {
        return (relatedness *  selectivityCat1 * selectivityCat2);

    }

    /**
     * @param page The page object.
     * @return If the page is a disambiguation page, then the method returns the set of pages belonging to the senses of the word.
     * If the page is not a disambiguation page, then the method returns a set with only the original page object in it.
     * @throws WikiApiException
     */
    protected Set<Page> getDisambiguationPageSet(Page page) throws WikiApiException {
        Set<Page> pageSet = new HashSet<Page>();

        boolean isDisambiguation = page.isDisambiguation();
        String pageTitle = page.getTitle().getEntity();
        // pageLinks is a map with link texts and frequency
        Map<String,Integer> pageLinks = getPageLinks(page);

        if (isDisambiguation) {
            // find all links that start with the pageTitle and add them to the pageSet
            Page firstLinkPage = null;
            boolean first = true;
            for (String link : pageLinks.keySet()) {
                if (link.startsWith(pageTitle)) {
                    Page linkPage;
                    try {
                        linkPage = wiki.getPage(link);
                    } catch (WikiPageNotFoundException e) {
                        continue;
                    }

                    pageSet.add(linkPage);
                }
                // get the id of the first page
                else {
                    if (first) {
                        Page linkPage;
                        try {
                            linkPage = wiki.getPage(link);
                        } catch (WikiPageNotFoundException e) {
                            continue;
                        }
                        firstLinkPage = linkPage;
                        first = false;
                    }
                }
            }
            // if there is no link that starts with the page title (empty pageSet), simply add the first link
            // if firstLinkPageID is -1, no page for the first link has been found
            if (pageSet.size() == 0 && firstLinkPage != null) {
                pageSet.add(firstLinkPage);
            }
        }
        else {
            pageSet.add(page);
        }

        return pageSet;
    }

    /**
     * @param page The page to check for links.
     * @return A map containing the names of all pages that are linked in the given page.
     */
    private Map<String,Integer> getPageLinks(Page page) {
        Map<String,Integer> pageLinks = new HashMap<String,Integer>();
        String pageText = page.getText();

        String linkRegex = "\\[\\[\\s*(.+?)\\s*]]";
        Pattern pattern = Pattern.compile(linkRegex);
        Matcher matcher = pattern.matcher(pageText);

        while (matcher.find ()) {
            String link = matcher.group(1);
            if (pageLinks.containsKey(link)) {
                pageLinks.put( link, pageLinks.get(link)+1 );
            }
            else {
                pageLinks.put(link,1);
            }
        }
        return pageLinks;
    }

    /**
     * Combines the relatedness values that have been computed between the categories of an article using a given combination strategy.
     * @param relatednessValues A list with the relatedness values.
     * @return The combined relatedness value.
     */
    public double combineRelatedness(List<Double> relatednessValues) throws WikiRelatednessException {
        double combinedRelatedness = -1.0;
        if (strategy.equals(CombinationStrategy.Average) ||
            strategy.equals(CombinationStrategy.SelectivityLinear) ||
            strategy.equals(CombinationStrategy.SelectivityLog)) {
                combinedRelatedness = computeAverageRelatedness(relatednessValues);
        }
        // If CombinationStrategy is "none" then there is only a single result value and we do not have to combine anything.
        // But inheritence structure makes it necessary to have a combination of relatedness values computed.
        // So we use findSmallestRelatedness for "none" as this will select the smallest (that is the only value) in the list :)
        else if (strategy.equals(CombinationStrategy.Best) || strategy.equals(CombinationStrategy.None)) {
            combinedRelatedness = getBestRelatedness(relatednessValues);
        }
        else {
            throw new WikiRelatednessException("Unknown combination strategy " + strategy);
        }
        return combinedRelatedness;
    }

//    /**
//     * Finds the smallest relatedness that is >= 0.
//     * Relatedness values < 0 are invalid by definition.
//     * @param relatednessValues A list of relatedness values.
//     * @return The smallest relatedness.
//     */
//    private double findSmallestRelatedness(List<Double> relatednessValues) {
//
//        if (relatednessValues == null) {
//            return -1.0;
//        }
//
//        double smallest = Double.POSITIVE_INFINITY;
//        int counter = 0;
//        for (double value : relatednessValues) {
//            if (value >= 0 && value < smallest) {
//                smallest = value;
//                counter++;
//            }
//        }
//
//        // if all values are < 0, then the counter is 0, then return -1.0
//        if (counter == 0) {
//            return -1.0;
//        }
//        return smallest;
//    }

    /**
     * Computes the average relatedness value out of all relatdness values >= 0.
     * Path lengths < 0 are invalid by definition.
     * @param relatednessValues A list of relatedness values.
     * @return The average of the given relatedness values.
     */
    private double computeAverageRelatedness(List<Double> relatednessValues) {

        // no values, no average
        if (relatednessValues == null) {
            return -1.0;
        }

        double avg = 0.0;
        int counter = 0;
        for (double value : relatednessValues) {
            if (value >= 0) {
                counter++;
                avg += value;
            }
        }

        // all values < 0, no average
        // also prevents from division by zero in the next step
        if (counter == 0) {
            return -1.0;
        }

        return (avg / counter);
    }

    /**
     * Relatedness values < 0 are invalid by definition.
     * @param valueList A list containing the values.
     * @return Returns the minimum of the values in the given list, or NO_SENSE if the value list is empty.
     */
    public double getMinimum(List<Double> valueList) {

        if (valueList == null) {
            return NO_SENSE;
        }

        boolean valid = false;
        double minimum = Double.POSITIVE_INFINITY;
        for (double value : valueList) {
            if (value >= 0 && value < minimum) {
                minimum = value;
                valid = true;
            }
        }

        if (!valid) {
            return NO_SENSE;
        }

        return minimum;
    }

    /**
     * @param valueList A list containing the values.
     * @return Returns the maximum of the values in the given list, or NO_SENSE if the value list is empty.
     */
    public double getMaximum(List<Double> valueList) {

        if (valueList == null) {
            return NO_SENSE;
        }

        boolean valid = false;
        double maximum = Double.NEGATIVE_INFINITY;
        for (double value : valueList) {
            if (value > maximum) {
                maximum = value;
                valid = true;
            }
        }

        if (!valid) {
            return NO_SENSE;
        }

        return maximum;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

}
