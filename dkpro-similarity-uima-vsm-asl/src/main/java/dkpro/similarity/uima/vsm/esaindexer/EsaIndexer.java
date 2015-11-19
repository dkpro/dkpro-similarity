/*******************************************************************************
 * Copyright 2013 Mateusz Parzonka
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
package dkpro.similarity.uima.vsm.esaindexer;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.io.jwpl.WikipediaReaderBase;
import de.tudarmstadt.ukp.dkpro.core.snowball.SnowballStemmer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;

/**
 * Indexes Wikipedia and creates an inverted index to be used with ESA. 
 * 
 * @author Mateusz Parzonka
 *
 */
public class EsaIndexer {
	
	private final static String luceneIndexPath = "target/lucene";
	private final static String esaIndexPath = "target/esa";
	
	public static void main(String[] args)
		throws Exception
	{
		createLuceneWikipediaIndex("de");
		createInvertedIndex();
	}

	/**
	 * Creates a Lucene index from Wikipedia based on lower cased stems with length >=3 containing only characters.
	 * 
	 * @throws UIMAException
	 * @throws IOException
	 */
	private static void createLuceneWikipediaIndex(String language)
		throws UIMAException, IOException
	{
		CollectionReader reader = createReader(
				ExtendedWikipediaArticleReader.class,
				WikipediaReaderBase.PARAM_HOST, "YOUR_HOST",
				WikipediaReaderBase.PARAM_DB, "YOUR_DB",
				WikipediaReaderBase.PARAM_USER, "YOUR_USERNAME",
				WikipediaReaderBase.PARAM_PASSWORD, "YOUR_PASSWORD",
				WikipediaReaderBase.PARAM_LANGUAGE, Language.german);
		
		AnalysisEngine segmenter = createEngine(
				BreakIteratorSegmenter.class,
				BreakIteratorSegmenter.PARAM_LANGUAGE, language);
		
		AnalysisEngine stemmer = createEngine(
				SnowballStemmer.class,
				SnowballStemmer.PARAM_LANGUAGE, language,
				SnowballStemmer.PARAM_LOWER_CASE, true);
		
		AnalysisEngine indexTermGenerator = createEngine(
				LuceneIndexer.class,
				LuceneIndexer.PARAM_INDEX_PATH, luceneIndexPath,
				LuceneIndexer.PARAM_MIN_TERMS_PER_DOCUMENT, 50);

		SimplePipeline.runPipeline(reader, segmenter, stemmer, indexTermGenerator);
		
	}
	
	/**
	 * Creates an inverted index for ESA
	 * 
	 * @throws Exception
	 */
	private static void createInvertedIndex()
		throws Exception
	{
		IndexInverter indexInverter = new IndexInverter(new File(luceneIndexPath), new File(esaIndexPath));
		indexInverter.setMinDocumentFrequency(1);
		indexInverter.createInvertedIndex();
	}
}