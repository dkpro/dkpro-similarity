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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import dkpro.similarity.uima.entailment.type.EntailmentClassificationOutcome;
import dkpro.similarity.uima.io.util.CombinationPair;
import dkpro.similarity.uima.io.util.EntailmentPair;

/**
 * Reader for the Recognizing Textual Entailment (RTE) datasets
 * (Dagan, Glickman, and Magnini, 2006).
 * 
 * Dagan I., Glickman O., and Magnini B. (2006). The PASCAL Recognising
 * Textual Entailment Challenge. In Machine Learning Challenges, Lecture
 * Notes in Computer Science, pages 177-190, Springer.
 * <a href="http://eprints.pascal-network.org/archive/00001298/01/dagan_et_al_rte05.pdf">(pdf)</a>
 */
public class RTECorpusReader
	extends CombinationReader
{
	
    public static final String PARAM_INPUT_FILE = "InputFile";
	@ConfigurationParameter(name=PARAM_INPUT_FILE, mandatory=true)
	private String inputFile;
	
	
	@Override
	public List<CombinationPair> getAlignedPairs()
		throws ResourceInitializationException
	{
		List<CombinationPair> pairs = new ArrayList<CombinationPair>();
			
		SAXReader reader = null;
		InputStream is = null;
		URL url;
		try {
			reader = new SAXReader(false);
			
			// Disable DTD resolution (which fails due to relative path to DTD file)
			NullEntityResolver resolver = new NullEntityResolver();
			reader.setEntityResolver(resolver);  
			
			url = ResourceUtils.resolveLocation(inputFile, this, this.getUimaContext());
			Document document = reader.read(new BufferedInputStream(url.openStream()));
			Element root = document.getRootElement();

            final XPath pairXPath = new Dom4jXPath("//pair");
            
            int i = 0;
            for (Object element : pairXPath.selectNodes(root)) {
            	i++;
                String text1 = "";
                String text2 = "";
                String entailmentOutcome = "";
            	if (element instanceof Element) {
                    Element node = (Element) element;
                    
                    String tXPath = "child::t";
                    
                    for (Object tElement : new Dom4jXPath(tXPath).selectNodes(node)) {
                        if (tElement instanceof Element) {
                        	text1 = ((Element) tElement).getText();
                        }
                    }

                    String hXPath = "child::h";
                    
                    for (Object hElement : new Dom4jXPath(hXPath).selectNodes(node)) {
                        if (hElement instanceof Element) {
                        	text2 = ((Element) hElement).getText();
                        }
                    }
                    
                    
                    // print out entailment value for use as gold standard
                    for (Object o : node.attributes()) {
                        Attribute attribute = (Attribute) o;  
                        String name = attribute.getName().toLowerCase();
                        if (name.equals("value") || name.equals("entailment")) {                    	
                        	entailmentOutcome = attribute.getValue();
                            System.out.println(i + ":" + entailmentOutcome);
                        }
                    }
                }

                EntailmentPair pair = new EntailmentPair(url.toString());
    			pair.setID1("t1-" + i);
    			pair.setID2("t2-" + i);
    			pair.setText1(text1);
    			pair.setText2(text2);
    			pair.setEntailmentOutcome(entailmentOutcome);
    			
    			pairs.add(pair);
            }
		}
        catch (JaxenException e) {
            throw new ResourceInitializationException(e);
        }
		catch (DocumentException e) {
			throw new ResourceInitializationException(e);
		}
		catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
		
		return pairs;
	}

    @Override
    public void getNext(CAS aCAS)
        throws IOException, CollectionException
    {
        super.getNext(aCAS);

        String entailmentOutcome1 = ((EntailmentPair) this.currentPair1).getEntailmentOutcome();
        String entailmentOutcome2 = ((EntailmentPair) this.currentPair1).getEntailmentOutcome();

        if (!entailmentOutcome1.equals(entailmentOutcome2)) {
            throw new CollectionException(new Throwable("Paring strategy is not valid for entailment pairs."));
        }
        
        try {
            JCas jcas = aCAS.getJCas();
            EntailmentClassificationOutcome outcome = new EntailmentClassificationOutcome(jcas);
            outcome.setOutcome(entailmentOutcome1);
            outcome.addToIndexes();
        }
        catch (CASException e) {
            throw new CollectionException(e);
        }
    }
    
    private class NullEntityResolver
    	implements EntityResolver  
    {
    	static final String emptyDtd = ""; 
    	
    	@Override
        public InputSource resolveEntity (String publicId, String systemId)
    		throws SAXException, IOException
    	{
    		return new InputSource(new ByteArrayInputStream(emptyDtd.getBytes("UTF-8")));
    	}
    }
}