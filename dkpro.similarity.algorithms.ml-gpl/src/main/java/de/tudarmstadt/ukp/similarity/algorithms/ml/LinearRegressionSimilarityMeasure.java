package de.tudarmstadt.ukp.similarity.algorithms.ml;

import java.io.File;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.similarity.ml.filters.LogFilter;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import dkpro.similarity.algorithms.api.SimilarityException;


/**
 * Runs a linear regression classifier on the provided test data on a model
 * that is trained on the given training data. Mind that the
 * {@link #getSimilarity(JCas,JCas) getSimilarity} method
 * classifies the input texts by their ID, not their textual contents. The
 * <pre>DocumentID</pre> of the <pre>DocumentMetaData</pre> is expected to denote
 * the corresponding input line in the test data.
 */
public class LinearRegressionSimilarityMeasure
	extends JCasTextSimilarityMeasureBase
{
	public static final Classifier CLASSIFIER = new LinearRegression();
	
	Classifier filteredClassifier;
	List<String> features;
	
	Instances test;
	
	public LinearRegressionSimilarityMeasure(File trainArff, File testArff, boolean useLogFilter)
		throws Exception
	{
		// Get all instances
		Instances train = getTrainInstances(trainArff);	
		test = getTestInstances(testArff);
		
		// Apply log filter
		if (useLogFilter)
		{
			Filter logFilter = new LogFilter();
			logFilter.setInputFormat(train);
			train = Filter.useFilter(train, logFilter);        
			logFilter.setInputFormat(test);
			test = Filter.useFilter(test, logFilter);
		}
        
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
	
	@Override
	public double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1,
            Annotation coveringAnnotation2)
		throws SimilarityException
	{
		// The feature generation needs to have happened before!
		
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