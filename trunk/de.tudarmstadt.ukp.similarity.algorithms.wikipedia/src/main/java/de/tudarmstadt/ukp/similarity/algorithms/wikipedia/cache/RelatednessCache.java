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
package de.tudarmstadt.ukp.similarity.algorithms.wikipedia.cache;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import de.tudarmstadt.ukp.similarity.algorithms.wikipedia.measures.Measures;
import de.tudarmstadt.ukp.similarity.algorithms.wikipedia.measures.RelatednessUtilities;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiPageNotFoundException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiRelatednessException;
import de.tudarmstadt.ukp.wikipedia.api.hibernate.WikiHibernateUtil;

/**
 * Computing relatedness measures is quite costly.
 * There are almost infinite combinations of pages. Pre-computing them would run until the end of days.
 * So we have to compute them at run-time.
 * But, if a measure has been computed it should be stored for later retrieval (DB access is much faster than recomputing).
 * This allows to pre-compute needed relatedness values for live-demos etc.
 *
 * @author zesch
 *
 */
public class RelatednessCache implements Measures {

    private final Log logger = LogFactory.getLog(getClass());
    private Wikipedia wiki;

    public RelatednessCache(Wikipedia pWiki) {
        this.wiki = pWiki;
    }

    /**
     * There are some relatedness measures that are asymetric, we need two cache lines (page1,page2 and page2,page1) for each page pair.
     * Symmetric measure must add relatedness values in both lines!
     * @param page1 The pageID of the first page.
     * @param page2 The pageID of the second page.
     * @param method The relatedness method for which the pre-computed value should be returned.
     * @return The relatedness value or -1 if the value was not pre-computed so far.
     */
    public double getCachedRelatedness(int pageID1, int pageID2, Measure measure) throws WikiRelatednessException {
        // lookup if there is an entry for the two categories and these methods
        RelatednessCacheLine cacheLine = getCacheLine(pageID1, pageID2);

        // no entry found => return -1
        if (cacheLine == null) {
            logger.info("No cacheline found.");
            return -1.0;
        }

        // retrieve the relatedness
        return cacheLine.getRelatednessValue(measure);
    }

    /**
     * Sets the relatedness values for a given page combination and a given measure.
     * In case of a symmetric measure, both page combinations (page1,page2 and page2,page1) have to be set, with the exception of page1=page2.
     * @param page1 The pageID of the first page.
     * @param page2 The pageID of the second page.
     * @param measure The relatedness measure.
     * @param relatednessValue The relatedness value.
     * @throws WikiRelatednessException
     */
    public void setCachedRelatedness(int pageID1, int pageID2, Measure measure, double relatednessValue) throws WikiRelatednessException {
        // Get the cache line
        RelatednessCacheLine cacheLine1 = getCacheLine(pageID1, pageID2);
        if (cacheLine1 != null) {
            logger.debug("Get existing cacheline.");
            Session session = WikiHibernateUtil.getSessionFactory(wiki.getDatabaseConfiguration()).getCurrentSession();
            session.beginTransaction();
            session.update(cacheLine1);
            cacheLine1.setRelatednessValue(pageID1, pageID2, measure, relatednessValue);
            session.flush();
            session.getTransaction().commit();
            if (pageID1 != pageID2 && RelatednessUtilities.isSymmetric(measure)) {
                RelatednessCacheLine cacheLine2 = getCacheLine(pageID2, pageID1);
                session = WikiHibernateUtil.getSessionFactory(wiki.getDatabaseConfiguration()).getCurrentSession();
                session.beginTransaction();
                session.update(cacheLine2);
                cacheLine2.setRelatednessValue(pageID2, pageID1, measure, relatednessValue);
                session.flush();
                session.getTransaction().commit();
            }

        }
        // Create the cache line.
        // For symmetric measures both category combinations must be created.
        // Persist the objects using hibernate.
        else {
            logger.debug("Creating new cacheline.");
            RelatednessCacheLine newRCL = new RelatednessCacheLine(wiki, pageID1, pageID2, measure, relatednessValue);
            // save the object to the database
            Session session = WikiHibernateUtil.getSessionFactory(wiki.getDatabaseConfiguration()).getCurrentSession();
            session.beginTransaction();
            session.save(newRCL);
            session.getTransaction().commit();

            // for symmetric measures also persist the other category combination
            if (pageID1 != pageID2 && RelatednessUtilities.isSymmetric(measure)) {
                RelatednessCacheLine newRCL2 = new RelatednessCacheLine(wiki, pageID2, pageID1, measure, relatednessValue);
                // save the object to the database
                session = WikiHibernateUtil.getSessionFactory(wiki.getDatabaseConfiguration()).getCurrentSession();
                session.beginTransaction();
                session.save(newRCL2);
                session.getTransaction().commit();
            }
        }
    }

    /**
     * Invalidates a certain cache line represented by a token1/token2 combination.
     * The token2/token1 combo is also invalidated.
     * This may be too strict for some asymetric measures, but will only cause a negligible performance loss.
     * Even for asymetric measures it is unlikely that only one combination has to be invalidated.
     * @param token1 The string representing the first page.
     * @param token2 The string representing the second page.
     * @throws WikiApiException
     */
    public void invalidateCacheline(Wikipedia wiki, String token1, String token2) throws WikiApiException {
        try {
            int pageID1 = wiki.getPage(token1).getPageId();
            int pageID2 = wiki.getPage(token2).getPageId();
            invalidateCacheline(pageID1, pageID2);
        } catch (WikiPageNotFoundException e) {
            logger.error("Page not found for tokens " + token1 + " or " + token2);
            e.printStackTrace();
        }
    }

    /**
     * Invalidates a certain cache line represented by the pageID1/pageID2 combination.
     * The pageID2/pageID1 combo is also invalidated.
     * This may be too strict for some asymetric measures, but will only cause a negligible performance loss.
     * Even for asymetric measures it is unlikely that only one combination has to be invalidated.
     * Instead of setting all measure to 0, we simply delete the cacheline.
     * @param page1 The pageID of the first page.
     * @param page2 The pageID of the second page.
     */
    public void invalidateCacheline(int pageID1, int pageID2) {
        RelatednessCacheLine cacheLine = getCacheLine(pageID1, pageID2);
        if (cacheLine != null) {
            deleteCacheline(pageID1, pageID2);
            deleteCacheline(pageID2, pageID1);
        }
    }

    /**
     * Remove a certain cacheline from the database
     * @param page1 The pageID of the first page.
     * @param page2 The pageID of the second page.
     */
    private void deleteCacheline(int pageID1, int pageID2) {
        Session session = WikiHibernateUtil.getSessionFactory(wiki.getDatabaseConfiguration()).getCurrentSession();
        session.beginTransaction();

        String hqlDelete = "delete from RelatednessCacheLine as rcl where rcl.page1 = ? and rcl.page2 = ?";
        session.createQuery( hqlDelete )
                .setInteger(0, pageID1)
                .setInteger(1, pageID2)
                .executeUpdate();

        // delete also the other combo
        hqlDelete = "delete from RelatednessCacheLine as rcl where rcl.page1 = ? and rcl.page2 = ?";
        session.createQuery( hqlDelete )
                .setInteger(0, pageID2)
                .setInteger(1, pageID1)
                .executeUpdate();

        session.getTransaction().commit();
    }

    /**
     * If the implementation details for a certain measure are changed, the cached value may not be valid any more.
     * This method invalidates the cached values for this measure, but leaves all other values untouched.
     *
     * This is a cache function - it should be fast. So I bypass the hibernate mechanics a bit.
     * I do not want to load all cachelines, change the validity values and flush the session.
     * I will use a nativ SQL statement instead (much, much faster; but a bit dirty)
     * @param measure The measure to invalidate.
     */
    public void invalidateMeasure(Measure measure) throws WikiRelatednessException {
        Session session = WikiHibernateUtil.getSessionFactory(wiki.getDatabaseConfiguration()).getCurrentSession();
        session.beginTransaction();

        double invalidValue = -1.0;

        String hqlUpdate;
        hqlUpdate = "update RelatednessCacheLine rcl set rcl." + measure.toString() + " = " + invalidValue;
        int updatedEntities = session.createQuery( hqlUpdate ).executeUpdate();
        logger.info (updatedEntities + " cache lines invalidated.");

        session.getTransaction().commit();
    }

    /**
     * @param page1 The pageID of the first page.
     * @param page2 The pageID of the second page.
     * @return The cache line for the page1/page2 combination or null if the cache line does not exists. In case of a symmetric measure it is guaranteed that page2/page1 line holds the same value.
     */
    private RelatednessCacheLine getCacheLine(int pageID1, int pageID2) {
        Session session = WikiHibernateUtil.getSessionFactory(wiki.getDatabaseConfiguration()).getCurrentSession();
        session.beginTransaction();
        RelatednessCacheLine cacheLine = (RelatednessCacheLine) session.createQuery(
                "from RelatednessCacheLine as rcl where rcl.page1 = ? and rcl.page2 = ?")
                .setInteger(0, pageID1)
                .setInteger(1, pageID2)
                .uniqueResult();
        session.getTransaction().commit();
        return cacheLine;
    }
}