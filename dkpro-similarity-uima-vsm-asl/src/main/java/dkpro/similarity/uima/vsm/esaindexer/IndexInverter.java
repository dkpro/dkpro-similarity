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

import static org.apache.commons.io.FileUtils.deleteQuietly;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import no.uib.cipr.matrix.Vector;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.vsm.store.LuceneVectorReader;
import dkpro.similarity.algorithms.vsm.store.convert.ConvertLuceneToVectorIndex;
import dkpro.similarity.algorithms.vsm.store.vectorindex.VectorIndexWriter;
import dkpro.similarity.algorithms.vsm.util.ProgressMeter;

/**
 * Creates an inverted index for ESA. Based on
 * {@link ConvertLuceneToVectorIndex}.
 * 
 * @author Mateusz Parzonka
 * 
 */
public class IndexInverter {

	private final File luceneIndexDir;
	private final File invertedIndexDir;

	private int minDocumentFrequency = 1;

	public IndexInverter() {
		super();
		this.luceneIndexDir = new File("target/lucene");
		this.invertedIndexDir = new File("target/esa");
	}

	public IndexInverter(File luceneIndexDir, File invertedIndexDir) {
		super();
		this.luceneIndexDir = luceneIndexDir;
		this.invertedIndexDir = invertedIndexDir;
	}

	protected void configureLuceneVectorReader(LuceneVectorReader luceneVectorReader) {
		// standard configuration
	}

	public void createInvertedIndex() throws CorruptIndexException, IOException, SimilarityException {

		deleteQuietly(invertedIndexDir);
		if (!invertedIndexDir.mkdirs()) {
		    throw new IOException("Cannot create folder: " + invertedIndexDir);
		}

		final IndexReader reader = IndexReader.open(FSDirectory.open(luceneIndexDir));
		final TermEnum termEnum = reader.terms();
		final Set<String> terms = new HashSet<String>();

		int totalTerms = 0;
		while (termEnum.next()) {
			final String term = termEnum.term().text();
			final int termDocFreq = termEnum.docFreq();
			if (minDocumentFrequency <= termDocFreq) {
				terms.add(term);
			}
			totalTerms++;
		}
		reader.close();

		System.out.println("Using " + terms.size() + " terms out of " + totalTerms);
		System.out.println("Input Lucene index: " + luceneIndexDir);
		final LuceneVectorReader luceneVectorReader = new LuceneVectorReader(luceneIndexDir);
		configureLuceneVectorReader(luceneVectorReader);
		
		System.out.println("Output inverted index: " + invertedIndexDir);
		final VectorIndexWriter vectorIndexWriter = new VectorIndexWriter(invertedIndexDir,
				luceneVectorReader.getConceptCount());

		final ProgressMeter progressMeter = new ProgressMeter(terms.size());
		for (String term : terms) {
			final Vector vector = luceneVectorReader.getVector(term);
			vectorIndexWriter.put(term, vector);
			progressMeter.next();
			System.out.println("[" + term + "] " + progressMeter);
		}
		vectorIndexWriter.close();
	}

	public int getMinDocumentFrequency() {
		return minDocumentFrequency;
	}

	/**
	 * Terms which appear in less then the given amount of documents are
	 * ignored.
	 * 
	 * @param minDocumentFrequency
	 *            must be greater than 0
	 */
	public void setMinDocumentFrequency(int minDocumentFrequency) {
		if (minDocumentFrequency < 0) {
			throw new IllegalArgumentException("minDocumentFrequency must be in [0,)");
		}
		this.minDocumentFrequency = minDocumentFrequency;
	}
}