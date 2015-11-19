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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

public class WikiLinkComparator extends WikipediaSimilarityMeasureBase {

    private WikiLinkCache cache;
    private final long numberOfArticles;
    private boolean useOutboundLinks;

    public WikiLinkComparator(Wikipedia pWiki) {
        this(pWiki, true, false);
    }

    public WikiLinkComparator(Wikipedia pWiki, boolean useCache) {
        this(pWiki, useCache, false);
    }

    public WikiLinkComparator(Wikipedia pWiki, boolean useCache, boolean useOutboundLinks) {
        super(pWiki, Measure.WikiLinkMeasure, CombinationStrategy.Best);
        this.numberOfArticles = pWiki.getMetaData().getNumberOfPages() - pWiki.getMetaData().getNumberOfRedirectPages() - pWiki.getMetaData().getNumberOfDisambiguationPages();
        if (useCache) {
            this.cache = new WikiLinkCache(pWiki);
        }
        this.useOutboundLinks = useOutboundLinks;
    }

    @Override
    protected List<Double> computeRelatedness(Page article1, Page article2) throws WikiApiException {
        double relatedness = getRelatedness(article1, article2);
        List<Double> values = new ArrayList<Double>();
        values.add(relatedness);
        return values;
    }

    @Override
    protected double getBestRelatedness(List<Double> relatednessValues) {
        return getMaximum(relatednessValues);
    }

    /**
     * <p>
     * Calculates a weight of the semantic relation between this article and the argument one.
     * The stronger the semantic relation, the higher the weight returned.
     * i.e "6678: Cat" has a higher relatedness to "4269567: Dog" than to "27178: Shoe".
     * This is based on the links extending out from and in to each of the articles being compared.
     * </p>
     *
     * <p>
     * The details of this measure (and an evaluation) is described in the paper:
     * <br/>
     * Milne, D and Witten, I.H. (2008) An effective, low-cost measure of semantic relatedness obtained from Wikipedia links. In Proceedings of WIKIAI'08.
     * </p>
     *
     * <p>
     * If you only cache inLinks, then for efficiency's sake relatedness measures will only be calculated from them.
     * Measures obtained only from inLinks are only marginally less accurate than those obtained from both anyway.
     * </p>
     *
     * <p>
     * The reverse is true if you cache only outLinks, although that isnt reccomended. They take up much more memory, and
     * resulting measures are not as accurate.
     * </p>
     *
     * @param article1 The first article
     * @param article2 The second article
     * @return the weight of the semantic relation between this article and the argument one.
     * @throws WikiApiException
     */
    public double getRelatedness(Page article1, Page article2) throws WikiApiException {

//        if (cache.areOutLinksCached() && cache.areInLinksCached()) {
//            return (getRelatednessFromInLinks(article1, article2) + getRelatednessFromOutLinks(article1, article2))/2 ;
//        }
//
//        if (cache.areOutLinksCached()) {
//            return getRelatednessFromOutLinks(article1, article2) ;
//        }
//
//        if (cache.areInLinksCached()) {
//            return getRelatednessFromInLinks(article1, article2) ;
//        }
//
//        return (getRelatednessFromInLinks(article1, article2) + getRelatednessFromOutLinks(article1, article2))/2 ;

        // TODO TZ: I deactivated outlink relatedness computation as it is computationally more expansive and I do not fully understand David's code for that branch

        if(useOutboundLinks){
            return getRelatednessFromOutLinks(article1, article2) ;
        }
        else{
            return getRelatednessFromInLinks(article1, article2) ;
        }

    }

    private double getRelatednessFromInLinks(Page article1, Page article2) throws WikiApiException {

        if (article1.getPageId() == article2.getPageId()) {
            return 1.0;
        }

        int[] linksA = getLinksInIds(article1) ;
        int[] linksB = getLinksInIds(article2) ;

        return getRelatednessFromLinks(linksA, linksB);
    }

    private double getRelatednessFromOutLinks(Page article1, Page article2) throws WikiApiException {

        if (article1.getPageId() == article2.getPageId()) {
            return 1.0;
        }

        int[] linksA = getLinksOutIds(article1) ;
        int[] linksB = getLinksOutIds(article2) ;

        return getRelatednessFromLinks(linksA, linksB);
    }

    private double getRelatednessFromLinks(int[] linksA, int[] linksB)
    {
        int linksBoth = 0 ;

        int indexA = 0 ;
        int indexB = 0 ;

        while (indexA < linksA.length && indexB < linksB.length) {

            long idA = linksA[indexA] ;
            long idB = linksB[indexB] ;

            if (idA == idB) {
                linksBoth ++ ;
                indexA ++ ;
                indexB ++ ;
            } else if ((idA < idB && idA > 0)|| idB < 0) {
                indexA ++ ;
            } else {
                indexB ++ ;
            }
        }

        double a = Math.log(linksA.length) ;
        double b = Math.log(linksB.length) ;
        double ab = Math.log(linksBoth) ;
        double m = Math.log(this.numberOfArticles);

        double sr = (Math.max(a, b) -ab) / (m - Math.min(a, b));

        if (Double.isNaN(sr) || Double.isInfinite(sr) || sr > 1) {
			sr = 1 ;
		}

        sr = 1-sr ;

        return sr ;
    }

    /**
     * @return an ordered array of article ids that link to this page (with redirects resolved)
     * @throws WikiApiException
     */
    public int[] getLinksInIds(Page article) throws WikiApiException {

        if (cache == null) {
            return getInlinkIds(article);
        }
        else {
            return getCachedInlinkIds(article);
        }

    }

    /**
     * @return an ordered array of article ids that link from this page (with redirects resolved)
     * @throws WikiApiException
     */
    public int[] getLinksOutIds(Page article) throws WikiApiException {

        return getOutlinkIds(article);
        
    }

    protected static int[] getInlinkIds(Page article) {
        Set<Integer> inlinkIds = article.getInlinkIDs();
        int[] ids = new int[inlinkIds.size()];
        int n = 0;
        for (Integer id : inlinkIds) {
            ids[n] = id;
            n++;
        }
        Arrays.sort(ids);
        return ids;
    }

    protected static int[] getOutlinkIds(Page article) {
        Set<Integer> outlinkIds = article.getOutlinkIDs();
        int[] ids = new int[outlinkIds.size()];
        int n = 0;
        for (Integer id : outlinkIds) {
            ids[n] = id;;
            n++;
        }
        Arrays.sort(ids);
        return ids;
    }

    private int[] getCachedInlinkIds(Page article) throws WikiApiException {
        if (cache.isCacheEmpty()) {
            try {
                cache.fillInLinkCache();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new WikiApiException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new WikiApiException(e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new WikiApiException(e);
            }
        }

        if (cache.cachedInLinks.containsKey(article.getPageId())) {
            return cache.cachedInLinks.get(article.getPageId()) ;
        }
        else {
            return new int[0] ;
        }
    }

//    private double getRelatednessFromOutLinks(Page article1, Page article2) {
//
//        if (article1.getPageId() == article2.getPageId()) {
//            return 1 ;
//        }
//
//        int[][] dataA = getLinksOutIdsAndCounts(article1) ;
//        int[][] dataB = getLinksOutIdsAndCounts(article2) ;
//
//        if (dataA.length == 0 || dataB.length == 0)
//            return 0 ;
//
//        int indexA = 0 ;
//        int indexB = 0 ;
//
//        List<Double> vectA = new ArrayList<Double>() ;
//        List<Double> vectB = new ArrayList<Double>() ;
//
//        while (indexA < dataA.length || indexB < dataB.length) {
//
//            int idA = -1 ;
//            int idB = -1 ;
//
//            if (indexA < dataA.length)
//                idA = dataA[indexA][0] ;
//
//            if (indexB < dataB.length)
//                idB = dataB[indexB][0] ;
//
//            if (idA == idB) {
//                double probability = Math.log((double)this.numberOfArticles/dataA[indexA][1]) ;
//                vectA.add(new Double(probability)) ;
//                vectB.add(new Double(probability)) ;
//
//                indexA ++ ;
//                indexB ++ ;
//            } else if ((idA < idB && idA > 0)|| idB < 0) {
//                double probability = Math.log((double)this.numberOfArticles/dataA[indexA][1]) ;
//                vectA.add(new Double(probability)) ;
//                vectB.add(new Double(0)) ;
//
//                indexA ++ ;
//            } else {
//                double probability = Math.log((double)this.numberOfArticles/dataB[indexB][1]) ;
//                vectA.add(new Double(0)) ;
//                vectB.add(new Double(probability)) ;
//
//                indexB ++ ;
//            }
//        }
//
//        // calculate angle between vectors
//        double dotProduct = 0 ;
//        double magnitudeA = 0 ;
//        double magnitudeB = 0 ;
//
//        for (int x=0;x<vectA.size();x++) {
//            double valA = ((Double)vectA.get(x)).doubleValue() ;
//            double valB = ((Double)vectB.get(x)).doubleValue() ;
//
//            dotProduct = dotProduct + (valA * valB) ;
//            magnitudeA = magnitudeA + (valA * valA) ;
//            magnitudeB = magnitudeB + (valB * valB) ;
//        }
//
//        magnitudeA = Math.sqrt(magnitudeA) ;
//        magnitudeB = Math.sqrt(magnitudeB) ;
//
//        double sr = Math.acos(dotProduct / (magnitudeA * magnitudeB)) ;
//        sr = (Math.PI/2) - sr ; // reverse, so 0=no relation, PI/2= same
//        sr = sr / (Math.PI/2) ; // normalize, so measure is between 0 and 1 ;
//
//        return sr ;
//    }
//
//
//    /**
//     * @return an ordered array of article ids that this page links to (with redirects resolved)
//     */
//    public int[] getLinksOutIds(Page article) {
//
//        int[] outLinkIds;
//
//        if (cache.areInLinksCached()){
//            if (cache.cachedInLinks.containsKey(article.getPageId())) {
//                return cache.cachedInLinks.get(article.getPageId()) ;
//            }
//            else {
//                return new int[0] ;
//            }
//        }
//
//        Set<Integer> ids = article.getOutlinkIDs();
//        outLinkIds = new int[ids.size()];
//
//        List<Integer> idsSorted = new ArrayList<Integer>( ids );
//        Collections.sort(idsSorted);
//
//        int i=0;
//        for (int id : ids) {
//            outLinkIds[i] = id;
//            i++;
//        }
//        return outLinkIds ;
//    }

//    /**
//     * @param article
//     * @return An array of
//     */
//    private int[][] getLinksOutIdsAndCounts(Page article) {
//
//        int[][] outLinkIdsAndCounts;
//
//        if (cache.areOutLinksCached()) {
//            if (cache.cachedOutLinks.containsKey(article.getPageId())) {
//                return cache.cachedOutLinks.get(article.getPageId()) ;
//            }
//            else {
//                return new int[0][2] ;
//            }
//        }
//
//        String data = "" ;
//
//        Statement stmt = database.createStatement() ;
//        ResultSet rs = stmt.executeQuery("SELECT lo_data FROM pagelink_out WHERE lo_id = " + id) ;
//
//        if (rs.first())
//            data = rs.getString(1) ;
//
//        stmt.close() ;
//        rs.close();
//
//        String[] values = data.split(";") ;
//
//        outLinkIdsAndCounts = new int[values.length][2] ;
//
//        int index = 0 ;
//        for (String value:values) {
//            String values2[] = value.split(":") ;
//
//            if (values2.length == 2) {
//                outLinkIdsAndCounts[index][0] = Integer.parseInt(values2[0]) ;
//                outLinkIdsAndCounts[index][1] = Integer.parseInt(values2[1]) ;
//            }
//            index ++ ;
//        }
//
//        return outLinkIdsAndCounts ;
//    }

}