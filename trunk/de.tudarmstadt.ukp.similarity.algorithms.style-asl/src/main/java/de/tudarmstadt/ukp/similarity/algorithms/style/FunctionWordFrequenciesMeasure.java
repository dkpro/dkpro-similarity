package de.tudarmstadt.ukp.similarity.algorithms.style;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;

public class FunctionWordFrequenciesMeasure
	extends JCasTextSimilarityMeasureBase
{
	private List<String> functionWords;
	
	public FunctionWordFrequenciesMeasure()
		throws IOException
	{
	    init("classpath:/functionWords/en/function-words-mosteller-wallace.txt");
	}	
	
    public FunctionWordFrequenciesMeasure(String functionWordListLocation)
        throws IOException
    {
        init(functionWordListLocation);
    }   

    private void init(String functionWordListLocation) throws IOException {
        functionWords = new ArrayList<String>();
        InputStream is = null;
        try {
            URL url = ResourceUtils.resolveLocation(functionWordListLocation, this, null);
            is = url.openStream();
            String content = IOUtils.toString(is, "UTF-8");
            for (String line : Arrays.asList(content.split("\n"))) {
                if (line.length() > 0)
                    functionWords.add(line);
            }
        }
        finally{
            IOUtils.closeQuietly(is);
        }
    }
    
    @Override
	public double getSimilarity(JCas jcas1, JCas jcas2)
		throws SimilarityException
	{
		double[] v1 = getDocumentVector(jcas1);
		double[] v2 = getDocumentVector(jcas2);
		
		PearsonsCorrelation p = new PearsonsCorrelation();
		return p.correlation(v1, v2);
	}
	
	private double[] getDocumentVector(JCas jcas)
	{
		double[] v = new double[functionWords.size()];
		
		Collection<Token> tokens = JCasUtil.select(jcas, Token.class);
		Map<String,Integer> freq = new HashMap<String,Integer>();
		
		for (Token token : tokens)
		{
			String str = token.getCoveredText().toLowerCase();
			
			if (freq.containsKey(str)) {
				int count = freq.get(str);
				count++;
				freq.put(str, count);
			} else {
				freq.put(str, 1);
			}
		}
		
		for (int i = 0; i < functionWords.size(); i++)
		{
			String functionWord = functionWords.get(i);
			
			if (freq.containsKey(functionWord))
				v[i] = new Double(freq.get(functionWord));
			else
				v[i] = 0.0;
		}
		
		return v;
	}
}
