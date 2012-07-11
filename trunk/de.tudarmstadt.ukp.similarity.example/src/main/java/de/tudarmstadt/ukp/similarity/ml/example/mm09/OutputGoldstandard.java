package de.tudarmstadt.ukp.similarity.ml.example.mm09;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitive;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.similarity.dkpro.io.ShortAnswerGradingReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;

public class OutputGoldstandard
{
	public static void main(String[] args)
		throws Exception
	{
		CollectionReader reader = createCollectionReader(ShortAnswerGradingReader.class,
				ShortAnswerGradingReader.PARAM_INPUT_DIR, "classpath:/datasets/mm09",
				ShortAnswerGradingReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		
		AnalysisEngine writer = createPrimitive(OutputGoldstandardWriter.class,
				OutputGoldstandardWriter.PARAM_OUTPUT_FILE, "target/mm09-goldstandard.txt");

		SimplePipeline.runPipeline(reader, writer);
	}

}
