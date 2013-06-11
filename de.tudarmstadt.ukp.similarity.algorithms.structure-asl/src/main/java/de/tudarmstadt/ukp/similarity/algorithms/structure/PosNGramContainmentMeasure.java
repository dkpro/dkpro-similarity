package de.tudarmstadt.ukp.similarity.algorithms.structure;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Computes the overlap of part-of-speech n-grams using the
 * containment measure, similarly as for the word n-grams
 * in the {@link WordNGramContainmentMeasure}. 
 */
public class PosNGramContainmentMeasure
	extends PosNGramJaccardMeasure
{
	public PosNGramContainmentMeasure(int n)
	{
		super(n);
	}
	
	@Override
	protected double getNormalizedSimilarity(
			Set<List<String>> suspiciousNGrams, Set<List<String>> originalNGrams)
	{
		// Compare using the Containment measure (Broder, 1997)
		Set<List<String>> commonNGrams = new HashSet<List<String>>();
		commonNGrams.addAll(suspiciousNGrams);
		commonNGrams.retainAll(originalNGrams);
				
		double norm = suspiciousNGrams.size();
		double sim = 0.0;
		
		if (norm > 0.0)
			sim = commonNGrams.size() / norm;
		
		return sim;
	}
}
