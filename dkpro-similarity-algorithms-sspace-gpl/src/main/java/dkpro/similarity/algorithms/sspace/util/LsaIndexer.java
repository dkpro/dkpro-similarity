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
package dkpro.similarity.algorithms.sspace.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathException;
import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathFactory;
import dkpro.similarity.algorithms.sspace.util.LatentSemanticAnalysis;
import edu.ucla.sspace.common.SemanticSpaceIO;

/**
 * Creates a semantic space for LSA.
 * 
 */
public class LsaIndexer
    extends JCasAnnotator_ImplBase
{

	/**
	 * Path to the directory where the semantic space will be stored.
	 */
	public static final String PARAM_INDEX_PATH = "IndexPath";
	@ConfigurationParameter(name = PARAM_INDEX_PATH, mandatory = true)
	private File indexPath;
	
	/**
     * The maximum number of dimensions in the semantic space.
     */
    public static final String PARAM_MAX_DIMENSIONS = "maxDimensions";
    @ConfigurationParameter(name = PARAM_MAX_DIMENSIONS, mandatory = true, defaultValue = "300")
    private int maxDimensions;
    
    /**
     * This annotator is type agnostic, so it is mandatory to specify the type of the working
     * annotation and how to obtain the string representation with the feature path.
     */
    public static final String PARAM_FEATURE_PATH = "featurePath";
    @ConfigurationParameter(name = PARAM_FEATURE_PATH, mandatory = true)
    private String featurePath;

    private LatentSemanticAnalysis sspace;
    
    private int nrOfDocuments;

	@Override
	public void initialize(UimaContext context)
	        throws ResourceInitializationException
	{
		super.initialize(context);

		nrOfDocuments = 0;
		
        try {
            sspace = new LatentSemanticAnalysis();
        }
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
	    nrOfDocuments++;
	    
		final List<String> terms = new ArrayList<String>();
        try {
            for (Entry<AnnotationFS, String> entry : FeaturePathFactory.select(jCas.getCas(),
                    featurePath))
            {
//                System.out.println(entry.getKey());
//                System.out.println(entry.getValue());
                terms.add(entry.getValue());
            }
            
            sspace.processDocument(terms);
        }
        catch (FeaturePathException e) {
            throw new AnalysisEngineProcessException(e);
        }
	}

	@Override
	public void collectionProcessComplete()
	        throws AnalysisEngineProcessException
	{
		super.collectionProcessComplete();
		
        int dimensions = Math.min(nrOfDocuments, maxDimensions);

        Properties props = new Properties();
        props.setProperty(LatentSemanticAnalysis.LSA_DIMENSIONS_PROPERTY, Integer.toString(dimensions));
        sspace.processSpace(props);

        // serialize to disk
        try {
            indexPath.mkdirs();
            SemanticSpaceIO.save(sspace, new File(indexPath, "test.sspace"));
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }	
	}
}