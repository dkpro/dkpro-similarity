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

import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.CategoryGraph;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiException;

/**
 * Implements the distance measure by Rada et al. (1989).
 * The distance between two terms a and b is defined as the minimum number of edges separating
 * the nodes that represent a and b in the semantic net.
 * @author zesch
 *
 */
public abstract class PathLengthComparator
    extends PathBasedComparator
{

    public PathLengthComparator(Wikipedia pWiki, CategoryGraph pCatGraph, Measure pMeasure, CombinationStrategy pStrategy) {
        super(pWiki, pCatGraph, pMeasure, pStrategy);
    }

    @Override
	protected List<Double> computeRelatedness(Page page1, Page page2) throws WikiException {
        List<Double> relatednessValues = new ArrayList<Double>();

        Set<Category> categories1 = relatednessUtilities.getCategories(page1);
        Set<Category> categories2 = relatednessUtilities.getCategories(page2);

        if (categories1 == null || categories2 == null) {
            return null;
        }

        for (Category cat1 : categories1) {
            for (Category cat2 : categories2) {
                double relatedness = catGraph.getTaxonomicallyBoundPathLengthInEdges(cat1, cat2);

//                if (strategy.equals(CombinationStrategy.SelectivityLinear) || strategy.equals(CombinationStrategy.SelectivityLog)) {
//                    relatedness = applySelectivity(relatedness, getSelectivity(catID1, strategy), getSelectivity(catID2, strategy));
//                }

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
           return getMinimum(relatednessValues);
    }
}
