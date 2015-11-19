package dkpro.similarity.experiments.sts2013baseline;

import static dkpro.similarity.experiments.sts2013baseline.Pipeline.DATASET_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.FEATURES_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.GOLDSTANDARD_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.OUTPUT_DIR;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.Dataset.ALL;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.Dataset.MSRpar;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.Dataset.MSRvid;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.Dataset.OnTheFly;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.Dataset.SMTeuroparl;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.Mode.TEST;
import static dkpro.similarity.experiments.sts2013baseline.Pipeline.Mode.TRAIN;

import java.io.File;

import org.apache.commons.io.FileUtils;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import dkpro.similarity.experiments.sts2013baseline.util.Evaluator;
import dkpro.similarity.experiments.sts2013baseline.util.Features2Arff;

public class OnTheFlyComputation
{
    public static void main(String[] args)
        throws Exception
    {  
        // Generate the features for training data
        FeatureGeneration.generateFeatures(MSRpar, TRAIN);
        FeatureGeneration.generateFeatures(MSRvid, TRAIN);
        FeatureGeneration.generateFeatures(SMTeuroparl, TRAIN);
        
        // Concatenate all training data
        FeatureGeneration.combineFeatureSets(TRAIN, ALL, MSRpar, MSRvid, SMTeuroparl);

        // Package features in arff files
        Features2Arff.toArffFile(TRAIN, ALL);

        String[] texts1 = new String[] {
                "This is an example.",
                "Example this is."
        };
        
        String[] texts2 = new String[] {
                "I need an example.",
                "Colorless green ideas sleep furiously."
        };
        
        for (int i=0; i<texts1.length; i++) {
            // create temporary input file and gold file
            File inputDir = new File(ResourceUtils.resolveLocation(DATASET_DIR + "/test/").getFile());
            File inputFile = new File(inputDir, "STS.input." + OnTheFly.name() + ".txt");
            inputFile.createNewFile();
            FileUtils.writeStringToFile(inputFile, texts1[i] + "\t" + texts2[i]);
            
            File goldDir = new File(ResourceUtils.resolveLocation(GOLDSTANDARD_DIR + "/test/").getFile());
            File goldFile = new File(goldDir, "STS.gs." + OnTheFly.name() + ".txt");
            goldFile.createNewFile();
            FileUtils.writeStringToFile(goldFile, "0.0");

            // remove old arff file
            File featureDir = new File(ResourceUtils.resolveLocation(FEATURES_DIR + "/test/").getFile());
            FileUtils.deleteDirectory(featureDir);
            
            // Generate the features for test data
            FeatureGeneration.generateFeatures(OnTheFly, TEST);
            
            // Package features in arff files
            Features2Arff.toArffFile(TEST, OnTheFly);

            // Run the classifer
            Evaluator.runLinearRegression(ALL, OnTheFly);

            // delete temporary input file
            inputFile.delete();
            goldFile.delete();
            
            // output result
            String result = FileUtils.readFileToString(new File(OUTPUT_DIR + "/test/" + OnTheFly.name() + ".csv"));
            System.out.println("Result: " + result);
        }
    }
}
