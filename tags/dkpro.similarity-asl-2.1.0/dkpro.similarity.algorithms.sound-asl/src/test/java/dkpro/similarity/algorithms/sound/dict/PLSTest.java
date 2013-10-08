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

import java.util.List;

import org.dom4j.DocumentException;
import org.junit.Test;

import dkpro.similarity.algorithms.sound.dict.PLS;
import dkpro.similarity.algorithms.sound.dict.PronouncingDictionaryException;

public class PLSTest
{

	@Test
	public void testPLS() throws DocumentException, PronouncingDictionaryException
	{
		PLS dict = new PLS("src/test/resources/pls-sample.xml");
		List<String> result;

		assertEquals("ipa", dict.getAlphabetId());
		assertEquals("de-DE", dict.getLanguage());
		assertEquals("A sample PLS German dictionary", dict.getDictionaryId());

		result = dict.getPronunciations("10.");
		assertEquals(5, result.size());
		assertEquals("t͡seːntən", result.get(4));

		List<String> rauResult = dict.getPronunciations("rau");
		List<String> rauhResult = dict.getPronunciations("rauh");
		assertEquals(rauResult, rauhResult);
		assertEquals(1, rauResult.size());
	}

}
