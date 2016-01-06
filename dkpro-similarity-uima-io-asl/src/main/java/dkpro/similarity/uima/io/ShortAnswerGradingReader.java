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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import dkpro.similarity.uima.api.type.GoldTextSimilarityScore;
import dkpro.similarity.uima.io.util.CombinationPair;

/**
 * Reader for the dataset by Mohler and Mihalcea (2009).
 * 
 * Michael Mohler and Rada Mihalcea. 2009. Text-to-text Semantic
 * Similarity for Automatic Short Answer Grading. In Proceedings
 * of the European Chapter of the ACL, pages 567–575.
 */
public class ShortAnswerGradingReader
	extends CombinationReader
{
	public enum ShortAnswerGradingDocumentID
	{
		hierarchical,
		sequential
	}
	
	public static final String PARAM_INPUT_DIR = "InputDir";
	@ConfigurationParameter(name=PARAM_INPUT_DIR, mandatory=true)
	private File inputDir;
	
	public static final String PARAM_DOCUMENT_IDS = "DocumentIds";
	@ConfigurationParameter(name=PARAM_DOCUMENT_IDS, mandatory=false, defaultValue="sequential")
	private ShortAnswerGradingDocumentID documentIDs;
	
	public static final String PARAM_ENCODING = "Encoding";
	@ConfigurationParameter(name=PARAM_ENCODING, mandatory=false, defaultValue="UTF-8")
	private String encoding;

    private Map<String, Double> goldScoreMap = new HashMap<String, Double>();

	@Override
	public List<CombinationPair> getAlignedPairs()
		throws ResourceInitializationException
	{
		List<CombinationPair> pairs = new ArrayList<CombinationPair>();
		
		try
		{
			int seq_no = 1;
			
			for(int assignment = 1; assignment <= 3; assignment++)
			{
				// Process assignment
				File file = new File(inputDir.getAbsolutePath() + "/" + assignment + ".txt");
				
				List<String> lines = FileUtils.readLines(file, encoding);
				
				int questionIndex = 0;
				String refAnswer = "";
				
				for (int i = 0; i < lines.size(); i++)
				{
					String line = lines.get(i);
					
					if (line.startsWith("#"))
					{
						// A new question starts here
						i++;
						line = lines.get(i);
						line = line.substring(12);			// Skip "\t\tQuestion: " prefix
						questionIndex++;

						i++;									// Advance to perfect answer line
						refAnswer = lines.get(i).substring(10);	// Skip "\t\tAnswer: " prefix
						i++;									// Skip empty line
					}
					else if (!line.equals(""))
					{
						// Combine refAnswer with the answer on this line
						
						String[] lineSplit = line.split("\t");
						
						String answer = lineSplit[2];
						int studentID = Integer.parseInt(lineSplit[1].substring(1, lineSplit[1].length() - 1));		// Ignore surrounding braces
						
						String id1, id2;
						
						switch (documentIDs)
						{
							case sequential:
								id1 = Integer.valueOf(seq_no).toString();
								id2 = Integer.valueOf(seq_no).toString();
								break;
							default:
								id1 = assignment + ":" + questionIndex;
								id2 = assignment + ":" + questionIndex + ":" + studentID;
						}
						
						seq_no++;
						
						// Add to combination pairs
						CombinationPair pair = new CombinationPair(inputDir.toString());
						pair.setID1(id1);
						pair.setID2(id2);
						pair.setText1(refAnswer);
						pair.setText2(answer);
						
						getLogger().debug(pair.getID2() + " # " + refAnswer + " # " + answer);
						
						pairs.add(pair);
						
						goldScoreMap.put(id2, Double.parseDouble(lineSplit[0]));
					}
				}			
			}
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		
		
		
		return pairs;
	}

    @Override
    public void getNext(CAS cas)
        throws IOException, CollectionException
    {
        super.getNext(cas);

        try {
            JCas jcas = cas.getJCas();
            JCas view2 = jcas.getView(VIEW_2);
            DocumentMetaData md2 = DocumentMetaData.get(view2);
            
            GoldTextSimilarityScore goldScore = new GoldTextSimilarityScore(jcas);
            goldScore.setScore(goldScoreMap.get(md2.getDocumentId()));
            goldScore.addToIndexes();
        }
        catch (CASException e) {
            throw new CollectionException(e);
        }
    }
}
