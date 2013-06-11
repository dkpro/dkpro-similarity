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
package de.tudarmstadt.ukp.similarity.experiments.coling2012.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;

public class WordIdfValuesGeneratorWriter
	extends JCasConsumer_ImplBase
{
	public static final String LF = System.getProperty("line.separator");
	    
	public static final String PARAM_OUTPUT_FILE = "OutputFile";
	@ConfigurationParameter(name=PARAM_OUTPUT_FILE, mandatory=true)
	private File outputFile;
	
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

		List<JCas> views = new ArrayList<JCas>();
		views.add(view1);
		views.add(view2);
		
		for (JCas view : views)
		{
			Collection<Lemma> lemmas = JCasUtil.select(view, Lemma.class);
			
			String lemmaString = "";
			
			for (Lemma lemma : lemmas)
			{
				try {
					lemmaString += lemma.getValue().toLowerCase() + "###";
				}
				catch (NullPointerException e)
				{
					System.out.println("   - untokenizable: " + lemma.getCoveredText());
				}
			}
				
			
			try {
				writer.write(lemmaString + LF);
			}
			catch (IOException e) {
				throw new AnalysisEngineProcessException(e);
			}
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
