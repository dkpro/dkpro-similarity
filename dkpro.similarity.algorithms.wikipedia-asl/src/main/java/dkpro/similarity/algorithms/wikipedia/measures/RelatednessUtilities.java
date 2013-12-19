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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiRelatednessException;

public class RelatednessUtilities implements Measures {

	private final Log logger = LogFactory.getLog(getClass());

    public static boolean isSymmetric(Measure measure) throws WikiRelatednessException {

        if (isSymmetricMap.containsKey(measure)) {
            return isSymmetricMap.get(measure);
        }
        else {
            throw new WikiRelatednessException("unknown measure " + measure);
        }
    }

    /**
     * @param page The page.
     * @return The categories of the given page.
     * @throws WikiApiException
     */
    public Set<Category> getCategories(Page page) throws WikiApiException {
        Set<Category> categories = new HashSet<Category>(page.getCategories());
//        logger.debug(CommonUtilities.getSetContents(categories));
        if (categories.size() == 0) {
            logger.info (page.getTitle() + " not categorized.");
        }
        return categories;
    }

}
