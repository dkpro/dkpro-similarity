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
package dkpro.similarity.algorithms.vsm.store;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * Vector reader using an Lucene index.
 *
 * @author Richard Eckart de Castilho
 */
public class LuceneVectorReader
	extends IndexedDocumentsVectorReaderBase
{
	public static final String FIELD_NAME = "token";

	private final File path;

	private IndexReader reader;
	private Map<Integer, Integer> documentVocabularySizes;
	private int indexVocabularySize = -1;
	private String termBuffer;
	private Map<Integer, Integer> termFrequencies;

	public LuceneVectorReader(File aPath)
	{
		path = aPath;
	}

	@Override
	public String getId()
	{
		return path.getAbsolutePath();
	}

	@Override
	public int getDocumentCount()
		throws SimilarityException
	{
		return getReader().numDocs();
	}

	@Override
	public int getDocumentFrequency(String aTerm)
		throws SimilarityException
	{
		try {
			Term term = new Term(FIELD_NAME, aTerm);
			return getReader().docFreq(term);
		}
		catch (IOException e) {
			throw new SimilarityException(e);
		}
	}

	@Override
	public Set<Integer> getDocuments(String aTerm)
		throws SimilarityException
	{
		return getTermFrequenies(aTerm).keySet();
	}

	@Override
	public int getTermFrequency(int aDocId, String aTerm)
		throws SimilarityException
	{
		Integer count = getTermFrequenies(aTerm).get(aDocId);
		return (count == null) ? 0 : count;
	}

	@Override
	public int getIndexVocabularySize()
		throws SimilarityException
	{
		try {
			if (indexVocabularySize == -1) {
				int iTerms = 0;
				TermEnum te = reader.terms();
				while (te.next()) {
					iTerms++;
				}
				indexVocabularySize = iTerms;
			}
			return indexVocabularySize;
		}
		catch (IOException e) {
			throw new SimilarityException(e);
		}

	}

	@Override
	public int getDocumentLength(int aDocId)
		throws SimilarityException
	{
		return getDocumentVocabularySizes().get(aDocId);
	}

	@Override
	public void close()
	{
		if (reader != null) {
			try {
				reader.close();
			}
			catch (IOException e) {
				// Ignore
			}
		}
	}

	private Map<Integer, Integer> getDocumentVocabularySizes()
		throws SimilarityException
	{
		try {
			if (documentVocabularySizes == null) {
				documentVocabularySizes = new HashMap<Integer, Integer>();

				for (int documentID = 0; documentID < reader.maxDoc(); documentID++) {

					if (!reader.isDeleted(documentID)) {
						TermFreqVector tfv = reader.getTermFreqVector(documentID, FIELD_NAME);
						if (tfv == null) {
							continue;
						}

						documentVocabularySizes.put(documentID, tfv.size());
					}
				}
			}

			return documentVocabularySizes;
		}
		catch (IOException e) {
			throw new SimilarityException(e);
		}
	}

	private Map<Integer, Integer> getTermFrequenies(String aTerm) throws SimilarityException
	{
		// Return buffered IDs.
		if (aTerm.equals(termBuffer) && termFrequencies != null) {
			return termFrequencies;
		}

		// Fetch the IDs from the index and at the same time cache the term frequencies.
		TermDocs termDocs = null;
		try {
			Term term = new Term(FIELD_NAME, aTerm);
			termDocs = getReader().termDocs(term);

			termBuffer = aTerm;
			termFrequencies = new HashMap<Integer, Integer>();

			// the semantics of lucene next() is different from java.util.collections, initial
			// next() is required (do not transform into do while loop)
			while (termDocs.next()) {
				termFrequencies.put(termDocs.doc(), termDocs.freq());
			}

			return termFrequencies;
		}
		catch (IOException e) {
			termBuffer = null;
			termFrequencies = null;
			throw new SimilarityException(e);
		}
		finally {
			if (termDocs != null) {
				try {
					termDocs.close();
				}
				catch (IOException e) {
					return null;
				}
			}
		}
	}

	private IndexReader getReader()
		throws SimilarityException
	{
		try {
			if (reader == null) {
				// get the location of the index files
				// second parameter false means not to erase existing files
				Directory dir = FSDirectory.open(path);
				reader = IndexReader.open(dir, true);
			}
			return reader;
		}
		catch (IOException e) {
			throw new SimilarityException(e);
		}
	}
}
