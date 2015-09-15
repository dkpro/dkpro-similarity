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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory;
import dkpro.similarity.algorithms.lsr.LexSemResourceComparator;
import dkpro.similarity.algorithms.lsr.path.LinComparator;


public class LinComparatorTest
{
    private static final double epsilon = 0.0001;

    @Ignore("There seems to be something wrong with the test database. Error: No category with name Anime-influenced_animatio_(non-commercial) was found.")
    @Test
	public void testLinWikipediaCategory()
		throws Exception
	{
        LexicalSemanticResource wikiResource = ResourceFactory.getInstance().get("wikipedia_category", "test");

        LexSemResourceComparator comparator = new LinComparator(wikiResource);

        runWikipediaCategoryTests(comparator, wikiResource);
    }

    @Ignore("There seems to be something wrong with the test database. Error: No category with name Anime-influenced_animatio_(non-commercial) was found.")
    @Test
	public void testLinWikipediaCategorySetRoot()
		throws Exception
	{
        LexicalSemanticResource wikiResource = ResourceFactory.getInstance().get("wikipedia_category", "test");

        Map<String,String> rootLexemes = new HashMap<String,String>();
        rootLexemes.put("Telecooperation", Entity.UNKNOWN_SENSE);

        LexSemResourceComparator comparator = new LinComparator(wikiResource, new Entity(rootLexemes, Entity.UNKNOWN_POS));

        runWikipediaCategoryTests(comparator, wikiResource);
    }

	private void runWikipediaCategoryTests(LexSemResourceComparator comparator, LexicalSemanticResource wikiResource)
		throws Exception
	{
		assertEquals("LinComparator", comparator.getName());

        Set<Entity> entitiesAqua = wikiResource.getEntity("AQUA");
        Set<Entity> entitiesSir  = wikiResource.getEntity("SIR");
        Set<Entity> entitiesTk   = wikiResource.getEntity("Telecooperation");

        assertTrue("AQUA", entitiesAqua.size() > 0);
        assertTrue("SIR", entitiesSir.size() > 0);
        assertTrue("Tk", entitiesTk.size() > 0);

        // same page
        assertEquals(1.0, comparator.getSimilarity(entitiesAqua, entitiesAqua), epsilon);

        // different pages
        // IIC(Aqua) = 1
        // IIC(Sir) = 1
        // IIC(LCS) = 1 - (Math.log(3)/Math.log(16)
        // (2*IIC(LCS) / 1 + 1 = IIC(LCS)
        double iicLcs = 1 - (Math.log(3)/Math.log(16));
        assertEquals( iicLcs, comparator.getSimilarity(entitiesAqua, entitiesSir), epsilon);

        // different pages
        // IIC(Aqua) = 1
        // IIC(TK) = 0
        // IIC(LCS) = 0
        assertEquals(0.0, comparator.getSimilarity(entitiesAqua, entitiesTk), epsilon);

    }

    @Test
    @Ignore("See bug 164")
	public void testLinGermaNet()
		throws Exception
	{
    	LexicalSemanticResource germaNet = ResourceFactory.getInstance().get("germanet", "de");

    	LexSemResourceComparator comparator = new LinComparator(germaNet, germaNet.getRoot());
        assertEquals("LinComparator", comparator.getName());

        Set<Entity> entities1 = germaNet.getEntity("Quelle");
        Set<Entity> entities2 = germaNet.getEntity("Text");

        Set<Entity> entities3 = germaNet.getEntity("Kommunikation");
        Set<Entity> entities4 = germaNet.getEntity("überzeugen");

        Set<Entity> entities5 = germaNet.getEntity("Macht");
        Set<Entity> entities6 = germaNet.getEntity("Reich");

        assertEquals(-1.0, comparator.getSimilarity(entities1, entities2), epsilon);

        assertEquals(-1.0, comparator.getSimilarity(entities3, entities4), epsilon);

        assertEquals(0.0, comparator.getSimilarity(entities5, entities6), epsilon);
    }

    //FIXME reactivate after aggregation strategy has been moved to new relatedness package 
    @Ignore
    @Test
	public void testLinBipartite()
		throws Exception
	{
//    	LexicalSemanticResource wordnet = ResourceFactory.getInstance().get("wordnet3", "en");

//    	WbpgmAggregator comparator =
//    		new WbpgmAggregator(new LinComparator(wordnet), Normalization.NONE);
//        assertEquals("LinComparator-WbpgmAggregator", comparator.getName());
//
//        String[] s1_1 = new String[] { "test" };
//        String[] s2_1 = new String[] { "test" };
//
//        String[] s1_2 = new String[] { "test" };
//        String[] s2_2 = new String[] { "test", "fest" };
//
//        String[] s1_3 = new String[] { "diamond", "test" };
//        String[] s2_3 = new String[] { "stone", "test" };
//
//        String[] s1_4 = new String[] { "diamond" };
//        String[] s2_4 = new String[] { "stone" };
//
//        String[] s1_5 = new String[] { "needle", "diamond", "test" };
//        String[] s2_5 = new String[] { "aircraft", "stone" };
//
//        String[] s1_6 = new String[] { "needle" };
//        String[] s2_6 = new String[] { "aircraft" };
//
//        double sim1 = comparator.getRelatedness(asList(s1_1), asList(s2_1));
//        double sim2 = comparator.getRelatedness(asList(s1_2), asList(s2_2));
//        double sim3 = comparator.getRelatedness(asList(s1_3), asList(s2_3));
//        double sim4 = comparator.getRelatedness(asList(s1_4), asList(s2_4));
//        double sim5 = comparator.getRelatedness(asList(s1_5), asList(s2_5));
//        double sim6 = comparator.getRelatedness(asList(s1_6), asList(s2_6));
//
//        System.out.print("");
    }
}