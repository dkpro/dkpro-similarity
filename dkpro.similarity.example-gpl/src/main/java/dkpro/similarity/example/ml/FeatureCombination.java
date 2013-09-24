package dkpro.similarity.example.ml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.tudarmstadt.ukp.similarity.ml.util.ArffConverter;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class FeatureCombination
{
	public static final File TEMP_ARFF_FILE = new File("target/temp.arff");
	public static final File TEMP_ARFF_FILE_2 = new File("target/temp2.arff");
	
	List<File> trainFeatures = new ArrayList<File>();
	List<File> testFeatures = new ArrayList<File>();
	
	String trainPath;
	String testPath;

	public static void main(String[] args)
	{
		FeatureCombination p = new FeatureCombination(
			"target/train-features",
			"target/test-features");
	}
	
	public FeatureCombination(String trainPath, String testPath)
	{
		this.trainPath = trainPath;
		this.testPath = testPath;
	}

	public void addFeature(String feature)
	{
		trainFeatures.add(new File(trainPath + "/" + feature));
		testFeatures.add(new File(trainPath + "/" + feature));
	}
	
	private Instances getTrainInstances()
		throws Exception
	{		
		// TODO: WHERE DOES THE GOLD STANDARD COME FROM?
		// TODO: Create shared gold standard wrapper which can also be used with the yet-to-come GoldStandardAnnotator
		
		File gs = null; // new File("src/main/resources/goldstandards/semeval/train/STS.gs." + dataset.toString() + ".txt");
		
		// Convert to arff
		String arff = ArffConverter.toArffString(
				trainFeatures,
				gs);
		
		// Output to file
		FileUtils.writeStringToFile(TEMP_ARFF_FILE, arff);
		
		// Read with Weka
		Instances data = DataSource.read(TEMP_ARFF_FILE.getAbsolutePath());
		
		// Set the index of the class attribute
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}

	private Instances getTestInstances()
		throws Exception
	{			
		// Convert to arff
		String arff = ArffConverter.toArffString(
				testFeatures,
				null);	// we pass null for the gold standard in the Test setting
		
		// Output to file
		FileUtils.writeStringToFile(TEMP_ARFF_FILE, arff);
		
		// Read with Weka
		Instances data = DataSource.read(TEMP_ARFF_FILE.getAbsolutePath());
		
		// Set the index of the class attribute
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}
}
