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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import dkpro.similarity.uima.io.util.CombinationPair;


/**
 * Reader for the Wikipedia Rewrite Corpus (Clough and Stevenson, 2011)
 * 
 * Clough P. and Stevenson M. (2011). Developing a Corpus of Plagiarised
 * Short Answers. Language Resources and Evaluation: Special Issue on
 * Plagiarism and Authorship Analysis, 45(1):5-24.
 * <a href="http://link.springer.com/article/10.1007/s10579-009-9112-1">(pdf)</a>
 */
public class CloughCorpusReader
	extends CombinationReader
{
	public static final String PARAM_INPUT_DIR = "InputDir";
	@ConfigurationParameter(name=PARAM_INPUT_DIR, mandatory=true)
	private File inputDir;
	
	
	@Override
	public List<CombinationPair> getAlignedPairs()
		throws ResourceInitializationException
	{
		List<CombinationPair> pairs = new ArrayList<CombinationPair>();
		
		List<File> answerFiles = listFiles(inputDir, "g", false);		
		for (File answerFile : answerFiles)
		{
			try {
				String answer = FileUtils.readFileToString(answerFile);
				
				String task = answerFile.getName().substring(answerFile.getName().length() - 5, answerFile.getName().length() - 4);
				
				File originalFile = new File(answerFile.getParent() + "/orig_task" + task + ".txt"); 
				String original = FileUtils.readFileToString(originalFile);
					
				CombinationPair pair = new CombinationPair(inputDir.getAbsolutePath());
				pair.setID1(answerFile.getName().substring(0, answerFile.getName().length() - 4));
				pair.setID2(originalFile.getName().substring(0, originalFile.getName().length() - 4));
				pair.setText1(answer);
				pair.setText2(original);
				
				pairs.add(pair);
			}
			catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
		}

		return pairs;
	}
	
	private List<File> listFiles(File folder, String prefix, boolean recursively)
	{
		List<File> files = new ArrayList<File>();
		
		for (File file : folder.listFiles())
		{
			if (file.isDirectory())
			{
				if (recursively && !file.getName().startsWith(".")) {
                    files.addAll(listFiles(file, prefix, recursively));
                }
			} else {
				if (!file.getName().startsWith(".") && 
					file.getName().startsWith(prefix)) {
                    files.add(file);
                }
			}
		}
		
		return files;
	}

}
