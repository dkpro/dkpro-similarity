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
package dkpro.similarity.example;

import java.util.Arrays;
import java.util.List;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure;

public class WithoutDKPro
{
	public static void main(String[] args)
		throws SimilarityException
	{
		// These are the two input documents
		String[] tokens1 = "The quick brown fox jumps over the lazy dog".split(" ");     
		String[] tokens2 = "The quick brown dog jumps over the lazy fox".split(" ");
		
		// Compute a trigram relatedness		
		TextSimilarityMeasure measure = new WordNGramContainmentMeasure(3);

		double score = measure.getSimilarity(tokens1, tokens2);

		System.out.println("Trigram similarity: " + score);
		
		// Compute ESA relatedness on Wiktionary 		
		/*VectorComparator esa = new VectorComparator(
				new VectorIndexReader(new File("[PATH TO DKPRO_HOME]/ESA/VectorIndexes/wiktionary_en")));
		esa.setInnerProduct(InnerVectorProduct.COSINE);
		esa.setNormalization(VectorNorm.L2);
		
		score = esa.getRelatedness(lemmas1, lemmas2);
		
		System.out.println("ESA relatedness: " + score);*/
	}
	
	public static List<String> getTokens(String text)
	{
		// Simple whitespace tokenization
		return Arrays.asList(text.split(" "));
	}

}
