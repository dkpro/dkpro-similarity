/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package dkpro.similarity.example.ml.mm09;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;

import dkpro.similarity.uima.io.ShortAnswerGradingReader;
import dkpro.similarity.uima.io.CombinationReader.CombinationStrategy;

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
