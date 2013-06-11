package de.tudarmstadt.ukp.similarity.example.ml.mm09;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.similarity.dkpro.api.type.ExperimentalTextSimilarityScore;
import de.tudarmstadt.ukp.similarity.dkpro.api.type.GoldTextSimilarityScore;
import de.tudarmstadt.ukp.similarity.dkpro.api.type.TextSimilarityScore;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;

public class OutputGoldstandardWriter
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
		
		GoldTextSimilarityScore score = JCasUtil.selectSingle(jcas, GoldTextSimilarityScore.class);
		
		try {
			writer.write(score.getScore() + LF);
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
