

/* First created by JCasGen Fri Dec 07 15:43:31 CET 2012 */
package de.tudarmstadt.ukp.similarity.entailment.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Dec 07 15:44:30 CET 2012
 * XML source: /home/zesch/workspace_new/de.tudarmstadt.ukp.similarity-asl/de.tudarmstadt.ukp.similarity.dkpro.api-asl/src/main/resources/desc/type/EntailmentClassificationOutcome.xml
 * @generated */
public class EntailmentClassificationOutcome extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(EntailmentClassificationOutcome.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected EntailmentClassificationOutcome() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public EntailmentClassificationOutcome(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public EntailmentClassificationOutcome(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public EntailmentClassificationOutcome(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: outcome

  /** getter for outcome - gets 
   * @generated */
  public String getOutcome() {
    if (EntailmentClassificationOutcome_Type.featOkTst && ((EntailmentClassificationOutcome_Type)jcasType).casFeat_outcome == null)
      jcasType.jcas.throwFeatMissing("outcome", "de.tudarmstadt.ukp.similarity.entailment.type.EntailmentClassificationOutcome");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EntailmentClassificationOutcome_Type)jcasType).casFeatCode_outcome);}
    
  /** setter for outcome - sets  
   * @generated */
  public void setOutcome(String v) {
    if (EntailmentClassificationOutcome_Type.featOkTst && ((EntailmentClassificationOutcome_Type)jcasType).casFeat_outcome == null)
      jcasType.jcas.throwFeatMissing("outcome", "de.tudarmstadt.ukp.similarity.entailment.type.EntailmentClassificationOutcome");
    jcasType.ll_cas.ll_setStringValue(addr, ((EntailmentClassificationOutcome_Type)jcasType).casFeatCode_outcome, v);}    
  }

    