package de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.ukp.dkpro.core.frequency.Web1TFileAccessProvider;
import de.tudarmstadt.ukp.dkpro.core.frequency.Web1TProviderBase;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasureBase;

public class Islam2012Measure
	extends TextSimilarityMeasureBase
{
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		String s1 = StringUtils.join(stringList1, " ");
		String s2 = StringUtils.join(stringList2, " ");
		
		try {
			Web1TProviderBase web1t = new Web1TFileAccessProvider(
				new File("/home/danielb/Projekte/DKPro/Resources/web1t/ENGLISH/"),
						1,
						3);
			
			//long f = web1t.getFrequency(s1.split(" ")[0]);
			long a = web1t.getNrOfNgrams(1);
			long b = web1t.getNrOfNgrams(2);
			long f = web1t.getFrequency("the lucky man");
			
			System.out.println(a + " " + b + " " + f);
		}
		catch (IOException e) {
			throw new SimilarityException(e);
		}
		catch (Exception e) {
			throw new SimilarityException(e);
		}
		
		return 0;
	}

}
