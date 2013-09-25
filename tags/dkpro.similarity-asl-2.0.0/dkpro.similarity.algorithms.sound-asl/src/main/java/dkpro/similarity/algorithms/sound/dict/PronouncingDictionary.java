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
/**
 *
 */
package dkpro.similarity.algorithms.sound.dict;

import java.util.List;

/**
 * An interface for pronouncing dictionaries.
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public interface PronouncingDictionary
{
	/**
	 * Returns the ID of the pronouncing dictionary.
	 *
	 * @return The ID of the pronouncing dictionary.
	 */
	public String getDictionaryId();

	/**
	 * Returns the ID of the pronunciation alphabet (phoneme set) used by the
	 * pronouncing dictionary.
	 *
	 * @return The ID of the pronunciation alphabet (phoneme set) used by the
	 *         pronouncing dictionary.
	 */
	public String getAlphabetId();

	/**
	 * Returns the language used by the pronouncing dictionary.
	 *
	 * @return The language used by the pronouncing dictionary.
	 */
	public String getLanguage();

	/**
	 * Returns a collection of acceptable pronunciations for the given word.
	 *
	 * @param word
	 *            The word for which pronunciations are requested.
	 * @return A collection of acceptable pronunciations for the given word, or
	 *         null if the word is not found in the dictionary.
	 * @throws PronouncingDictionaryException
	 */
	public List<String> getPronunciations(String word)
		throws PronouncingDictionaryException;
}
