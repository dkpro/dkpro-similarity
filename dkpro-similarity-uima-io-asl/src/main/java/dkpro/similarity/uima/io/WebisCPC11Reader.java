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
 * Reader for the Webis Crowd Paraphrase Corpus (Burrows, Potthast,
 * and Stein, 2012)
 * 
 * Burrows S., Potthast M., and Stein B. (2012). Paraphrase Acquisition
 * via Crowdsourcing and Machine Learning. Transactions on Intelligent
 * Systems and Technology, V(January):1-22.
 */
public class WebisCPC11Reader
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
		
		List<File> originals = listFiles(inputDir, "-original.txt", false);		
		for (File original : originals)
		{
			try {
				String textOriginal = FileUtils.readFileToString(original);
				
				String id = original.getName().substring(original.getName().length() - 5, original.getName().length() - 4);
				
				File paraphrase = new File(original.getParent() + "/" + id + "-paraphrase.txt"); 
				String textParaphrase = FileUtils.readFileToString(paraphrase);
					
				CombinationPair pair = new CombinationPair(inputDir.getAbsolutePath());
				pair.setID1(original.getName().substring(0, original.getName().length() - 4));
				pair.setID2(paraphrase.getName().substring(0, paraphrase.getName().length() - 4));
				pair.setText1(textOriginal);
				pair.setText2(textParaphrase);
				
				pairs.add(pair);
			}
			catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
		}

		return pairs;
	}
	
	private List<File> listFiles(File folder, String suffix, boolean recursively)
	{
		List<File> files = new ArrayList<File>();
		
		for (File file : folder.listFiles())
		{
			if (file.isDirectory())
			{
				if (recursively && !file.getName().startsWith(".")) {
                    files.addAll(listFiles(file, suffix, recursively));
                }
			} else {
				if (!file.getName().startsWith(".") && 
					file.getName().endsWith(suffix)) {
                    files.add(file);
                }
			}
		}
		
		return files;
	}

}
