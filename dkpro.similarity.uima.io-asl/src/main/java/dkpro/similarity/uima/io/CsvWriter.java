/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package dkpro.similarity.uima.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
import dkpro.similarity.uima.api.type.TextSimilarityScore;

/**
 * Writer which outputs text similarity scores along with their goldstandard
 * scores (optional).
 */
public class CsvWriter
	extends JCasConsumer_ImplBase
{
    public static final String LF = System.getProperty("line.separator");
    
	public static final String PARAM_OUTPUT_FILE = "OutputFile";
	@ConfigurationParameter(name=PARAM_OUTPUT_FILE, mandatory=true)
	private File outputFile;
	
	public static final String PARAM_OUTPUT_SCORES_ONLY = "OutputScoresOnly";
	@ConfigurationParameter(name=PARAM_OUTPUT_SCORES_ONLY, mandatory=false)
	private boolean outputScoresOnly;
	
	private BufferedWriter writer;

	
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException
	{
		super.initialize(context);
		
		try {
			// Make sure all intermediate dirs are there
			 if (!outputFile.getParentFile().mkdirs()) {
			     throw new IOException("Could not create folder: " + outputFile);
			 }
			
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
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
		
		try {
			if (outputScoresOnly)
			{
				writer.write(score.getScore() + LF);
			} else {
				writer.write(md1.getDocumentId() + "\t" + 
							 md2.getDocumentId() + "\t" + 
							 score.getScore() + LF);
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
