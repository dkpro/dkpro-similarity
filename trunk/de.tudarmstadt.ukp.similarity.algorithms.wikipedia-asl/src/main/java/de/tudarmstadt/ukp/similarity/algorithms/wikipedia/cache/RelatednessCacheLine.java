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

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

import de.tudarmstadt.ukp.similarity.algorithms.wikipedia.measures.Measures;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiRelatednessException;
import de.tudarmstadt.ukp.wikipedia.api.hibernate.WikiHibernateUtil;

/**
 * A negative value of for a measure means that it is not valid. (There should be no measures that return negative values.)
 * @author zesch
 *
 */
public class RelatednessCacheLine implements Measures {

	private final Log logger = LogFactory.getLog(getClass());

    private long id;

    private int page1;
    private int page2;

    private double PathLengthAverage;
    private double PathLengthBest;
    private double PathLengthSelectivity;
    private double LeacockChodorowAverage;
    private double LeacockChodorowBest;
    private double LeacockChodorowSelectivity;
    private double ResnikAverage;
    private double ResnikBest;
    private double ResnikSelectivityLinear;
    private double ResnikSelectivityLog;
    private double LinAverage;
    private double LinBest;
    private double JiangConrathAverage;
    private double JiangConrathBest;
    private double WuPalmerAverage;
    private double WuPalmerBest;
    private double LeskFirst;
    private double LeskFull;
    private double WikiLinkMeasure;


    private Wikipedia wiki;

    /** A no argument constructor as required by Hibernate. */
    public RelatednessCacheLine() {};

    public RelatednessCacheLine(Wikipedia pWiki, int page1, int page2, Measure measure, double relatednessValue ) throws WikiRelatednessException {
        super();

        this.wiki = pWiki;

        // invalidate all measures
        for (Measure singleMeasure : Measure.values()) {
            invalidateMeasure(singleMeasure);
        }

        // set the relatedness value for the given measure
        setRelatednessValue(page1, page2, measure, relatednessValue);

    }

    /**
     * Sets the relatedness value and the validity for the given measure.
     * @param page1 The pageID of the first page.
     * @param page2 The pageID of the second page.
     * @param measure The relatedness measure.
     * @param relatednessValue The relatedness value.
     * @throws WikiRelatednessException
     */
    public void setRelatednessValue(int page1, int page2, Measure measure, double relatednessValue) throws WikiRelatednessException {
        this.page1 = page1;
        this.page2 = page2;

        Class c = this.getClass();
        try {
            // the members of the enum Measure and the members of this class holding their values must have the same name
            Field measureField = c.getDeclaredField(measure.toString());
            measureField.setDouble(this, relatednessValue);
        } catch (NoSuchFieldException e) {
            logger.error("No such field " + measure.toString(), new Throwable());
            throw new WikiRelatednessException(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error("Illegal access.", new Throwable());
            throw new WikiRelatednessException(e.getMessage());
        }
    }

    /**
     * For symmetric measures there is no difference in calling this with page1/page2 or page2/page1.
     * @param measure The relatedness measure.
     * @return The relatedness value between the two pages for the measure, or -1 if the value cannot be found in the cache.
     */
    public double getRelatednessValue(Measure measure) throws WikiRelatednessException {

        Class c = this.getClass();
        try {
            // the members of the enum Measure and the members of this class holding their values must have the same name
            Field measureField = c.getDeclaredField(measure.toString());

            return measureField.getDouble(this);

        } catch (NoSuchFieldException e) {
            logger.error("No such field " + measure.toString(), new Throwable());
            throw new WikiRelatednessException(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error("Illegal access.", new Throwable());
            throw new WikiRelatednessException(e.getMessage());
        }
    }



    /**
     * Invalidates (= sets to -1) the value for a given measure.
     * @param measure The relatedness measure.
     */
    public void invalidateMeasure(Measure measure) throws WikiRelatednessException {
        Session session = WikiHibernateUtil.getSessionFactory(wiki.getDatabaseConfiguration()).getCurrentSession();
        session.beginTransaction();

        double invalidValue = -1.0;

        Class c = this.getClass();
        try {
            // the members of the enum Measure and the members of this class holding their values must have the same name
            Field measureField = c.getDeclaredField(measure.toString());
            measureField.setDouble(this, invalidValue);
        } catch (NoSuchFieldException e) {
            logger.error("No such field " + measure.toString(), new Throwable());
            throw new WikiRelatednessException(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error("Illegal access.", new Throwable());
            throw new WikiRelatednessException(e.getMessage());
        }
        session.flush();
        session.getTransaction().commit();
    }

    protected long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    protected int getPage1() {
        return page1;
    }

    protected void setPage1(int page1) {
        this.page1 = page1;
    }

    protected int getPage2() {
        return page2;
    }

    protected void setPage2(int page2) {
        this.page2 = page2;
    }

    protected double getLeacockChodorowAverage() {
        return LeacockChodorowAverage;
    }

    protected void setLeacockChodorowAverage(double leacockChodorowAverage) {
        LeacockChodorowAverage = leacockChodorowAverage;
    }

    protected double getLeacockChodorowBest() {
        return LeacockChodorowBest;
    }

    protected void setLeacockChodorowBest(double leacockChodorowBest) {
        LeacockChodorowBest = leacockChodorowBest;
    }

    protected double getLeacockChodorowSelectivity() {
        return LeacockChodorowSelectivity;
    }

    protected void setLeacockChodorowSelectivity(double leacockChodorowSelectivity) {
        LeacockChodorowSelectivity = leacockChodorowSelectivity;
    }

    protected double getPathLengthAverage() {
        return PathLengthAverage;
    }

    protected void setPathLengthAverage(double pathLengthAverage) {
        PathLengthAverage = pathLengthAverage;
    }

    protected double getPathLengthBest() {
        return PathLengthBest;
    }

    protected void setPathLengthBest(double pathLengthBest) {
        PathLengthBest = pathLengthBest;
    }

    protected double getPathLengthSelectivity() {
        return PathLengthSelectivity;
    }

    protected void setPathLengthSelectivity(double pathLengthSelectivity) {
        PathLengthSelectivity = pathLengthSelectivity;
    }

    protected double getResnikAverage() {
        return ResnikAverage;
    }

    protected void setResnikAverage(double resnikAverage) {
        ResnikAverage = resnikAverage;
    }

    protected double getResnikBest() {
        return ResnikBest;
    }

    protected void setResnikBest(double resnikBest) {
        ResnikBest = resnikBest;
    }

    protected double getResnikSelectivityLinear() {
        return ResnikSelectivityLinear;
    }

    protected void setResnikSelectivityLinear(double resnikSelectivityLinear) {
        ResnikSelectivityLinear = resnikSelectivityLinear;
    }

    protected double getResnikSelectivityLog() {
        return ResnikSelectivityLog;
    }

    protected void setResnikSelectivityLog(double resnikSelectivityLog) {
        ResnikSelectivityLog = resnikSelectivityLog;
    }

    protected double getLeskFirst() {
        return LeskFirst;
    }

    protected void setLeskFirst(double leskFirst) {
        LeskFirst = leskFirst;
    }

    protected double getLeskFull() {
        return LeskFull;
    }

    protected void setLeskFull(double leskFull) {
        LeskFull = leskFull;
    }

    protected double getLinAverage() {
        return LinAverage;
    }

    protected void setLinAverage(double linAverage) {
        LinAverage = linAverage;
    }

    protected double getLinBest() {
        return LinBest;
    }

    protected void setLinBest(double linBest) {
        LinBest = linBest;
    }

    protected double getJiangConrathAverage() {
        return JiangConrathAverage;
    }

    protected void setJiangConrathAverage(double jiangConrathAverage) {
        JiangConrathAverage = jiangConrathAverage;
    }

    protected double getJiangConrathBest() {
        return JiangConrathBest;
    }

    protected void setJiangConrathBest(double jiangConrathBest) {
        JiangConrathBest = jiangConrathBest;
    }

    protected double getWuPalmerAverage() {
        return WuPalmerAverage;
    }

    protected void setWuPalmerAverage(double wuPalmerAverage) {
        WuPalmerAverage = wuPalmerAverage;
    }

    protected double getWuPalmerBest() {
        return WuPalmerBest;
    }

    protected void setWuPalmerBest(double wuPalmerBest) {
        WuPalmerBest = wuPalmerBest;
    }

    protected double getWikiLinkMeasure() {
        return WikiLinkMeasure;
    }

    protected void setWikiLinkMeasure(double wikiLinkMeasure) {
        WikiLinkMeasure = wikiLinkMeasure;
    }

}
