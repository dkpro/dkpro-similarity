package de.tudarmstadt.ukp.similarity.semeval2013;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.ukp.similarity.ml.util.ArffConverter;
import de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.Dataset;
import de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.Mode;

import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.FEATURES_DIR;
import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.MODELS_DIR;


public class Features2Arff
{
	public static final String LF = System.getProperty("line.separator");
	
	public static void toArffFile(Dataset dataset, Mode mode, String targetDir)
		throws IOException
	{
		// Do not use a gold standard (i.e. create a training model)		
		toArffFile(dataset, mode, targetDir, null);		
	}
	
	public static void toArffFile(Dataset dataset, Mode mode, String targetDir, File goldStandard)
		throws IOException
	{
		// Use a gold standard (i.e. create a test model)
		
		System.out.println("Generating ARFF file");
		
		Collection<File> files = FileUtils.listFiles(
				new File(FEATURES_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString()),
				new String[] { "txt" },
				true); 
		
		String arffString = toArffString(files, goldStandard);
		
		FileUtils.writeStringToFile(
				new File(MODELS_DIR + "/" + mode.toString().toLowerCase() + "/" + dataset.toString() + ".arff"),
				arffString);
		
		System.out.println(" - done");
	}
	
	private static String toArffString(Collection<File> csvFiles, File goldFile)
		throws IOException
	{
		// Create the Arff header
		StringBuilder arff = new StringBuilder();
		arff.append("@relation temp-relation" + LF);
		arff.append(LF);
		
		// Init data object
		Map<Integer,List<Double>> data = new HashMap<Integer,List<Double>>();
		
		for (File file : csvFiles)
		{			
			String feature = file.getParentFile().getName() + "/" + file.getName().substring(0, file.getName().length() - 4);
			// feature = feature.replaceAll(",", "");
			
			// Add the attribute to the Arff header
			arff.append("@attribute " + feature + " numeric" + LF);
			
			// Read data
			List<String> lines = FileUtils.readLines(file);
			for (int doc = 1; doc <= lines.size(); doc++)
			{
				String line = lines.get(doc - 1);
				
				if (line.length() > 0)	// Ignore empty lines
				{
					double value = Double.parseDouble(line);	// There's just the score on the line, nothing else.
					
					// Get doc object in data list
					List<Double> docObj;
					if (data.containsKey(doc))
						docObj = data.get(doc);
					else
						docObj = new ArrayList<Double>();
					
					// Put data
					docObj.add(value);
					data.put(doc, docObj);
				}
			}
		}
		
		// Add gold attribute to attribute list in header
		// We also need to do this for unlabeled data
		arff.append("@attribute gold real" + LF);
		
		// Add gold similarity score 
		List<String> lines;
		if (goldFile != null)
		{
			lines = FileUtils.readLines(goldFile);
		}
		else
		{
			lines = new ArrayList<String>();
			for (int i = 0; i < FileUtils.readLines(csvFiles.iterator().next()).size(); i++)
				lines.add("0.0");
		}
			
		for (int doc = 1; doc <= lines.size(); doc++)
		{					
			double value = Double.parseDouble(lines.get(doc - 1));				
			
			List<Double> docObj = data.get(doc);
			docObj.add(value);
			data.put(doc, docObj);									
		}
		
		// Finalize header
		arff.append(LF);
		arff.append("@data" + LF);
		
		// Write data
		for (int i = 1; i <= data.keySet().size(); i++)
		{			
			String dataItem = StringUtils.join(data.get(i), ",");
			
			arff.append(dataItem + LF);
		}
		
		return arff.toString();
	}
}
