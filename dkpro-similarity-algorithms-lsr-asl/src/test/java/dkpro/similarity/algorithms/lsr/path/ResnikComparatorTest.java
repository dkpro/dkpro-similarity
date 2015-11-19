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
package dkpro.similarity.algorithms.lsr.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory;
import dkpro.similarity.algorithms.lsr.LexSemResourceComparator;
import dkpro.similarity.algorithms.lsr.path.ResnikComparator;

public class ResnikComparatorTest
{
    private static final double epsilon = 0.0001;

    @Ignore("There seems to be something wrong with the test database. Error: No category with name Anime-influenced_animatio_(non-commercial) was found.")
    @Test
	public void testResnikWikipediaCategory()
		throws Exception
	{
        LexicalSemanticResource wikiResource = ResourceFactory.getInstance().get("wikipedia_category", "test");

        LexSemResourceComparator comparator = new ResnikComparator(wikiResource);
        assertEquals("ResnikComparator", comparator.getName());


        Set<Entity> entitiesAqua = wikiResource.getEntity("AQUA");
        Set<Entity> entitiesSir  = wikiResource.getEntity("SIR");
        Set<Entity> entitiesTk   = wikiResource.getEntity("Telecooperation");

        assertTrue("AQUA", entitiesAqua.size() > 0);
        assertTrue("SIR", entitiesSir.size() > 0);
        assertTrue("Tk", entitiesTk.size() > 0);

        // same page
        assertEquals(1.0, comparator.getSimilarity(entitiesAqua, entitiesAqua), epsilon);

        // different pages
        assertEquals(1 - (Math.log(3)/Math.log(16)), comparator.getSimilarity(entitiesAqua, entitiesSir), epsilon);

        // different pages
        assertEquals(0.0, comparator.getSimilarity(entitiesAqua, entitiesTk), epsilon);
    }

    @Test
	public void testResnikWordnet()
		throws Exception
    {
        
        Assume.assumeTrue(Runtime.getRuntime().maxMemory() > 1000000000);

    	LexicalSemanticResource wordnet = ResourceFactory.getInstance().get("wordnet3", "en");

        wordnet.setIsCaseSensitive(false);

        LexSemResourceComparator comparator = new ResnikComparator(wordnet, wordnet.getRoot());
        assertEquals("ResnikComparator", comparator.getName());

        Set<Entity> entities1 = wordnet.getEntity("gem");
        Set<Entity> entities2 = wordnet.getEntity("jewel");

        assertTrue("gem",   entities1.size() > 0);
        assertTrue("jewel", entities2.size() > 0);

        // same page
        assertEquals(1.0, comparator.getSimilarity(entities1, entities2), epsilon);

        // symmetry
        assertEquals(1.0, comparator.getSimilarity(entities2, entities1), epsilon);
    }

}