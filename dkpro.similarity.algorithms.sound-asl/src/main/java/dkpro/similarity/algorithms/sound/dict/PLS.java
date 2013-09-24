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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

/**
 * An interface to pronouncing dictionaries following the W3C's <a
 * href="http://www.w3.org/TR/pronunciation-lexicon/">Pronunciation Lexicon
 * Specification</a>.  Note that &lt;alias&gt; elements are not yet supported.
 *
 * @author Tristan Miller <miller@ukp.informatik.tu-darmstadt.de>
 *
 */
public class PLS
	implements PronouncingDictionary
{

	private final String dictionaryId;
	private final String alphabetId;
	private final String dictionaryLanguage;
	private final MultiMap dict;

	/**
	 *
	 * @param dictionaryFilename
	 *            The filename of the PLS dictionary to read in.
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	public PLS(String dictionaryFilename) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(dictionaryFilename);
		dict = new MultiValueMap();

		// Get dictionary alphabet and language
		Element lexicon = document.getRootElement();
		alphabetId = lexicon.attributeValue("alphabet");
		dictionaryLanguage = lexicon.attributeValue("lang");

		// Extract dictionary name from metadata
		Map<String, String> namespaceMap = new HashMap<String, String>();
		namespaceMap.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		namespaceMap.put("dc", "http://purl.org/dc/elements/1.1/");
		XPath xpath = document.createXPath("//rdf:Description");
		xpath.setNamespaceURIs(namespaceMap);
		Node node = xpath.selectSingleNode(lexicon);
		if (node != null) {
			dictionaryId = node.valueOf("@dc:title");
		}
		else {
			dictionaryId = "";
		}

		for (Iterator<Element> lexemeIterator = lexicon
				.elementIterator("lexeme"); lexemeIterator.hasNext();) {
			Element lexeme = lexemeIterator.next();
			List<Element> graphemes = lexeme.selectNodes("grapheme");
			List<Element> phonemes = lexeme.selectNodes("phoneme");
			for (Element grapheme : graphemes) {
				for (Element phoneme : phonemes) {
					dict.put(grapheme.getText(), phoneme.getText());
				}
			}
		}
	}

	@Override
	public String getDictionaryId()
	{
		return dictionaryId;
	}

	@Override
	public String getAlphabetId()
	{
		return alphabetId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPronunciations(String word)
		throws PronouncingDictionaryException
	{
		return (List<String>) dict.get(word);
	}

	@Override
	public String getLanguage()
	{
		return dictionaryLanguage;
	}

}
