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


/* First created by JCasGen Wed May 23 16:07:44 CEST 2012 */
package de.tudarmstadt.ukp.similarity.dkpro.api.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Thu May 31 14:36:10 CEST 2012
 * XML source: /home/danielb/Projekte/Similarity/workspace/de.tudarmstadt.ukp.similarity/de.tudarmstadt.ukp.similarity.dkpro/de.tudarmstadt.ukp.similarity.dkpro.api/src/main/resources/desc/type/Relatedness.xml
 * @generated */
public class GoldTextSimilarityScore extends TextSimilarityScore {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(GoldTextSimilarityScore.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected GoldTextSimilarityScore() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public GoldTextSimilarityScore(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public GoldTextSimilarityScore(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public GoldTextSimilarityScore(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
}

    