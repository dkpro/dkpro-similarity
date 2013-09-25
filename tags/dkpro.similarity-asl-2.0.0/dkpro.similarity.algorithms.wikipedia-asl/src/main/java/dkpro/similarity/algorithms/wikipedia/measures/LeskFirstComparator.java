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
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * Implements the distance measure by Lesk (1986)
 * lesk(c1,c2) = overlap(t1,t2)/min(length(t1),length(t2))
 *
 * Uses only the first paragraph of an article.
 * @author zesch
 *
 */
public class LeskFirstComparator
    extends LeskComparator
{

    public LeskFirstComparator(Wikipedia pWiki) {
        super(pWiki, Measure.LeskFirst, CombinationStrategy.None);
    }

    /**
     * This constructor is only necessary because we want to be able to instantiate all relatedness measures with an equally parametrized constructor (needed for reflection).
     * @param pWiki
     * @param pCatGraph
     */
    public LeskFirstComparator(Wikipedia pWiki, CategoryGraph pCatGraph) {
        this(pWiki);
    }

    /* (non-Javadoc)
     * @see org.tud.ukp.wikipedia.relatedness.LeskComparator#getTextToTokenize(org.tud.ukp.wikipedia.api.Page)
     */
    @Override
	protected String getTextToTokenize(Page page) throws WikiApiException {
        return getFirstParagraph(page);
    }
}