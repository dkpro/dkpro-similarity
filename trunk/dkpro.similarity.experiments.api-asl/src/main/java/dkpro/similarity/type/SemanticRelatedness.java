

/* First created by JCasGen Fri Sep 21 09:55:55 CEST 2012 */
package dkpro.similarity.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Sep 25 09:03:34 CEST 2013
 * XML source: /home/zesch/workspace_new/dkpro.similarity-asl/dkpro.similarity.experiments.api-asl/src/main/resources/desc/type/SemanticRelatedness.xml
 * @generated */
public class SemanticRelatedness extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SemanticRelatedness.class);
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
  protected SemanticRelatedness() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public SemanticRelatedness(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public SemanticRelatedness(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public SemanticRelatedness(JCas jcas, int begin, int end) {
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
  //* Feature: MeasureType

  /** getter for MeasureType - gets The type of the semantic relatedness measure that was used to create the annotation.
   * @generated */
  public String getMeasureType() {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_MeasureType == null)
      jcasType.jcas.throwFeatMissing("MeasureType", "dkpro.similarity.type.SemanticRelatedness");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_MeasureType);}
    
  /** setter for MeasureType - sets The type of the semantic relatedness measure that was used to create the annotation. 
   * @generated */
  public void setMeasureType(String v) {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_MeasureType == null)
      jcasType.jcas.throwFeatMissing("MeasureType", "dkpro.similarity.type.SemanticRelatedness");
    jcasType.ll_cas.ll_setStringValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_MeasureType, v);}    
   
    
  //*--------------*
  //* Feature: RelatednessValue

  /** getter for RelatednessValue - gets The semantic relatedness value.
   * @generated */
  public double getRelatednessValue() {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_RelatednessValue == null)
      jcasType.jcas.throwFeatMissing("RelatednessValue", "dkpro.similarity.type.SemanticRelatedness");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_RelatednessValue);}
    
  /** setter for RelatednessValue - sets The semantic relatedness value. 
   * @generated */
  public void setRelatednessValue(double v) {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_RelatednessValue == null)
      jcasType.jcas.throwFeatMissing("RelatednessValue", "dkpro.similarity.type.SemanticRelatedness");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_RelatednessValue, v);}    
   
    
  //*--------------*
  //* Feature: MeasureName

  /** getter for MeasureName - gets The name of the semantic relatedness measure that was used to create the annotation.
   * @generated */
  public String getMeasureName() {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_MeasureName == null)
      jcasType.jcas.throwFeatMissing("MeasureName", "dkpro.similarity.type.SemanticRelatedness");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_MeasureName);}
    
  /** setter for MeasureName - sets The name of the semantic relatedness measure that was used to create the annotation. 
   * @generated */
  public void setMeasureName(String v) {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_MeasureName == null)
      jcasType.jcas.throwFeatMissing("MeasureName", "dkpro.similarity.type.SemanticRelatedness");
    jcasType.ll_cas.ll_setStringValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_MeasureName, v);}    
   
    
  //*--------------*
  //* Feature: WordPair

  /** getter for WordPair - gets 
   * @generated */
  public WordPair getWordPair() {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_WordPair == null)
      jcasType.jcas.throwFeatMissing("WordPair", "dkpro.similarity.type.SemanticRelatedness");
    return (WordPair)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_WordPair)));}
    
  /** setter for WordPair - sets  
   * @generated */
  public void setWordPair(WordPair v) {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_WordPair == null)
      jcasType.jcas.throwFeatMissing("WordPair", "dkpro.similarity.type.SemanticRelatedness");
    jcasType.ll_cas.ll_setRefValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_WordPair, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: Term1

  /** getter for Term1 - gets 
   * @generated */
  public String getTerm1() {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_Term1 == null)
      jcasType.jcas.throwFeatMissing("Term1", "dkpro.similarity.type.SemanticRelatedness");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_Term1);}
    
  /** setter for Term1 - sets  
   * @generated */
  public void setTerm1(String v) {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_Term1 == null)
      jcasType.jcas.throwFeatMissing("Term1", "dkpro.similarity.type.SemanticRelatedness");
    jcasType.ll_cas.ll_setStringValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_Term1, v);}    
   
    
  //*--------------*
  //* Feature: Term2

  /** getter for Term2 - gets 
   * @generated */
  public String getTerm2() {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_Term2 == null)
      jcasType.jcas.throwFeatMissing("Term2", "dkpro.similarity.type.SemanticRelatedness");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_Term2);}
    
  /** setter for Term2 - sets  
   * @generated */
  public void setTerm2(String v) {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_Term2 == null)
      jcasType.jcas.throwFeatMissing("Term2", "dkpro.similarity.type.SemanticRelatedness");
    jcasType.ll_cas.ll_setStringValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_Term2, v);}    
   
    
  //*--------------*
  //* Feature: AnnotationPair

  /** getter for AnnotationPair - gets 
   * @generated */
  public AnnotationPair getAnnotationPair() {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_AnnotationPair == null)
      jcasType.jcas.throwFeatMissing("AnnotationPair", "dkpro.similarity.type.SemanticRelatedness");
    return (AnnotationPair)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_AnnotationPair)));}
    
  /** setter for AnnotationPair - sets  
   * @generated */
  public void setAnnotationPair(AnnotationPair v) {
    if (SemanticRelatedness_Type.featOkTst && ((SemanticRelatedness_Type)jcasType).casFeat_AnnotationPair == null)
      jcasType.jcas.throwFeatMissing("AnnotationPair", "dkpro.similarity.type.SemanticRelatedness");
    jcasType.ll_cas.ll_setRefValue(addr, ((SemanticRelatedness_Type)jcasType).casFeatCode_AnnotationPair, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    