

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
public class AnnotationPair extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AnnotationPair.class);
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
  protected AnnotationPair() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public AnnotationPair(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public AnnotationPair(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public AnnotationPair(JCas jcas, int begin, int end) {
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
  //* Feature: Annotation1

  /** getter for Annotation1 - gets 
   * @generated */
  public Annotation getAnnotation1() {
    if (AnnotationPair_Type.featOkTst && ((AnnotationPair_Type)jcasType).casFeat_Annotation1 == null)
      jcasType.jcas.throwFeatMissing("Annotation1", "dkpro.similarity.type.AnnotationPair");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AnnotationPair_Type)jcasType).casFeatCode_Annotation1)));}
    
  /** setter for Annotation1 - sets  
   * @generated */
  public void setAnnotation1(Annotation v) {
    if (AnnotationPair_Type.featOkTst && ((AnnotationPair_Type)jcasType).casFeat_Annotation1 == null)
      jcasType.jcas.throwFeatMissing("Annotation1", "dkpro.similarity.type.AnnotationPair");
    jcasType.ll_cas.ll_setRefValue(addr, ((AnnotationPair_Type)jcasType).casFeatCode_Annotation1, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: Annotation2

  /** getter for Annotation2 - gets 
   * @generated */
  public Annotation getAnnotation2() {
    if (AnnotationPair_Type.featOkTst && ((AnnotationPair_Type)jcasType).casFeat_Annotation2 == null)
      jcasType.jcas.throwFeatMissing("Annotation2", "dkpro.similarity.type.AnnotationPair");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AnnotationPair_Type)jcasType).casFeatCode_Annotation2)));}
    
  /** setter for Annotation2 - sets  
   * @generated */
  public void setAnnotation2(Annotation v) {
    if (AnnotationPair_Type.featOkTst && ((AnnotationPair_Type)jcasType).casFeat_Annotation2 == null)
      jcasType.jcas.throwFeatMissing("Annotation2", "dkpro.similarity.type.AnnotationPair");
    jcasType.ll_cas.ll_setRefValue(addr, ((AnnotationPair_Type)jcasType).casFeatCode_Annotation2, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: StringRepresentation1

  /** getter for StringRepresentation1 - gets 
   * @generated */
  public String getStringRepresentation1() {
    if (AnnotationPair_Type.featOkTst && ((AnnotationPair_Type)jcasType).casFeat_StringRepresentation1 == null)
      jcasType.jcas.throwFeatMissing("StringRepresentation1", "dkpro.similarity.type.AnnotationPair");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnnotationPair_Type)jcasType).casFeatCode_StringRepresentation1);}
    
  /** setter for StringRepresentation1 - sets  
   * @generated */
  public void setStringRepresentation1(String v) {
    if (AnnotationPair_Type.featOkTst && ((AnnotationPair_Type)jcasType).casFeat_StringRepresentation1 == null)
      jcasType.jcas.throwFeatMissing("StringRepresentation1", "dkpro.similarity.type.AnnotationPair");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnnotationPair_Type)jcasType).casFeatCode_StringRepresentation1, v);}    
   
    
  //*--------------*
  //* Feature: StringRepresentation2

  /** getter for StringRepresentation2 - gets 
   * @generated */
  public String getStringRepresentation2() {
    if (AnnotationPair_Type.featOkTst && ((AnnotationPair_Type)jcasType).casFeat_StringRepresentation2 == null)
      jcasType.jcas.throwFeatMissing("StringRepresentation2", "dkpro.similarity.type.AnnotationPair");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AnnotationPair_Type)jcasType).casFeatCode_StringRepresentation2);}
    
  /** setter for StringRepresentation2 - sets  
   * @generated */
  public void setStringRepresentation2(String v) {
    if (AnnotationPair_Type.featOkTst && ((AnnotationPair_Type)jcasType).casFeat_StringRepresentation2 == null)
      jcasType.jcas.throwFeatMissing("StringRepresentation2", "dkpro.similarity.type.AnnotationPair");
    jcasType.ll_cas.ll_setStringValue(addr, ((AnnotationPair_Type)jcasType).casFeatCode_StringRepresentation2, v);}    
  }

    