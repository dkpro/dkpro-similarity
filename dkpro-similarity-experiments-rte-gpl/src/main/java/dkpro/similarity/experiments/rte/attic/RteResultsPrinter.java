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
package dkpro.similarity.experiments.rte.attic;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import dkpro.similarity.uima.entailment.type.EntailmentClassificationOutcome;
import dkpro.similarity.uima.io.CombinationReader;

public class RteResultsPrinter
    extends JCasAnnotator_ImplBase
{

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        try {
            JCas view1 = jcas.getView(CombinationReader.VIEW_1);
            JCas view2 = jcas.getView(CombinationReader.VIEW_2);
            
            System.out.println(view1.getDocumentText());
            System.out.println(view2.getDocumentText());
            
            EntailmentClassificationOutcome outcome = JCasUtil.selectSingle(jcas, EntailmentClassificationOutcome.class);
            System.out.println(outcome);
        }
        catch (CASException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }
}