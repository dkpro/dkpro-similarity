package de.tudarmstadt.ukp.similarity.semeval2013;

import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.Dataset.*;
import static de.tudarmstadt.ukp.similarity.semeval2013.SemEval2013Baseline.Mode.*;

import java.io.File;

import org.apache.commons.io.FileUtils;

import de.tudarmstadt.ukp.similarity.ml.util.ArffConverter;


public class SemEval2013Baseline
{
	public enum Mode
	{
		TRAIN,
		TEST
	}
	
	public enum Dataset
	{
		MSRpar,
		MSRvid,
		ALLcombined
	}
	
	public static final String FEATURES_DIR = "src/main/resources/features";
	public static final String MODELS_DIR = "src/main/resources/models";
	
	public static void main(String[] args)
		throws Exception
	{
		// TRAIN
		
		FeatureGeneration.generateFeatures(MSRpar, TRAIN);
		FeatureGeneration.generateFeatures(MSRvid, TRAIN);
		
		FeatureGeneration.combineFeatureSets(FEATURES_DIR, TRAIN, ALLcombined, MSRpar, MSRvid);
		
		Features2Arff.toArffFile(ALLcombined, TRAIN, MODELS_DIR);


	}
}
