package de.tudarmstadt.ukp.similarity.algorithms.ml;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasureBase;
import de.tudarmstadt.ukp.similarity.ml.util.ArffConverter;


public class LinearRegressionSimilarityMeasure
	extends JCasTextSimilarityMeasureBase
{
	public static final Classifier CLASSIFIER = new LinearRegression();
	
	Classifier filteredClassifier;
	List<String> features;
	
	Instances test;
	
	public LinearRegressionSimilarityMeasure(File trainArff, File testArff)
		throws SimilarityException, IOException
	{
		// Get all instances
		Instances train = getTrainInstances(trainArff);	
		test = getTestInstances(testArff);
		
		// Apply log filter
	    /*Filter logFilter = new LogFilter();
        logFilter.setInputFormat(train);
        train = Filter.useFilter(train, logFilter);        
        logFilter.setInputFormat(test);
        test = Filter.useFilter(test, logFilter);*/ 		         
        
        Classifier clsCopy;
		try {
			// Copy the classifier
			clsCopy = AbstractClassifier.makeCopy(CLASSIFIER);
			
			// Build the classifier
			filteredClassifier = clsCopy;
			filteredClassifier.buildClassifier(train);
		}
		catch (Exception e) {
			throw new SimilarityException(e);
		}
	}
	
	private Instances getTrainInstances(File trainArff)
		throws SimilarityException
	{					
		// Read with Weka
		Instances data;
		try {
			data = DataSource.read(trainArff.getAbsolutePath());
		}
		catch (Exception e) {
			throw new SimilarityException(e);
		}
		
		// Set the index of the class attribute
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}
	
	private Instances getTestInstances(File testArff)
		throws SimilarityException
	{
		// Read with Weka
		Instances data;
		try {
			data = DataSource.read(testArff.getAbsolutePath());
		}
		catch (Exception e) {
			throw new SimilarityException(e);
		}
		
		// Set the index of the class attribute
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}
	
//	private List<String> getFeatures()
//		throws IOException
//	{
//		List<String> lines = FileUtils.readLines(trainArff);
//		
//		for (int i = lines.size() - 1; i >= 0; i--)
//		{
//			if (!lines.get(i).startsWith("@attribute ") ||
//				lines.get(i).startsWith("@attribute gold"))
//			{
//				lines.remove(i);
//			} else {
//				String ln = lines.get(i).split(" ")[1];
//				lines.remove(i);
//				lines.add(i, ln);
//			}				
//		}
//		
//		System.out.println(lines);
//		
//		return lines;
//	}
	
	@Override
	public double getSimilarity(JCas jcas1, JCas jcas2)
		throws SimilarityException
	{
		// The feature generation needs to have happend before!
		
		DocumentMetaData md = DocumentMetaData.get(jcas1);
		int id = Integer.parseInt(md.getDocumentId());
		
		System.out.println(id);
		
		Instance testInst = test.get(id - 1);
		
		try {
			return filteredClassifier.classifyInstance(testInst);
		}
		catch (Exception e) {
			throw new SimilarityException(e);
		}
	}

}
