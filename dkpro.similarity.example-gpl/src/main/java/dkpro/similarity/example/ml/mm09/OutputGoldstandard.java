package dkpro.similarity.example.ml.mm09;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.dkpro.io.ShortAnswerGradingReader;

public class OutputGoldstandard
{
	public static void main(String[] args)
		throws Exception
	{
		CollectionReader reader = createReader(ShortAnswerGradingReader.class,
				ShortAnswerGradingReader.PARAM_INPUT_DIR, "classpath:/datasets/mm09",
				ShortAnswerGradingReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY.toString());
		
		AnalysisEngine writer = createEngine(OutputGoldstandardWriter.class,
				OutputGoldstandardWriter.PARAM_OUTPUT_FILE, "target/mm09-goldstandard.txt");

		SimplePipeline.runPipeline(reader, writer);
	}

}
