

/* First created by JCasGen Fri Sep 21 09:54:00 CEST 2012 */
package dkpro.similarity.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Sep 25 09:04:04 CEST 2013
 * XML source: /home/zesch/workspace_new/dkpro.similarity-asl/dkpro.similarity.experiments.api-asl/src/main/resources/desc/type/WordChoiceProblem.xml
 * @generated */
public class WordChoiceProblem extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(WordChoiceProblem.class);
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
  protected WordChoiceProblem() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public WordChoiceProblem(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public WordChoiceProblem(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public WordChoiceProblem(JCas jcas, int begin, int end) {
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
  //* Feature: Target

  /** getter for Target - gets 
   * @generated */
  public String getTarget() {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "dkpro.similarity.type.WordChoiceProblem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Target);}
    
  /** setter for Target - sets  
   * @generated */
  public void setTarget(String v) {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "dkpro.similarity.type.WordChoiceProblem");
    jcasType.ll_cas.ll_setStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Target, v);}    
   
    
  //*--------------*
  //* Feature: Candidate1

  /** getter for Candidate1 - gets 
   * @generated */
  public String getCandidate1() {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Candidate1 == null)
      jcasType.jcas.throwFeatMissing("Candidate1", "dkpro.similarity.type.WordChoiceProblem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Candidate1);}
    
  /** setter for Candidate1 - sets  
   * @generated */
  public void setCandidate1(String v) {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Candidate1 == null)
      jcasType.jcas.throwFeatMissing("Candidate1", "dkpro.similarity.type.WordChoiceProblem");
    jcasType.ll_cas.ll_setStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Candidate1, v);}    
   
    
  //*--------------*
  //* Feature: Candidate2

  /** getter for Candidate2 - gets 
   * @generated */
  public String getCandidate2() {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Candidate2 == null)
      jcasType.jcas.throwFeatMissing("Candidate2", "dkpro.similarity.type.WordChoiceProblem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Candidate2);}
    
  /** setter for Candidate2 - sets  
   * @generated */
  public void setCandidate2(String v) {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Candidate2 == null)
      jcasType.jcas.throwFeatMissing("Candidate2", "dkpro.similarity.type.WordChoiceProblem");
    jcasType.ll_cas.ll_setStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Candidate2, v);}    
   
    
  //*--------------*
  //* Feature: Candidate3

  /** getter for Candidate3 - gets 
   * @generated */
  public String getCandidate3() {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Candidate3 == null)
      jcasType.jcas.throwFeatMissing("Candidate3", "dkpro.similarity.type.WordChoiceProblem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Candidate3);}
    
  /** setter for Candidate3 - sets  
   * @generated */
  public void setCandidate3(String v) {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Candidate3 == null)
      jcasType.jcas.throwFeatMissing("Candidate3", "dkpro.similarity.type.WordChoiceProblem");
    jcasType.ll_cas.ll_setStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Candidate3, v);}    
   
    
  //*--------------*
  //* Feature: Candidate4

  /** getter for Candidate4 - gets 
   * @generated */
  public String getCandidate4() {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Candidate4 == null)
      jcasType.jcas.throwFeatMissing("Candidate4", "dkpro.similarity.type.WordChoiceProblem");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Candidate4);}
    
  /** setter for Candidate4 - sets  
   * @generated */
  public void setCandidate4(String v) {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_Candidate4 == null)
      jcasType.jcas.throwFeatMissing("Candidate4", "dkpro.similarity.type.WordChoiceProblem");
    jcasType.ll_cas.ll_setStringValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_Candidate4, v);}    
   
    
  //*--------------*
  //* Feature: CorrectAnswer

  /** getter for CorrectAnswer - gets 
   * @generated */
  public int getCorrectAnswer() {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_CorrectAnswer == null)
      jcasType.jcas.throwFeatMissing("CorrectAnswer", "dkpro.similarity.type.WordChoiceProblem");
    return jcasType.ll_cas.ll_getIntValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_CorrectAnswer);}
    
  /** setter for CorrectAnswer - sets  
   * @generated */
  public void setCorrectAnswer(int v) {
    if (WordChoiceProblem_Type.featOkTst && ((WordChoiceProblem_Type)jcasType).casFeat_CorrectAnswer == null)
      jcasType.jcas.throwFeatMissing("CorrectAnswer", "dkpro.similarity.type.WordChoiceProblem");
    jcasType.ll_cas.ll_setIntValue(addr, ((WordChoiceProblem_Type)jcasType).casFeatCode_CorrectAnswer, v);}    
  }

    