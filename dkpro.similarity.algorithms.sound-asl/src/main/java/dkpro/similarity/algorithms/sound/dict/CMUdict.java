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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

/**
 * An interface to the <a
 * href="http://www.speech.cs.cmu.edu/cgi-bin/cmudict">CMU Pronouncing
 * Dictionary</a>.
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class CMUdict
	implements PronouncingDictionary
{
	private final MultiMap dict;
	private final static String DICT_PATH = "src/main/resources/cmudict/cmudict.0.7a";

	public CMUdict() throws IOException, PronouncingDictionaryException {
		this(new InputStreamReader(new FileInputStream(DICT_PATH), "UTF-8"));
	}

	public CMUdict(Reader reader)
		throws IOException, PronouncingDictionaryException
	{
		String line;
		int lineNumber = 0;
		final Pattern cmuPattern = Pattern.compile("(.+?)(\\(\\d+\\))?  (.*)");
		final BufferedReader bufRead = new BufferedReader(reader);
		dict = new MultiValueMap();

		while ((line = bufRead.readLine()) != null) {

			lineNumber++;

			if (line.length() < 3) {
				throw new PronouncingDictionaryException(
						"syntax error on line " + lineNumber);
			}

			// Skip comment lines
			if (line.substring(0, 3).equals(";;;")) {
				continue;
			}

			Matcher m = cmuPattern.matcher(line);

			if (!m.find()) {
				throw new PronouncingDictionaryException(
						"syntax error on line " + lineNumber);
			}

			dict.put(m.group(1), m.group(3));
		}

		bufRead.close();
	}

	@Override
	public String getDictionaryId()
	{
		return "CMUdict 0.7a";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPronunciations(String word)
		throws PronouncingDictionaryException
	{
		return (List<String>) dict.get(word.toUpperCase());
	}

	@Override
	public String getAlphabetId()
	{
		return "x-arpabet";
	}

	@Override
	public String getLanguage()
	{
		return "en-US";
	}

}
