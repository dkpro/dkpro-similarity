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
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;


/**
 * Implements the relatedness measure by Leacock and Chodorow (1998)
 * lch(c1,c2) = - log(minPathLength(c1,c2) / 2 * depth of the hierarchy)
 * lch(c1,c2) = - log(minPL(c1,c2) / 2 * depth) = log( 2*depth / minPL(c1,c2) )
 * minPathLength is measured in edges (original definition measures in nodes).
 *
 * If minPl is measured in nodes, then minPl(c,c) = 0.
 * This would cause logarithm error (or a division by zero)).
 * Hence, we change to formula to
 * lch(c1,c2) = log( 2*depth / minPL(c1,c2) + 1 )
 *
 * We also changed to path length to edges.
 * Measuring path length in nodes would lead to the fact that identical nodes and neighboring nodes get the same distance.
 *
 *
 * @author zesch
 *
 */
public abstract class LeacockChodorowComparator
    extends PathBasedComparator
{

	private final Log logger = LogFactory.getLog(getClass());

    public LeacockChodorowComparator(Wikipedia pWiki, CategoryGraph pCatGraph, Measure pMeasure, CombinationStrategy pStrategy) {
        super(pWiki, pCatGraph, pMeasure, pStrategy);
    }

    /**
     * Implements the distance measure by Leacock and Chodorow (1998)
     * lch(c1,c2) = - log(minPathLength(c1,c2) / 2 * depth of the hierarchy)
     * lch(c1,c2) = - log(minPL(c1,c2) / 2 * depth) = log ( 2*depth / minPL(c1,c2) )
     *
     * minPathLength is measured in nodes, i.e. the distance of a node to itself is 0!
     * This would cause a logarithm error (or a division by zero)).
     * Thus we changed the behaviour in order to return a distance of 1, if the nodes are equal or neighbors.
     *
     * @param page1 The first page.
     * @param page2 The second page.
     * @param strategy The combination strategy.
     * return A list with the leacock-chodorow relatedness values between all categories of the given pages.
     */
    @Override
	protected List<Double> computeRelatedness(Page page1, Page page2) throws WikiApiException {
        List<Double> relatednessValues = new ArrayList<Double>();

        Set<Category> categories1 = relatednessUtilities.getCategories(page1);
        Set<Category> categories2 = relatednessUtilities.getCategories(page2);

        if (categories1 == null || categories2 == null) {
            return null;
        }

        double depthOfHierarchy = catGraph.getDepth();

        // if the depth of the hierarchy is 0, we cannot compute a relatedness value
        if (depthOfHierarchy == 0) {
            logger.info("The depth of the hierarchy is 0. Cannot compute LeacockChodorow relatedness.");
            return null;
        }

        for (Category cat1 : categories1) {
            for (Category cat2 : categories2) {

                int pathLength = catGraph.getTaxonomicallyBoundPathLengthInEdges(cat1, cat2);

                // a negative or zero path length shows that there is no path
                if (pathLength < 0) {
                    continue;
                }

                // add one to the path length, as a value of zero would cause a division by zero
                double relatedness = Math.log( (2*depthOfHierarchy) / (pathLength + 1) );

//                if (strategy.equals(CombinationStrategy.SelectivityLinear) || strategy.equals(CombinationStrategy.SelectivityLog)) {
//                    relatedness = applySelectivity(relatedness, getSelectivity(catID1, strategy), getSelectivity(catID2, strategy));
//                }

                relatednessValues.add(relatedness);
            }
        }

        return relatednessValues;
    }

    /* (non-Javadoc)
     * @see org.tud.ukp.wikipedia.api.relatedness.PathBasedComparator#getBestRelatedness(java.util.List)
     */
    @Override
	protected double getBestRelatedness(List<Double> relatednessValues) {
           return getMaximum(relatednessValues);
    }
}