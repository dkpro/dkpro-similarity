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
import dkpro.similarity.algorithms.lsr.path.JiangConrathComparator;


public class JiangConrathComparatorTest
{
    private static final double epsilon = 0.0001;

    @Ignore("There seems to be something wrong with the test database. Error: No category with name Anime-influenced_animatio_(non-commercial) was found.")
    @Test
	public void testJiangConrathWikipediaCategory()
		throws Exception
	{
        LexicalSemanticResource wikiResource = ResourceFactory.getInstance().get("wikipedia_category", "test");

        LexSemResourceComparator comparator = new JiangConrathComparator(wikiResource);
        assertEquals("JiangConrathComparator", comparator.getName());

        Set<Entity> entitiesAqua = wikiResource.getEntity("AQUA");
        Set<Entity> entitiesSir  = wikiResource.getEntity("SIR");
        Set<Entity> entitiesTk   = wikiResource.getEntity("Telecooperation");

        assertTrue("AQUA", entitiesAqua.size() > 0);
        assertTrue("SIR",  entitiesSir.size()  > 0);
        assertTrue("Tk",   entitiesTk.size()   > 0);

        // same page
        assertEquals(1.0, comparator.getSimilarity(entitiesAqua, entitiesAqua), epsilon);

        // different pages
        // IIC(Aqua) = 1
        // IIC(Sir) = 1
        // IIC(LCS) = 1 - (Math.log(3)/Math.log(16)
        // (2 - 2 + 2*IIC(LCS)))/2 = 2*IIC(LCS)/2 = IIC(LCS)
        double iicLcs = 1 - (Math.log(3)/Math.log(16));
        assertEquals( iicLcs, comparator.getSimilarity(entitiesAqua, entitiesSir), epsilon);

        // different pages
        assertEquals(0.5, comparator.getSimilarity(entitiesAqua, entitiesTk), epsilon);
    }

    @Test
	public void testJiangConrathWordnet()
		throws Exception
	{
        
        Assume.assumeTrue(Runtime.getRuntime().maxMemory() > 1000000000);

        LexicalSemanticResource wordnet = ResourceFactory.getInstance().get("wordnet3", "en");
        wordnet.setIsCaseSensitive(false);

        LexSemResourceComparator comparator = new JiangConrathComparator(wordnet, wordnet.getRoot());
        assertEquals("JiangConrathComparator", comparator.getName());

        Set<Entity> entitiesEntity = wordnet.getEntity("entity");
        Set<Entity> entitiesCar    = wordnet.getEntity("car");

        assertTrue("entity", entitiesEntity.size() > 0);
        assertTrue("car",  entitiesCar.size()  > 0);

        // same page
        assertEquals(1.0, comparator.getSimilarity(entitiesEntity, entitiesEntity), epsilon);

        // different pages
        assertEquals(0.66515, comparator.getSimilarity(entitiesEntity, entitiesCar), epsilon);
    }

    @Ignore("The original GermaNet API is not Apache licensed.")
    @Test
	public void testJiangConrathGermaNet()
		throws Exception
	{
        LexicalSemanticResource germaNet = ResourceFactory.getInstance().get("germanet", "de");

        LexSemResourceComparator comparator = new JiangConrathComparator(germaNet, germaNet.getRoot());
        assertEquals("JiangConrathComparator", comparator.getName());

        Set<Entity> entitiesEntity = germaNet.getEntity("Entität");
        Set<Entity> entitiesAuto   = germaNet.getEntity("Auto");

        assertTrue("Entität", entitiesEntity.size() > 0);
        assertTrue("Auto",  entitiesAuto.size()  > 0);

        // same page
        assertEquals(1.0, comparator.getSimilarity(entitiesEntity, entitiesEntity), epsilon);

        // different pages
        assertEquals(0.74965, comparator.getSimilarity(entitiesEntity, entitiesAuto), epsilon);
    }
}