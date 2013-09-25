

/* First created by JCasGen Fri Sep 21 09:55:55 CEST 2012 */
package dkpro.similarity.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Wed Sep 25 09:03:34 CEST 2013
 * XML source: /home/zesch/workspace_new/dkpro.similarity-asl/dkpro.similarity.experiments.api-asl/src/main/resources/desc/type/SemanticRelatedness.xml
 * @generated */
public class WeightedAnnotationPair extends AnnotationPair {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(WeightedAnnotationPair.class);
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
  protected WeightedAnnotationPair() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public WeightedAnnotationPair(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public WeightedAnnotationPair(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public WeightedAnnotationPair(JCas jcas, int begin, int end) {
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
  //* Feature: weight

  /** getter for weight - gets 
   * @generated */
  public double getWeight() {
    if (WeightedAnnotationPair_Type.featOkTst && ((WeightedAnnotationPair_Type)jcasType).casFeat_weight == null)
      jcasType.jcas.throwFeatMissing("weight", "dkpro.similarity.type.WeightedAnnotationPair");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((WeightedAnnotationPair_Type)jcasType).casFeatCode_weight);}
    
  /** setter for weight - sets  
   * @generated */
  public void setWeight(double v) {
    if (WeightedAnnotationPair_Type.featOkTst && ((WeightedAnnotationPair_Type)jcasType).casFeat_weight == null)
      jcasType.jcas.throwFeatMissing("weight", "dkpro.similarity.type.WeightedAnnotationPair");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((WeightedAnnotationPair_Type)jcasType).casFeatCode_weight, v);}    
  }

    