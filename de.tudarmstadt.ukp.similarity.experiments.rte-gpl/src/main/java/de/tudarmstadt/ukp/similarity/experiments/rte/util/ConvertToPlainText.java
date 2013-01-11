package de.tudarmstadt.ukp.similarity.experiments.rte.util;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.similarity.dkpro.io.RTECorpusReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.Dataset;

import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.DATASET_DIR;
import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.UTILS_DIR;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;


public class ConvertToPlainText
{
	public static void convert(Dataset dataset)
		throws Exception
	{
		File outputDir = new File(UTILS_DIR + "/plaintexts/" + dataset.toString() + "/");
		
		if (!outputDir.exists())
		{
			CollectionReader reader = createCollectionReader(
					 RTECorpusReader.class,
		             RTECorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY,
		             RTECorpusReader.PARAM_INPUT_FILE, RteUtil.getInputFilePathForDataset(DATASET_DIR, dataset));
			
			AnalysisEngine writer = createPrimitive(
					PlainTextWriter.class,
					PlainTextWriter.PARAM_OUTPUT_DIR, outputDir.getAbsolutePath());
		
			SimplePipeline.runPipeline(reader, writer);
		}
	}

}
