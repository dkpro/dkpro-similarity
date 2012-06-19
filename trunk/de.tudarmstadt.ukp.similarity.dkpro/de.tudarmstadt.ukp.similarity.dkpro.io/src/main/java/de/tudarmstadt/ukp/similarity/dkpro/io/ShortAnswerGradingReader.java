package de.tudarmstadt.ukp.similarity.dkpro.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ConfigurationParameter;

import de.tudarmstadt.ukp.similarity.dkpro.io.util.CombinationPair;

public class ShortAnswerGradingReader
	extends CombinationReader
{
	public static final String PARAM_INPUT_DIR = "InputDir";
	@ConfigurationParameter(name=PARAM_INPUT_DIR, mandatory=true)
	private File inputDir;
	
	public static final String PARAM_ENCODING = "Encoding";
	@ConfigurationParameter(name=PARAM_ENCODING, mandatory=false, defaultValue="UTF-8")
	private String encoding;

	@Override
	public List<CombinationPair> getAlignedPairs()
		throws ResourceInitializationException
	{
		List<CombinationPair> pairs = new ArrayList<CombinationPair>();
		
		Collection<File> files = FileUtils.listFiles(inputDir, new String[]{ "txt" }, true);
		Iterator<File> iterator = files.iterator();
		
		try
		{
			while(iterator.hasNext())
			{
				// Process assignment
				File file = iterator.next();
				
				int assignment = Integer.parseInt(file.getName().substring(0, file.getName().length() - 4));
				
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
						line.substring(12);			// Skip "\t\tQuestion: " prefix
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
						
						// Add to combination pairs
						CombinationPair pair = new CombinationPair(inputDir.toString());
						pair.setID1(assignment + ":" + questionIndex);
						pair.setID2(assignment + ":" + questionIndex + ":" + studentID);
						pair.setText1(refAnswer);
						pair.setText2(answer);
						
						System.out.println(refAnswer + "#" + pair.getID1() + "#" + answer + "#" + pair.getID2());
						
						pairs.add(pair);
					}
				}			
			}
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		
		return pairs;
	}
}
