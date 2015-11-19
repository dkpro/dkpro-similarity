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

import static dkpro.similarity.experiments.rte.Pipeline.GOLD_DIR;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import dkpro.similarity.experiments.rte.Pipeline.Dataset;
import dkpro.similarity.uima.entailment.type.EntailmentClassificationOutcome;
import dkpro.similarity.uima.io.CombinationReader;


public class GoldstandardWriter
	extends JCasConsumer_ImplBase
{	
	public static final String PARAM_DATASET_NAME = "DatasetName";
	@ConfigurationParameter(name=PARAM_DATASET_NAME, mandatory=true)
	private String datasetName;
	
	public static final String LF = System.getProperty("line.separator");
	
	Map<String,String> gold = new HashMap<String,String>();
	
	@Override
	public void process(JCas jcas)
	    throws AnalysisEngineProcessException
	{
	    try
	    {	    	
	    	JCas view1 = jcas.getView(CombinationReader.VIEW_1);
	        
	        DocumentMetaData md = DocumentMetaData.get(view1);
	        
	        String docID = md.getDocumentId().substring(md.getDocumentId().indexOf("-") + 1);
	        
	        EntailmentClassificationOutcome outcome = JCasUtil.selectSingle(jcas, EntailmentClassificationOutcome.class);
//	        System.out.println(docID + "::" + outcome.getOutcome());	        
	        gold.put(docID, outcome.getOutcome());
	    }
	    catch (CASException e) {
	        throw new AnalysisEngineProcessException(e);
	    }
	}
	
	@Override
	public void collectionProcessComplete()
		throws AnalysisEngineProcessException
	{
		super.collectionProcessComplete();
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i <= gold.entrySet().size(); i++)
		{
			String key = new Integer(i).toString();
			
			if (gold.containsKey(key))
			{
				// Transform YES/NO to TRUE/FALSE, and
				// YES/NO/UNKNOWN to ENTAILMENT/CONTRADICTION/UNKNOWN
				// if necessary
				
				if (RteUtil.hasThreeWayClassification(Dataset.valueOf(datasetName)))
				{
					if (gold.get(key).equals("YES")) {
                        sb.append("ENTAILMENT" + LF);
                    }
                    else if (gold.get(key).equals("NO")) {
                        sb.append("CONTRADICTION" + LF);
                    }
                    else {
                        sb.append(gold.get(key) + LF);
                    }
				}
				else
				{
					if (gold.get(key).equals("YES")) {
                        sb.append("TRUE" + LF);
                    }
                    else if (gold.get(key).equals("NO")) {
                        sb.append("FALSE" + LF);
                    }
                    else {
                        sb.append(gold.get(key) + LF);
                    }
				}
			}
		}
		
		File outputFile = new File(GOLD_DIR + "/" + datasetName + ".txt");
		
		try {
			FileUtils.writeStringToFile(outputFile, sb.toString());
		}
		catch (IOException e)
		{
			throw new AnalysisEngineProcessException(e);
		}
	}
}