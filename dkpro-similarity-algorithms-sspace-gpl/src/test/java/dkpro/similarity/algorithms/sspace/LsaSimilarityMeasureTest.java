package dkpro.similarity.algorithms.sspace;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;

public class LsaSimilarityMeasureTest {

	@Test
	public void lsaSimilarityTest() 
			throws IOException, SimilarityException
	{
		String[] text1 = "This is a test .".split(" ");
		String[] text2 = "This is an example .".split(" ");
	
		TextSimilarityMeasure lsa = new LsaSimilarityMeasure(new File("src/test/resources/model/test.sspace"));
		
		assertEquals(1.0, lsa.getSimilarity(text1, text1), 0.00001);
		assertEquals(0.837171, lsa.getSimilarity(text1, text2), 0.00001);

	}
	
}
