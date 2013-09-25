

/* First created by JCasGen Fri Sep 21 09:55:49 CEST 2012 */
package dkpro.similarity.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Wed Sep 25 09:03:58 CEST 2013
 * XML source: /home/zesch/workspace_new/dkpro.similarity-asl/dkpro.similarity.experiments.api-asl/src/main/resources/desc/type/SemRelWordPair.xml
 * @generated */
public class SemRelWordPair extends WordPair {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SemRelWordPair.class);
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
  protected SemRelWordPair() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public SemRelWordPair(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public SemRelWordPair(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public SemRelWordPair(JCas jcas, int begin, int end) {
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
  //* Feature: GoldValue

  /** getter for GoldValue - gets 
   * @generated */
  public double getGoldValue() {
    if (SemRelWordPair_Type.featOkTst && ((SemRelWordPair_Type)jcasType).casFeat_GoldValue == null)
      jcasType.jcas.throwFeatMissing("GoldValue", "dkpro.similarity.type.SemRelWordPair");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((SemRelWordPair_Type)jcasType).casFeatCode_GoldValue);}
    
  /** setter for GoldValue - sets  
   * @generated */
  public void setGoldValue(double v) {
    if (SemRelWordPair_Type.featOkTst && ((SemRelWordPair_Type)jcasType).casFeat_GoldValue == null)
      jcasType.jcas.throwFeatMissing("GoldValue", "dkpro.similarity.type.SemRelWordPair");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((SemRelWordPair_Type)jcasType).casFeatCode_GoldValue, v);}    
  }

    