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
