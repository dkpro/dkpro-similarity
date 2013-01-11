package de.tudarmstadt.ukp.similarity.algorithms.ml;

import java.io.File;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;


public class ClassifierSimilarityMeasure
	extends JCasTextSimilarityMeasureBase
{
	public static Classifier CLASSIFIER;
	
	public enum WekaClassifier
	{
		NAIVE_BAYES,
		J48,
		SMO
	}
	
	Classifier filteredClassifier;
	List<String> features;
	
	Instances test;
	
	public ClassifierSimilarityMeasure(WekaClassifier classifier, File trainArff, File testArff)
		throws Exception
	{
		CLASSIFIER = getClassifier(classifier);
		
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
			
			Evaluation eval = new Evaluation(train);
	        eval.evaluateModel(filteredClassifier, test);
	        
	        System.out.println(filteredClassifier.toString());
		}
		catch (Exception e) {
			throw new SimilarityException(e);
		}
	}
	
	public static Classifier getClassifier(WekaClassifier classifier)
		throws IllegalArgumentException
	{
		Classifier cl;
		
		try {
			switch (classifier)
			{
				case NAIVE_BAYES:
					return new NaiveBayes();
				case J48:
					J48 j48 = new J48();			
					j48.setOptions(new String[] { "-C", "0.25", "-M", "2" });
					return j48;
				case SMO:
					cl = new SMO();
					// TODO: any parameters for SMO?
					return cl;
				default:
					throw new IllegalArgumentException("Classifier " + classifier + " not found!");
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
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
		int id = Integer.parseInt(md.getDocumentId().substring(md.getDocumentId().indexOf("-") + 1));
		
		System.out.println(id);
		
		Instance testInst = test.get(id - 1);
		
		try {
			return filteredClassifier.classifyInstance(testInst);
		}
		catch (Exception e) {
			throw new SimilarityException(e);
		}
	}

    // FIXME this should be properly implemented
    @Override
    public double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1,
            Annotation coveringAnnotation2)
        throws SimilarityException
    {
        return getSimilarity(jcas1, jcas2);
    }	
}