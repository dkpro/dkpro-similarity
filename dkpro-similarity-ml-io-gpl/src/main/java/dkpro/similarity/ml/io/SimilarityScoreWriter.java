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
package dkpro.similarity.ml.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import dkpro.similarity.uima.api.type.ExperimentalTextSimilarityScore;
import dkpro.similarity.uima.api.type.GoldTextSimilarityScore;
import dkpro.similarity.uima.api.type.TextSimilarityScore;
import dkpro.similarity.uima.io.CombinationReader;


/**
 * Writer which outputs text similarity scores along with their goldstandard
 * scores (optional).
 */
public class SimilarityScoreWriter
	extends JCasConsumer_ImplBase
{
    public static final String LF = System.getProperty("line.separator");
    
	public static final String PARAM_OUTPUT_FILE = "OutputFile";
	@ConfigurationParameter(name=PARAM_OUTPUT_FILE, mandatory=true)
	private File outputFile;
	
	public static final String PARAM_OUTPUT_SCORES_ONLY = "OutputScoresOnly";
	@ConfigurationParameter(name=PARAM_OUTPUT_SCORES_ONLY, mandatory=false)
	private boolean outputScoresOnly;
	
	public static final String PARAM_OUTPUT_GOLD_SCORES = "OutputGoldScores";
	@ConfigurationParameter(name=PARAM_OUTPUT_GOLD_SCORES, mandatory=false, defaultValue="false")
	private boolean outputGoldScores;
	
	private BufferedWriter writer;

	
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException
	{
		super.initialize(context);
		
		try {
			// Make sure all intermediate dirs are there
			outputFile.getParentFile().mkdirs();
			
			writer = new BufferedWriter(new FileWriter(outputFile));
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	@Override
	public void process(JCas jcas)
		throws AnalysisEngineProcessException
	{
		JCas view1;
		JCas view2;
		try {
			view1 = jcas.getView(CombinationReader.VIEW_1);
			view2 = jcas.getView(CombinationReader.VIEW_2);
		}
		catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
		DocumentMetaData md1 = JCasUtil.selectSingle(view1, DocumentMetaData.class);
		DocumentMetaData md2 = JCasUtil.selectSingle(view2, DocumentMetaData.class);
		
		TextSimilarityScore score = JCasUtil.selectSingle(jcas, ExperimentalTextSimilarityScore.class);
		
		TextSimilarityScore goldScore = null;
		if (outputGoldScores) {
            goldScore = JCasUtil.selectSingle(jcas, GoldTextSimilarityScore.class);
        } 
		
		try {
			if (outputScoresOnly)
			{
				if (outputGoldScores) {
                    writer.write(score.getScore() + "\t" + goldScore.getScore() + LF);
                }
                else {
                    writer.write(score.getScore() + LF);
                }
			} else {
				if (outputGoldScores) {
					writer.write(md1.getDocumentId() + "\t" + 
							 md2.getDocumentId() + "\t" + 
							 score.getScore() + "\t" + 
							 goldScore.getScore() + LF);
				} else {
					writer.write(md1.getDocumentId() + "\t" + 
							 md2.getDocumentId() + "\t" + 
							 score.getScore() + LF);
				}
			}
		}
		catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	@Override
	public void collectionProcessComplete()
		throws AnalysisEngineProcessException
	{
		try {
			writer.close();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		super.collectionProcessComplete();
	}
}
