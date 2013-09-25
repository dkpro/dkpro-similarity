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
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.CategoryGraph;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiException;

/**
 * Implements the relatedness measure by Wu Palmer et al. (1994).
 * wp(c1,c2) = 2 * depth(lcs(c1,c2)) / ( pl(c1,lcs(c1,c2)) + pl(c2,lcs(c1,c2)) + 2 * depth(lcs(c1,c2)) )
 * pl() is measured in nodes.
 * @author zesch
 *
 */
public abstract class WuPalmerComparator extends PathBasedComparator {

    private final Log logger = LogFactory.getLog(getClass());

    public WuPalmerComparator(Wikipedia pWiki, CategoryGraph pCatGraph, Measure pMeasure, CombinationStrategy pStrategy) {
        super(pWiki, pCatGraph, pMeasure, pStrategy);
    }

    /* (non-Javadoc)
     * @see org.tud.ukp.wikipedia.relatedness.Comparator#computeRelatedness(org.tud.ukp.wikipedia.api.Page, org.tud.ukp.wikipedia.api.Page)
     */
    @Override
	protected List<Double> computeRelatedness(Page page1, Page page2) throws WikiException {
        List<Double> relatednessValues = new ArrayList<Double>();

        Set<Category> categories1 = relatednessUtilities.getCategories(page1);
        Set<Category> categories2 = relatednessUtilities.getCategories(page2);

        if (categories1 == null || categories2 == null) {
            return null;
        }

        Category root = wiki.getMetaData().getMainCategory();
        // test whether the root category is in this graph
        if (!catGraph.getGraph().containsVertex(root.getPageId())) {
            logger.error("The root node is not part of this graph. Cannot compute WuPalmer relatedness.");
            return null;
        }

        for (Category cat1 : categories1) {
            for (Category cat2 : categories2) {

                // get the lowest common subsumer (lcs) of the two categories
                Category lcs = catGraph.getLCS(cat1, cat2);

                if (lcs == null) {
                    continue;
                }

                logger.debug(lcs.getTitle().getPlainTitle());

                double depthLCS = catGraph.getTaxonomicallyBoundPathLengthInEdges(root, lcs);
                double pl1      = catGraph.getTaxonomicallyBoundPathLengthInNodes(cat1, lcs);
                double pl2      = catGraph.getTaxonomicallyBoundPathLengthInNodes(cat2, lcs);

                logger.debug(depthLCS);
                logger.debug(pl1);
                logger.debug(pl2);

                // wp(c1,c2) = 2 * depth(lcs(c1,c2)) / ( pl(c1,lcs(c1,c2)) + pl(c2,lcs(c1,c2)) + 2 * depth(lcs(c1,c2)) )
                double nominator   = 2 * depthLCS;
                double denominator = pl1 + pl2 + 2 * depthLCS;
                double relatedness = nominator / denominator;

                relatednessValues.add(relatedness);
            }
        }

        return relatednessValues;
    }

    /* (non-Javadoc)
     * @see org.tud.ukp.wikipedia.relatedness.PathBasedComparator#getBestRelatedness(java.util.List)
     */
    @Override
	protected double getBestRelatedness(List<Double> relatednessValues) {
           return getMaximum(relatednessValues);
    }
}
