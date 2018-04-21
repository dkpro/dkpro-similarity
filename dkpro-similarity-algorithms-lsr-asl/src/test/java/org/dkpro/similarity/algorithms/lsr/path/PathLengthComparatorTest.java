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
package org.dkpro.similarity.algorithms.lsr.path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.dkpro.similarity.algorithms.lsr.LexSemResourceComparator;
import org.dkpro.similarity.algorithms.lsr.path.PathLengthComparator;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity.PoS;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LSRFramework;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory;

public class PathLengthComparatorTest {

    private static final double epsilon = 0.0001;

    private static String oldWorkspace;
    
    @BeforeClass
    public static void setup()
    {
        oldWorkspace = System.getProperty(LSRFramework.SYS_LSR_WORKSPACE);
        System.setProperty(LSRFramework.SYS_LSR_WORKSPACE,
                "target/test-output/PathLengthComparatorTest/");
    }
    
    @AfterClass
    public static void teardown()
    {
         System.setProperty(LSRFramework.SYS_LSR_WORKSPACE, oldWorkspace);
    }
    
    @Ignore("The original GermaNet API is not Apache licensed.")
    @Test
	public void testGermaNetUsingResourceLoader()
		throws Exception
	{
        LexicalSemanticResource germanet = ResourceFactory.getInstance().get("germanet7", "de");
        
        LexSemResourceComparator comparator = new PathLengthComparator(germanet);

        Set<Entity> entitiesAuto    = germanet.getEntity("Auto", PoS.n);
        Set<Entity> entitiesBagger  = germanet.getEntity("Bagger", PoS.n);
        Set<Entity> entitiesSchnell = germanet.getEntity("schnell", PoS.adj);

        assertEquals(0.0, comparator.getSimilarity(entitiesAuto, entitiesAuto), epsilon);
        assertEquals(1.0, comparator.getSimilarity(entitiesAuto, entitiesBagger), epsilon);
        assertEquals(6.0, comparator.getSimilarity(entitiesAuto, entitiesSchnell), epsilon);
    }

    @Test
	public void testWordNet()
		throws Exception
	{
        Assume.assumeTrue(Runtime.getRuntime().maxMemory() > 1000000000);

        LexicalSemanticResource wordnet = ResourceFactory.getInstance().get("wordnet-default",
                "en");
        
		LexSemResourceComparator comparator = new PathLengthComparator(wordnet);

        Set<Entity> entitiesTree  = wordnet.getEntity("tree", PoS.n);
        Set<Entity> entitiesPlant = wordnet.getEntity("plant", PoS.n);
        Set<Entity> entitiesFast  = wordnet.getEntity("fast", PoS.adj);

        assertEquals(0.0, comparator.getSimilarity(entitiesTree, entitiesTree), epsilon);
        assertEquals(2.0, comparator.getSimilarity(entitiesTree, entitiesPlant), epsilon);
        assertEquals(PathLengthComparator.NOT_FOUND,
                comparator.getSimilarity(entitiesTree, entitiesFast), epsilon);
    }

    @Test
	public void testWikipediaArticle()
		throws Exception
	{
        Assume.assumeTrue(Runtime.getRuntime().maxMemory() > 1000000000);

        LexicalSemanticResource wikipedia = ResourceFactory.getInstance().get("wikipedia", "test");
        
		LexSemResourceComparator comparator = new PathLengthComparator(wikipedia);

        // this are pages
        // we have to find a way to cast the path length between pages to the path length between
		// the corresponding categories
        Set<Entity> entitiesTK3   = wikipedia.getEntity("TK3");
        Set<Entity> entitiesIryna = wikipedia.getEntity("Iryna Gurevych");
        Set<Entity> entitiesUKP   = wikipedia.getEntity("UKP");
        Set<Entity> entitiesNCS   = wikipedia.getEntity("NCS");
        Set<Entity> entitiesNCSl  = wikipedia.getEntity("Net Centric Systems");
        Set<Entity> entitiesNLP   = wikipedia.getEntity("Natural Language Processing for Ambient Intelligence");

        assertTrue("TK3", entitiesTK3.size() > 0);
        assertTrue("Iryna Gurevych", entitiesIryna.size() > 0);
        assertTrue("UKP", entitiesUKP.size() > 0);
        assertTrue("NCS", entitiesNCS.size() > 0);
        assertTrue("Net Centric Systems", entitiesNCSl.size() > 0);
        assertTrue("Natural Language Processing for Ambient Intelligence", entitiesNLP.size() > 0);

        // same page
        assertEquals(0.0, comparator.getSimilarity(entitiesTK3, entitiesTK3), epsilon);

        // different pages
        // pathlength in Edges = 2
        assertEquals(2.0, comparator.getSimilarity(entitiesIryna, entitiesUKP), epsilon);

        // page and redirect
        assertEquals(0.0, comparator.getSimilarity(entitiesNCS, entitiesNCSl), epsilon);

        // one edge / zero nodes distance
        assertEquals(1.0, comparator.getSimilarity(entitiesUKP, entitiesNLP), epsilon);
    }

    @Test
    @Ignore("WiktionaryResource.getParents() is not implemented.")
	public void testWiktionary()
		throws Exception
	{
        LexicalSemanticResource wiktionary = ResourceFactory.getInstance().get("wiktionary", "en");
        
        LexSemResourceComparator comparator = new PathLengthComparator(wiktionary);

        Set<Entity> entitiesFahrzeug = wiktionary.getEntity("Fahrzeug");
        Set<Entity> entitiesAuto     = wiktionary.getEntity("Auto");
        Set<Entity> entitiesGarten   = wiktionary.getEntity("Garten");
        Set<Entity> entitiesEmpty    = new HashSet<Entity>();
        Set<Entity> entitiesUnknown  = new HashSet<Entity>();
        entitiesUnknown.add(new Entity("humbelgrmpfh"));

        assertEquals(0.0, comparator.getSimilarity(entitiesAuto, entitiesAuto), epsilon);
        assertEquals(1.0, comparator.getSimilarity(entitiesAuto, entitiesFahrzeug), epsilon);
        assertEquals(3.0, comparator.getSimilarity(entitiesAuto, entitiesGarten), epsilon);
        assertEquals(PathLengthComparator.NOT_FOUND,
                comparator.getSimilarity(entitiesAuto, entitiesEmpty), epsilon);
        assertEquals(PathLengthComparator.NOT_FOUND,
                comparator.getSimilarity(entitiesAuto, entitiesUnknown), epsilon);

        // test symmetry
        assertEquals(comparator.getSimilarity(entitiesFahrzeug, entitiesAuto), comparator.getSimilarity(entitiesAuto, entitiesFahrzeug), epsilon);
    }
}