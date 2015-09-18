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
package dkpro.similarity.algorithms.vsm.store.convert;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.lang.StringUtils.isNumericSpace;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.uib.cipr.matrix.Vector;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;

import dkpro.similarity.algorithms.vsm.store.LuceneVectorReader;
import dkpro.similarity.algorithms.vsm.store.VectorReader;
import dkpro.similarity.algorithms.vsm.store.vectorindex.VectorIndexWriter;
import dkpro.similarity.algorithms.vsm.util.ProgressMeter;

/**
 * Convert an index created using {@code EsaIndex} to a ESA Vector Cache index which intelligently
 * compresses and stores vectors using {@link VectorIndexWriter}.
 *
 * @author Richard Eckart de Castilho
 */
public class ConvertLuceneToVectorIndex
{
	private static Matcher monetaryPattern = Pattern.compile("^[$€]\\s*[0-9,.]+$").matcher("");
	private static Matcher cardinalNumber = Pattern.compile("^#[0-9]+$").matcher("");

	private static boolean isMonetary(String aTerm)
	{
		monetaryPattern.reset(aTerm);
		return monetaryPattern.matches();
	}

	private static boolean isCardinal(String aTerm)
	{
		cardinalNumber.reset(aTerm);
		return cardinalNumber.matches();
	}

	public static void main(String[] args)
		throws Exception
	{
		File inputPath = new File(args[0]);
		File outputPath = new File(args[1]);

		deleteQuietly(outputPath);
		outputPath.mkdirs();

		boolean ignoreNumerics = true;
		boolean ignoreCardinal = true;
		boolean ignoreMonetary = true;
		int minTermLength = 3;
		int minDocFreq = 5;

		System.out.println("Quality criteria");
		System.out.println("Minimum term length            : "+minTermLength);
		System.out.println("Minimum document frequency     : "+minDocFreq);
		System.out.println("Ignore numeric tokens          : "+ignoreNumerics);
		System.out.println("Ignore cardinal numeric tokens : "+ignoreNumerics);
		System.out.println("Ignore money values            : "+ignoreMonetary);

		System.out.print("Fetching terms list... ");

		IndexReader reader = IndexReader.open(FSDirectory.open(inputPath));
		TermEnum termEnum = reader.terms();
		Set<String> terms = new HashSet<String>();
		int ignoredTerms = 0;
		while (termEnum.next()) {
			String term = termEnum.term().text();
			if (
					((minTermLength > 0) && (term.length() < minTermLength)) ||
					(ignoreCardinal && isCardinal(term)) ||
					(ignoreMonetary && isMonetary(term)) ||
					(ignoreNumerics && isNumericSpace(term)) ||
					((minDocFreq > 0) && (termEnum.docFreq() < minDocFreq))
			) {
				ignoredTerms++;
				continue;
			}

			terms.add(term);
		}
		reader.close();

		System.out.println(terms.size()+" terms found. "+ignoredTerms+" terms ignored.");


		System.out.println("Opening source ESA index " + inputPath);
		VectorReader source = new LuceneVectorReader(inputPath);
		System.out.println("Opening destination ESA index " + inputPath);
		VectorIndexWriter esaWriter = new VectorIndexWriter(outputPath, source.getConceptCount());

		ProgressMeter p = new ProgressMeter(terms.size());
		for (String term : terms) {
			Vector vector = source.getVector(term);
			esaWriter.put(term, vector);

			p.next();
			System.out.println("[" + term + "] "+p);
		}

		esaWriter.close();
	}
}
