package de.tudarmstadt.ukp.similarity.experiments.rte.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.entailment.type.EntailmentClassificationOutcome;

import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.GOLD_DIR;


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
				sb.append(gold.get(key) + LF);
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