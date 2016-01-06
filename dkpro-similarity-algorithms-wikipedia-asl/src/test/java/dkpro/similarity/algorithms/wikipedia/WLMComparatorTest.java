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
import dkpro.similarity.algorithms.wikipedia.measures.WikiLinkComparator;

public class WLMComparatorTest {

    private static final double epsilon = 0.0001;

    private Wikipedia wiki;

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


    @Test
    @Ignore
	public void testWlvmWithCache()
		throws Exception
	{
		WikiLinkComparator wlc = new WikiLinkComparator(wiki, true);
        runTest(wlc);
    }

    @Test
    @Ignore
	public void testWlvmWithoutCache()
		throws Exception
	{
		WikiLinkComparator wlc = new WikiLinkComparator(wiki, false);
        runTest(wlc);
    }

	private void runTest(WikiLinkComparator wlc)
		throws Exception
	{
		// do not use JWPL database cache, as this interferes with correct testing
        // correct behaviour of caching should be tested in another method
        wlc.setUseCache(false);

        // same page
        String token1 = "TK3";
        String token2 = "TK3";
        double relatednessBest = wlc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);

        // different pages
        token1 = "TK1";
        token2 = "Peer-to-Peer_and_Grid_Computing";
        relatednessBest = wlc.getSimilarity(token1, token2);
        assertEquals(0.79196, relatednessBest, epsilon);

        // different pages 2
        token1 = "AQUA";
        token2 = "UKP";
        relatednessBest = wlc.getSimilarity(token1, token2);
        assertEquals(0.517, relatednessBest, epsilon);

        // unconnected pages
        token1 = "NCS";
        token2 = "Unconnected page";
        relatednessBest = wlc.getSimilarity(token1, token2);
        assertEquals(0.0, relatednessBest, epsilon);

        // page and redirect
        token1 = "NCS";
        token2 = "Net_Centric_Systems";
        relatednessBest = wlc.getSimilarity(token1, token2);
        assertEquals(1.0, relatednessBest, epsilon);
    }
}