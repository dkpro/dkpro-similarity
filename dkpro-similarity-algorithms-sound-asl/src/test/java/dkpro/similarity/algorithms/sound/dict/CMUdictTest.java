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
package dkpro.similarity.algorithms.sound.dict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import dkpro.similarity.algorithms.sound.dict.CMUdict;
import dkpro.similarity.algorithms.sound.dict.PronouncingDictionaryException;

/**
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class CMUdictTest
{

	@Test
	public void cmuTest() throws IOException, PronouncingDictionaryException
	{
		CMUdict dict = new CMUdict();
		List<String> result;

		result = dict.getPronunciations("ab");
		assertEquals(2, result.size());
		assertEquals("AE1 B", result.get(0));
		assertEquals("EY1 B IY1", result.get(1));

		result = dict.getPronunciations("foobarbaz");
		assertNull(result);
}

}
