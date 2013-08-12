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
package de.tudarmstadt.ukp.similarity.dkpro.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.similarity.dkpro.io.util.CombinationPair;


/**
 * Reader which combines the contents of plain text files. Each file is
 * expected to comprise two lines whereas the first line is considered
 * to be the first text, and the second line the second text.
 */
public class PlainTextCombinationReader
	extends CombinationReader
{
	public static final String PARAM_INPUT_DIR = "InputDir";
	@ConfigurationParameter(name=PARAM_INPUT_DIR, mandatory=true)
	private String inputDirName;
	
	
	@Override
	public List<CombinationPair> getAlignedPairs()
		throws ResourceInitializationException
	{
		List<CombinationPair> pairs = new ArrayList<CombinationPair>();
		
		URL inputUrl = null;
		try {
			inputUrl = ResourceUtils.resolveLocation(inputDirName, this, getUimaContext());
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		File inputDir = new File(inputUrl.getFile());
		
		List<File> files = listFiles(inputDir, ".txt", false);		
		for (File file : files)
		{
			try {
				String s = FileUtils.readFileToString(file);
				
				String id = file.getName().substring(file.getName().length() - 5, file.getName().length() - 4);
				
				CombinationPair pair = new CombinationPair(inputDir.getAbsolutePath());
				pair.setID1(file.getName().substring(0, file.getName().length() - 4));
				pair.setID2(file.getName().substring(0, file.getName().length() - 4));
				pair.setText1(s.split("\n")[0]);
				pair.setText2(s.split("\n")[1]);
				
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
		
		String s = folder.getAbsolutePath();
		
		for (File file : folder.listFiles())
		{
			if (file.isDirectory())
			{
				if (recursively && !file.getName().startsWith("."))
					files.addAll(listFiles(file, suffix, recursively));
			} else {
				if (!file.getName().startsWith(".") && 
					file.getName().endsWith(suffix))
					files.add(file);
			}
		}
		
		return files;
	}

}
