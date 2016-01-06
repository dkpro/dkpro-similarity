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
package dkpro.similarity.algorithms.wikipedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasure;
import dkpro.similarity.algorithms.wikipedia.WikipediaBasedComparator;
import dkpro.similarity.algorithms.wikipedia.WikipediaBasedComparator.WikipediaBasedRelatednessMeasure;


public class WikipediaBasedComparatorTest
{
	private Wikipedia wiki;

    private static final double epsilon = 0.0001;

//    @Before
	public void setupWikipedia()
		throws WikiApiException
	{
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
    }

	private void comparatorTest(WikipediaBasedRelatednessMeasure aMeasure, String aToken1,
			String aToken2, double aExpectedRelatedness)
		throws Exception
	{
    	TermSimilarityMeasure jcc = new WikipediaBasedComparator(wiki, aMeasure, false);

        double relatednessBest = jcc.getSimilarity(aToken1, aToken2);

        System.out.println("Measure  : "+aMeasure);
        System.out.println("Tokens   : ["+aToken1+"] ["+aToken2+"]");
        System.out.println("Expected : "+aExpectedRelatedness);
        System.out.println("Actual   : "+relatednessBest);

        assertEquals(aExpectedRelatedness, relatednessBest, epsilon);
    }

    /**
     * same page
     */
    @Test
    @Ignore
	public void testJiangConrath_samePage()
		throws Exception
	{
    	comparatorTest(WikipediaBasedRelatednessMeasure.JiangConrath, "TK3", "TK3", 1.0);
    }

    /**
     * different pages
     */
    @Test
    @Ignore
	public void testJiangConrath_differentPages()
		throws Exception
	{
    	// IC = 1 - ( log( hypo(n) + 1) / log(#cat) )
        // IC(token1) = 1
        // IC(token2) ~ 0.1872
        // IC(lcs) = 0
    	comparatorTest(WikipediaBasedRelatednessMeasure.JiangConrath, "Iryna Gurevych", "UKP", 0.4063);
    }

    /**
     * unconnected pages
     */
    @Test
    @Ignore
	public void testJiangConrath_unconnectedPages()
		throws Exception
	{
    	comparatorTest(WikipediaBasedRelatednessMeasure.JiangConrath, "NCS", "Unconnected page", -3.0);
    }

    /**
     * page and redirect
     */
    @Test
    @Ignore
	public void testJiangConrath_pageAndRedirect()
		throws Exception
	{
    	comparatorTest(WikipediaBasedRelatednessMeasure.JiangConrath, "NCS", "Net_Centric_Systems", 1.0);
    }

    /**
     * non-root LCS
     */
    @Test
    @Ignore
	public void testJiangConrath_nonRootLcs()
		throws Exception
	{
        // lcs = UKP
        // IC(token1) = 1
        // IC(token2) ~ 0.1872
        // IC(lcs) = IC(token2) ~ 0.1872
    	comparatorTest(WikipediaBasedRelatednessMeasure.JiangConrath, "AQUA", "UKP", 0.5936);
    }

    public void testLeacockChodorow() throws SimilarityException
    {
    	TermSimilarityMeasure lcc;
        lcc = new WikipediaBasedComparator(wiki, WikipediaBasedRelatednessMeasure.LeacockChodorow, false);

        // same page
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = lcc.getSimilarity(token1, token2);
        assertEquals(Math.log(8), relatednessBest, epsilon);

        // different pages
        // lch(c1,c2) = log ( 2*depth / (minPL(c1,c2) + 1) )
        // depth = 4
        // minPL = 2
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = lcc.getSimilarity(token1, token2);
        assertEquals(0.9808, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = lcc.getSimilarity(token1, token2);
        assertEquals(-1.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = lcc.getSimilarity(token1, token2);
        assertEquals(Math.log(8), relatednessBest, epsilon);

        // one edge / zero nodes distance
        // minPL = 1
        // depth = 4
        // log( 2*4 / (1+1)) = log(4)
        token1 = "UKP";
        token2 = "Natural Language Processing for Ambient Intelligence";
        relatednessBest = lcc.getSimilarity(token1, token2);
        assertEquals(Math.log(4), relatednessBest, epsilon);
    }

    // lin(c1,c2) = 2 * ic ( lcs(c1,c2) ) / IC(c1) + IC(c2)
    @Test
    @Ignore
	public void testLinMeasure()
		throws Exception
	{
    	TermSimilarityMeasure lc = new WikipediaBasedComparator(wiki, WikipediaBasedRelatednessMeasure.Lin, false);

        // same page
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = lc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // different pages
        // IC(token1) = 1
        // IC(token2) ~ 0.1872
        // IC(lcs) = 0
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = lc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = lc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = lc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // non-root LCS
        // IC(token1) = 1
        // IC(token2) ~ 0.1872
        // IC(lcs) = IC(token2) ~ 0.1872
        token1 = "AQUA";
        token2 = "UKP";
        relatednessBest = lc.getSimilarity(token1, token2);
        assertEquals(0.3155, relatednessBest, epsilon);
    }

    @Test
    @Ignore
	public void testPathLengthMeasure()
		throws Exception
	{
    	TermSimilarityMeasure plc = new WikipediaBasedComparator(wiki, WikipediaBasedRelatednessMeasure.PathLength, false);

        // same page
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = plc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // different pages
        // pathlength in Edges = 2
        // but as we use taxonomically bound paths to the root, it is 5
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = plc.getSimilarity(token1, token2);
        assertEquals(5.0, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = plc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = plc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // one edge / zero nodes distance
        // but as we use taxonomically bound paths to the root, it is 4
        token1 = "UKP";
        token2 = "Natural Language Processing for Ambient Intelligence";
        relatednessBest = plc.getSimilarity(token1, token2);
        assertEquals(4.0, relatednessBest, epsilon);
    }

    // res(c1,c2) = IC ( lcs(c1,c2) )
    @Test
    @Ignore
	public void testResnikMeasure()
		throws Exception
	{
    	TermSimilarityMeasure rc = new WikipediaBasedComparator(wiki, WikipediaBasedRelatednessMeasure.Resnik, false);

        // same page
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = rc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // different pages
        // IC(lcs) = 0
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = rc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = rc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = rc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);
    }

    // wp(c1,c2) = 2 * depth(lcs(c1,c2) / ( pl(c1,lcs(c1,c2)) + pl(c2,lcs(c1,c2)) + 2 * depth(lcs(c1,c2)) )
    @Test
    @Ignore
	public void testWuPalmerMeasure()
		throws Exception
	{
    	TermSimilarityMeasure wpc = new WikipediaBasedComparator(wiki, WikipediaBasedRelatednessMeasure.WuPalmer, false);

        // same page
        // depth(lcs) = 1
        // pl(c1,lcs) = 0
        // pl(c2,lcs) = 0
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = wpc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // different pages
        // depth(lcs) = 0
        // pl(c1,lcs) = 1
        // pl(c2,lcs) = 4
        token1 = "Iryna Gurevych";
        token2 = "UKP";
        relatednessBest = wpc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // different pages
        // depth(lcs) = 0
        // pl(c1,lcs) = 1
        // pl(c2,lcs) = 1
        token1 = "UKP";
        token2 = "Natural Language Processing for Ambient Intelligence";
        relatednessBest = wpc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = wpc.getSimilarity(token1, token2);
        assertEquals(-3.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = wpc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);
    }
}