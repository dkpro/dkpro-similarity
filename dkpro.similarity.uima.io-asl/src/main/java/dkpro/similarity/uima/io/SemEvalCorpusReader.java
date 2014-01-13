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
package dkpro.similarity.uima.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import dkpro.similarity.uima.io.util.CombinationPair;

/**
 * Reader for the datasets used in the Semantic Textual Similarity (STS)
 * Task at SemEval-2012 (Agirre et al., 2012) and <pre>*</pre>SEM-2013
 * (Agirre et al., 2013). 
 * 
 * Agirre E., Cer D., Diab M., and Gonzalez-Agirre A. (2012). SemEval-2012
 * Task 6: A Pilot on Semantic Textual Similarity. In Proceedings of the
 * 6th International Workshop on Semantic Evaluation, in conjunction with
 * the 1st Joint Conference on Lexical and Computational Semantics, pages
 * 385-393, Montreal, Canada.
 * <a href="http://aclweb.org/anthology-new/S/S12/S12-1051.pdf">(pdf)</a>
 * 
 * Agirre E., Cer D., Diab M., Gonzalez-Agirre A., and Guo W. (2013).
 * <pre>*</pre>SEM 2013 Shared Task: Semantic Textual Similarity. In
 * Proceedings of the 2nd Joint Conference on Lexical and Computational
 * Semantics, pages 32-43, Atlanta, GA, USA.
 * <a href="http://aclweb.org/anthology-new/S/S13/S13-1004.pdf">(pdf)</a>
 */
public class SemEvalCorpusReader
	extends CombinationReader
{
	
	public static final String PARAM_INPUT_FILE = "InputFile";
	@ConfigurationParameter(name=PARAM_INPUT_FILE, mandatory=true)
	private String inputFile;
	
	
	protected String getInputFile()
    {
        return inputFile;
    }

    @Override
	public List<CombinationPair> getAlignedPairs()
		throws ResourceInitializationException
	{
		List<CombinationPair> pairs = new ArrayList<CombinationPair>();
			
		List<String> lines = new ArrayList<String>();
		InputStream is = null;
		BufferedReader br = null;
		URL url;
		try {
			url = ResourceUtils.resolveLocation(inputFile, this, this.getUimaContext());
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				lines.add(strLine);
			}
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(is);
		}
		
		for (int i = 0; i < lines.size(); i++)
		{
			String line = lines.get(i);
			String[] linesplit = line.split("\t");
			
			String text1 = linesplit[0];
			String text2 = linesplit[1];
				
			CombinationPair pair = new CombinationPair(url.toString());
			pair.setID1(new Integer(i + 1).toString());
			pair.setID2(new Integer(i + 1).toString());
			pair.setText1(text1);
			pair.setText2(text2);
			
			pairs.add(pair);
		}

		return pairs;
	}
}
