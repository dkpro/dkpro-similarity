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
package dkpro.similarity.algorithms.lsr.gloss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiException;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.lsr.LexSemResourceComparator;
import dkpro.similarity.algorithms.lsr.gloss.GlossOverlapComparator;
import dkpro.similarity.algorithms.wikipedia.measures.LeskComparator;
import dkpro.similarity.algorithms.wikipedia.measures.LeskFirstComparator;


public class GlossOverlapComparatorTest
{
    private static final double epsilon = 0.0001;

    private Wikipedia wiki;
    private LexicalSemanticResource wikiResource;
    private LexicalSemanticResource wktResource;

	@Before
	public void setupWikipedia()
		throws Exception
	{
        wikiResource = ResourceFactory.getInstance().get("wikipedia", "en");
        wikiResource.setIsCaseSensitive(false);
    }


//	@Before
//    public void setupWiktionary()
//        throws Exception
//    {
//        wktResource = ResourceFactory.getInstance().get("wiktionary", "de");
//    }

	@Test
	public void testGlossOverlapWikipedia()
		throws Exception
	{
        LexSemResourceComparator comparator = new GlossOverlapComparator(wikiResource, false);

        Set<Entity> entitiesAuto  = wikiResource.getEntity("Automobile");
        Set<Entity> entitiesTruck = wikiResource.getEntity("Truck");

        assertTrue("Auto",  entitiesAuto.size() > 0);
        assertTrue("Truck", entitiesTruck.size() > 0);

        // same page
        assertEquals(1.0, comparator.getSimilarity(entitiesAuto, entitiesAuto), epsilon);

        // different pages
        assertEquals(0.2931, comparator.getSimilarity(entitiesAuto, entitiesTruck), epsilon);
    }

    @Ignore("Currently deactivated as Wiktionary resource data is not for API version is not available.")
	@Test
	public void testGlossOverlapWiktionary()
		throws LexicalSemanticResourceException, SimilarityException
	{
		LexSemResourceComparator comparator = new GlossOverlapComparator(wktResource, false);

        Set<Entity> entities1  = wktResource.getEntity("Bayern");
        Set<Entity> entities2 = wktResource.getEntity("Deutschland");

        assertTrue("Bayern",  entities1.size() > 0);
        assertTrue("Deutschland", entities2.size() > 0);

        // same page
        assertEquals(1.0, comparator.getSimilarity(entities1, entities1), epsilon);

        // different pages
        assertEquals(0.22222, comparator.getSimilarity(entities1, entities2), epsilon);
    }

    // LSR-based comparator and JWPL-based comparator should yield equal results
	// See Bug 144
	@Test
	@Ignore("TODO for some reason the leskComparator gloss is from the disambiguation page - need to discover why")
	public void comparisonWithJwplImplementationTest()
		throws LexicalSemanticResourceException, WikiException, SimilarityException
	{
		wikiResource.setIsCaseSensitive(true);
		LexSemResourceComparator glossOverlapComparator = new GlossOverlapComparator(wikiResource, false);

        String term1 = "Automobile";
        String term2 = "Truck";

        Set<Entity> entitiesAuto  = wikiResource.getEntity(term1);
        Set<Entity> entitiesTruck = wikiResource.getEntity(term2);

        LeskComparator leskComparator = new LeskFirstComparator(wiki);
        leskComparator.setUseCache(false);

        assertEquals(glossOverlapComparator.getSimilarity(entitiesAuto, entitiesTruck), leskComparator.getSimilarity(term1, term2), epsilon);

        wikiResource.setIsCaseSensitive(false);
    }
}