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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.wikipedia.api.CategoryGraph;
import de.tudarmstadt.ukp.wikipedia.api.CategoryGraphManager;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import dkpro.similarity.algorithms.wikipedia.measures.JiangConrathBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.LeacockChodorowBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.LinBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.PathLengthBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.ResnikBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.WuPalmerBestComparator;

public class MeasuresTest {

    private static final double epsilon = 0.0001;

    private static Wikipedia wiki;
    private static CategoryGraph catGraph;

//    @BeforeClass
    public static void setupWikipedia() throws WikiApiException {
        DatabaseConfiguration db = new DatabaseConfiguration();
        db.setDatabase("wikiapi_test");
        db.setHost("bender.ukp.informatik.tu-darmstadt.de");
        db.setUser("student");
        db.setPassword("student");
        db.setLanguage(Language._test);
        try {
            wiki = new Wikipedia(db);
        } catch (WikiInitializationException e) {
            fail("Wikipedia could not be initialized.");
        }

        catGraph = CategoryGraphManager.getCategoryGraph(wiki, false);

    }

//    @AfterClass
    public static void cleanUp() throws WikiApiException {
        catGraph.deleteRootPathMap();
    }

    // jc(c1,c2) = IC(c1) + IC(c2) - 2* IC( lcs(c1,c2) )
    // distance measure! 0.0 is best value.
    @Test
    @Ignore
    public void testJiangConrathMeasure() throws Exception {
        JiangConrathBestComparator jcbc = new JiangConrathBestComparator(wiki, catGraph);

        // do not use cache, as this interferes with correct testing
        // correct behaviour of caching should be tested in another method
        jcbc.setUseCache(false);

        // unconnected pages
        String token1 = "NCS";
        String token2 = "Unconnected page";
        double relatednessBest = jcbc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // same page
        token1 = "TK3";
        token2 = "TK3";
        relatednessBest = jcbc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = jcbc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // different pages
        // IC = 1 - ( log( hypo(n) + 1) / log(#cat) )
        // IC(token1) = 1
        // IC(token2) ~ 0.1872
        // IC(lcs) = 0
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = jcbc.getSimilarity(token1, token2);
        System.out.println(relatednessBest);
        assertEquals(0.4063, relatednessBest, epsilon);

        // non-root LCS
        // lcs = UKP
        // IC(token1) = 1
        // IC(token2) ~ 0.1872
        // IC(lcs) = IC(token2) ~ 0.1872
        token1 = "AQUA";
        token2 = "UKP";
        relatednessBest = jcbc.getSimilarity(token1, token2);
        assertEquals(0.5936, relatednessBest, epsilon);
    }

    // lch(c1,c2) = - log( (minPL(c1,c2) + 1) / 2 * depth) = log( 2*depth / (minPL(c1,c2) + 1) )
    // minPL is measured in edges, i.e. the distance of a node to itself is 1!
    // (A distance of 0, would cause logarithm error (or a division by zero)).
    @Test
    @Ignore
    public void testLeacockChodorowMeasure() throws Exception {
        LeacockChodorowBestComparator lcbc = new LeacockChodorowBestComparator(wiki, catGraph);
        lcbc.setUseCache(false);

        // same page
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = lcbc.getSimilarity(token1, token2);
        assertEquals(Math.log(8), relatednessBest, epsilon);

        // different pages
        // lch(c1,c2) = log ( 2*depth / (minPL(c1,c2) + 1) )
        // depth = 4
        // minPL = 5    // actually it is 2, but as we use taxonomically bound paths (shortest paths to the root), we miss the shorter path
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = lcbc.getSimilarity(token1, token2);
        assertEquals(0.2877, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = lcbc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = lcbc.getSimilarity(token1, token2);
        assertEquals(Math.log(8), relatednessBest, epsilon);

        // one edge / zero nodes distance
        // minPL = 4    // actually it is 1, but as we use taxonomically bound paths (shortest paths to the root), we miss the shorter path
        // depth = 4
        // log( 2*4 / (4+1)) = log(8/5)
        token1 = "UKP";
        token2 = "Natural Language Processing for Ambient Intelligence";
        relatednessBest = lcbc.getSimilarity(token1, token2);
        assertEquals(0.4700, relatednessBest, epsilon);
    }

    // lin(c1,c2) = 2 * ic ( lcs(c1,c2) ) / IC(c1) + IC(c2)
    @Test
    @Ignore
    public void testLinMeasure() throws Exception {
        LinBestComparator lbc = new LinBestComparator(wiki, catGraph);
        lbc.setUseCache(false);

        // same page
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = lbc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // different pages
        // IC(token1) = 1
        // IC(token2) ~ 0.1872
        // IC(lcs) = 0
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = lbc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = lbc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = lbc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // non-root LCS
        // IC(token1) = 1
        // IC(token2) ~ 0.1872
        // IC(lcs) = IC(token2) ~ 0.1872
        token1 = "AQUA";
        token2 = "UKP";
        relatednessBest = lbc.getSimilarity(token1, token2);
        assertEquals(0.3155, relatednessBest, epsilon);

    }

    @Test
    @Ignore
    public void testPathLengthMeasure() throws Exception {
        PathLengthBestComparator plbc = new PathLengthBestComparator(wiki, catGraph);
        plbc.setUseCache(false);

        // same page
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = plbc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // different pages
        // pathlength in Edges = 5
        // actually it is 2, but as we use taxonomically bound paths (shortest paths to the root), we miss the shorter path
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = plbc.getSimilarity(token1, token2);
        assertEquals(5.0, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = plbc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = plbc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // actually it is 1, but as we use taxonomically bound paths (shortest paths to the root), we miss the shorter path
        token1 = "UKP";
        token2 = "Natural Language Processing for Ambient Intelligence";
        relatednessBest = plbc.getSimilarity(token1, token2);
        assertEquals(4.0, relatednessBest, epsilon);
    }

    // res(c1,c2) = IC ( lcs(c1,c2) )
    @Test
    @Ignore
    public void testResnikMeasure() throws Exception {
        ResnikBestComparator rbc = new ResnikBestComparator(wiki, catGraph);
        rbc.setUseCache(false);

        // same page
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = rbc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // different pages
        // IC(lcs) = 0
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = rbc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = rbc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = rbc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);
    }

    // wp(c1,c2) = 2 * depth(lcs(c1,c2) / ( pl(c1,lcs(c1,c2)) + pl(c2,lcs(c1,c2)) + 2 * depth(lcs(c1,c2)) )
    @Test
    @Ignore
    public void testWuPalmerMeasure() throws Exception {
        WuPalmerBestComparator wpbc = new WuPalmerBestComparator(wiki, catGraph);
        wpbc.setUseCache(false);

        // same page
        // depth(lcs) = 1
        // pl(c1,lcs) = 0
        // pl(c2,lcs) = 0
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = wpbc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // different pages
        // depth(lcs) = 0
        // pl(c1,lcs) = 1
        // pl(c2,lcs) = 4
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = wpbc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // different pages
        // depth(lcs) = 0
        // pl(c1,lcs) = 1
        // pl(c2,lcs) = 1
        token1 = "UKP";
        token2 = "Natural Language Processing for Ambient Intelligence";
        relatednessBest = wpbc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = wpbc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = wpbc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

    }

}