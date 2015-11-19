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
package dkpro.similarity.uima.api.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Sat Sep 28 15:25:36 CEST 2013
 * XML source: /home/zesch/workspace_new/dkpro.similarity-asl/dkpro.similarity.uima.api-asl/src/main/resources/desc/type/Similarity.xml
 * @generated */
public class ExperimentalTextSimilarityScore extends TextSimilarityScore {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(ExperimentalTextSimilarityScore.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ExperimentalTextSimilarityScore() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ExperimentalTextSimilarityScore(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ExperimentalTextSimilarityScore(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ExperimentalTextSimilarityScore(JCas jcas, int begin, int end) {
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

    