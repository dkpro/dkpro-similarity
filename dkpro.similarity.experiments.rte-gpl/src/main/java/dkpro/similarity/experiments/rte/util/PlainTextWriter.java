/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 ******************************************************************************/
package dkpro.similarity.experiments.rte.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import dkpro.similarity.uima.io.CombinationReader;

public class PlainTextWriter
	extends JCasConsumer_ImplBase
{
public static final String LF = System.getProperty("line.separator");
    
	public static final String PARAM_OUTPUT_DIR = "OutputDir";
	@ConfigurationParameter(name=PARAM_OUTPUT_DIR, mandatory=true)
	private File outputDir;

	
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException
	{
		super.initialize(context);
		
		// Make sure all intermediate dirs are there
		outputDir.mkdirs();
	}
	
	@Override
	public void process(JCas jcas)
		throws AnalysisEngineProcessException
	{
		List<JCas> views = new ArrayList<JCas>();
		try
		{
			views.add(jcas.getView(CombinationReader.VIEW_1));
			views.add(jcas.getView(CombinationReader.VIEW_2));
		}
		catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
		
		
		for (JCas view : views)
		{
			DocumentMetaData md = DocumentMetaData.get(view);
			
			File outputFile = new File(outputDir.getAbsolutePath() + "/" + md.getDocumentId() + ".txt"); 
			
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(outputFile));
				
				writer.write(view.getDocumentText());
				
				writer.close();
			}
			catch (IOException e) {
				throw new AnalysisEngineProcessException(e);
			}			
		}
	}
	
	
}
