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
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory;
import dkpro.similarity.algorithms.lsr.LexSemResourceComparator;
import dkpro.similarity.algorithms.lsr.path.LeacockChodorowComparator;

public class LeacockChodorowComparatorTest
{
	private static final double epsilon = 0.0001;

	
	@Test
	public void testLC98WikipediaCategory()
		throws Exception
	{

	    Assume.assumeTrue(Runtime.getRuntime().maxMemory() > 1000000000);
	    
		LexicalSemanticResource wikiResource = ResourceFactory.getInstance().get(
				"wikipedia_category", "test");

		LexSemResourceComparator comparator = new LeacockChodorowComparator(wikiResource);

		Set<Entity> entitiesAqua = wikiResource.getEntity("AQUA");
		Set<Entity> entitiesSir = wikiResource.getEntity("SIR");
		Set<Entity> entitiesUKP = wikiResource.getEntity("UKP");

		assertTrue("AQUA", entitiesAqua.size() > 0);
		assertTrue("SIR", entitiesSir.size() > 0);
		assertTrue("UKP", entitiesUKP.size() > 0);

		// same page
		assertEquals(2.0794, comparator.getSimilarity(entitiesAqua, entitiesAqua), epsilon);

		// different pages
		// pathlength in Edges = 2
		assertEquals(-Math.log(0.375), comparator.getSimilarity(entitiesAqua, entitiesSir),
				epsilon);
	}
}