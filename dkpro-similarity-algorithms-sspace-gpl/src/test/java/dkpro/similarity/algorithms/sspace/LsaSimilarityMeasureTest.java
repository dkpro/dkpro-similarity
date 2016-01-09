/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package dkpro.similarity.algorithms.sspace;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;

public class LsaSimilarityMeasureTest {

	@Test
	public void lsaSimilarityTest() 
			throws IOException, SimilarityException
	{
		String[] text1 = "This is a test .".split(" ");
		String[] text2 = "This is an example .".split(" ");
	
		TextSimilarityMeasure lsa = new LsaSimilarityMeasure(new File("src/test/resources/model/test.sspace"));
		
		assertEquals(1.0, lsa.getSimilarity(text1, text1), 0.00001);
		assertEquals(0.837171, lsa.getSimilarity(text1, text2), 0.00001);

	}
	
}
