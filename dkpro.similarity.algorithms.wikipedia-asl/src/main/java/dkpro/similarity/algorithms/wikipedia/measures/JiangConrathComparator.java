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
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiRelatednessException;

/**
 * Implements the distance measure by JiangConrath (1997)
 * jc(c1,c2) = IC(c1) + IC(c2) - 2* IC( lcs(c1,c2) )
 * Note that jc returns a distance value rather than a similarity value.
 *
 * Attention: we convert it into a relatedness measure by using the formula
 * (2 - ( IC(c1) + IC(c2) - 2* IC(lcs(c1,c2)) )) / 2
 *
 * We then scale it to [0,1]
 *
 * We use intrinisc IC instead of corpus-based IC and transform it into a similarity measure:
 * jc(c1,c2) = 2 - ( IC(c1) + IC(c2) - 2* IC(lcs(c1,c2)) )

 *
 * @author zesch
 *
 */
public class JiangConrathComparator
    extends InformationContentBasedComparator
{

	private final Log logger = LogFactory.getLog(getClass());

    public JiangConrathComparator(Wikipedia pWiki, CategoryGraph pCatGraph, Measure pMeasure, CombinationStrategy pStrategy) throws WikiRelatednessException, WikiApiException {
        super(pWiki, pCatGraph, pMeasure, pStrategy);
    }

    /**
     * Implements the distance measure by JiangConrath (1997)
     * jc(c1,c2) = IC(c1) + IC(c2) - 2* IC( lcs(c1,c2) )
     * Note that jc returns a distance value rather than a similarity value.
     *
     * @param page1 The first page.
     * @param page2 The second page.
     * return A list with the Lin relatedness values between all categories of the given pages.
     */
    @Override
	protected List<Double> computeRelatedness(Page page1, Page page2) throws WikiApiException {
        List<Double> relatednessValues = new ArrayList<Double>();

        Set<Category> categories1 = relatednessUtilities.getCategories(page1);
        Set<Category> categories2 = relatednessUtilities.getCategories(page2);

        if (categories1 == null || categories2 == null) {
            return null;
        }

        Category root = wiki.getMetaData().getMainCategory();
        // test whether the root category is in this graph
        if (!catGraph.getGraph().containsVertex(root.getPageId())) {
            logger.error("The root node is not part of this graph. Cannot compute JiangConrath relatedness.");
            return null;
        }

        for (Category cat1 : categories1) {
            for (Category cat2 : categories2) {
                // get the lowest common subsumer (lcs) of the two categories
                Category lcs = catGraph.getLCS(cat1, cat2);

                if (lcs == null) {
                    continue;
                }

                double iicCat1 = catGraph.getIntrinsicInformationContent(cat1);
                double iicCat2 = catGraph.getIntrinsicInformationContent(cat2);
                double iicLcs = catGraph.getIntrinsicInformationContent(lcs);

                // (2 - ( IC(c1) + IC(c2) - 2* IC(lcs(c1,c2)) )) / 2
                double relatedness =  (2 - (iicCat1 + iicCat2 - 2*iicLcs)) / 2;
                relatednessValues.add(relatedness);
            }
        }

        return relatednessValues;
    }

    /* (non-Javadoc)
     * @see org.tud.ukp.wikipedia.api.relatedness.InformationContentBasedComparator#getBestRelatedness(java.util.List)
     */
    @Override
	protected double getBestRelatedness(List<Double> relatednessValues) {
           return getMaximum(relatednessValues);
    }
}
