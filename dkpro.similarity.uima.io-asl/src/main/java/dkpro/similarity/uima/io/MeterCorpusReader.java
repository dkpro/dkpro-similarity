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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import dkpro.similarity.uima.io.util.CombinationPair;


/**
 * Reader for the METER Corpus (Gaizauskas et al., 2001)
 * 
 * Gaizauskas R., Foster J., Wilks Y., Arundel J., Clough P. and Piao S.
 * (2001). The METER Corpus: A corpus for analysing journalistic text reuse.
 * In Proceedings of the Corpus Linguistics 2001 Conference, pages 214-233,
 * Bailrigg, UK.
 * <a href="https://wiki.dcs.shef.ac.uk/wiki/pub/Darwin2008/2008T6_Literature/cl2001.pdf">(pdf)</a>
 */
public class MeterCorpusReader
	extends CombinationReader
{	
	public static final String LF = System.getProperty("line.separator");
	
	public static final String PARAM_INPUT_DIR = "InputDir";
	@ConfigurationParameter(name=PARAM_INPUT_DIR, mandatory=true)
	private File inputDir;
	
	public static final String PARAM_COLLECTION = "Collection";
	@ConfigurationParameter(name=PARAM_COLLECTION, mandatory=true)
	private MeterCorpusCollection collection;
	
	public enum MeterCorpusCollection
	{
		FULL,
		SINGLE_SOURCE_SUBSET	// As used in F. Sánchez-Vega et al. Towards Document Plagiarism Detection Based on ... (2010)
	}
	
	
	@Override
	public List<CombinationPair> getAlignedPairs()
		throws ResourceInitializationException
	{
		List<CombinationPair> pairs = new ArrayList<CombinationPair>();
		
		List<File> sourceDirs = listDirectories(new File(inputDir.getAbsolutePath() + "/PA/annotated"));		
		for (File sourceDir : sourceDirs)
		{
			File sourceFile = new ArrayList<File>(FileUtils.listFiles(sourceDir, new String[] {"sgml"}, false)).get(0);
			
			File suspiciousDir = new File(sourceDir.getAbsolutePath().replace("PA", "newspapers").replace("annotated", "rawtexts"));
			
			Collection<File> suspiciousFiles = FileUtils.listFiles(suspiciousDir, new String[] {"txt"}, false);
			
			for (File suspiciousFile : suspiciousFiles)
			{
				try {
					// Source files has a 13-line header. Remove it first.
					List<String> sourceLines = FileUtils.readLines(sourceFile);
					for (int i = 0; i < 13; i++) {
                        sourceLines.remove(0);
                    }
					
					// Also remove the 5-line footer
					for (int i = 0; i < 5; i++) {
                        sourceLines.remove(sourceLines.size() - 1);
                    }
					
					// Besides that, lines may end with a "<" character. Remove it, too.
					for (int i = 0; i < sourceLines.size(); i++)
					{
						String line = sourceLines.get(i);
						
						if (line.endsWith("<"))
						{
							line = line.substring(0, line.length() - 1);
							sourceLines.set(i, line);
						}
					}						
					
					String source = StringUtils.join(sourceLines, LF);					
					String suspicious = FileUtils.readFileToString(suspiciousFile);
					
					// Get IDs
					String sourceID = sourceFile.getAbsolutePath().substring(
							sourceFile.getAbsolutePath().indexOf("meter") + 6);	
					sourceID = sourceID.substring(0, sourceID.length() - 5);
					sourceID = sourceID.replace("annotated/", "");
					
					String suspiciousID = suspiciousFile.getAbsolutePath().substring(
							suspiciousFile.getAbsolutePath().indexOf("meter") + 6);
					suspiciousID = suspiciousID.substring(0, suspiciousID.length() - 4);
					suspiciousID = suspiciousID.replace("rawtexts/", "");
						
					CombinationPair pair = new CombinationPair(inputDir.getAbsolutePath());
					pair.setID1(suspiciousID);
					pair.setID2(sourceID);
					pair.setText1(suspicious);
					pair.setText2(source);
					
					pairs.add(pair);
				}
				catch (IOException e) {
					throw new ResourceInitializationException(e);
				}
			}
		}

		return pairs;
	}
	
	private List<File> listDirectories(File folder)
	{
		List<File> files = new ArrayList<File>();
		
		List<File> fs = listFoldersRecursively(folder);
		
		for (File file : fs)
		{
			if (file.isDirectory())
			{
				// Check if we use the SINGLE_SOURCE_SUBSET
				if (collection.equals(MeterCorpusCollection.SINGLE_SOURCE_SUBSET))
				{
					Collection<File> txtFiles = FileUtils.listFiles(file, new String[] {"sgml", "txt"}, false);
					
					if (txtFiles.size() == 1) {
                        files.add(file);
                    }
				}
				else {
					// TODO Well, implement this if necessary
					throw new NotImplementedException("Currently, only the SINGLE_SOURCE_SUBSET collection reader is implemented.");
				}
			}
		}
		
		return files;
	}
	
	private List<File> listFoldersRecursively(File folder)
	{
		List<File> folders = new ArrayList<File>();
		
		File[] subfolders = folder.listFiles((FilenameFilter) FileFilterUtils.directoryFileFilter());
		
		if (subfolders.length == 1 && subfolders[0].getName().startsWith("."))
		{
			folders.add(folder);
		}
		else
		{		
			for (File subfolder : subfolders)
			{
				if (!subfolder.getName().startsWith("."))
				{
					folders.addAll(listFoldersRecursively(subfolder));
				}
			}
		}
		
		return folders;
	}

}
