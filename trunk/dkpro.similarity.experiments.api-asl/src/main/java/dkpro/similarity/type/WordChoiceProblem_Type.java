
/* First created by JCasGen Fri Sep 21 09:54:00 CEST 2012 */
package dkpro.similarity.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Wed Sep 25 09:04:05 CEST 2013
 * @generated */
public class WordChoiceProblem_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (WordChoiceProblem_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = WordChoiceProblem_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new WordChoiceProblem(addr, WordChoiceProblem_Type.this);
  			   WordChoiceProblem_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new WordChoiceProblem(addr, WordChoiceProblem_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = WordChoiceProblem.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("dkpro.similarity.type.WordChoiceProblem");
 
  /** @generated */
  final Feature casFeat_Target;
  /** @generated */
  final int     casFeatCode_Target;
  /** @generated */ 
  public String getTarget(int addr) {
        if (featOkTst && casFeat_Target == null)
      jcas.throwFeatMissing("Target", "dkpro.similarity.type.WordChoiceProblem");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Target);
  }
  /** @generated */    
  public void setTarget(int addr, String v) {
        if (featOkTst && casFeat_Target == null)
      jcas.throwFeatMissing("Target", "dkpro.similarity.type.WordChoiceProblem");
    ll_cas.ll_setStringValue(addr, casFeatCode_Target, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Candidate1;
  /** @generated */
  final int     casFeatCode_Candidate1;
  /** @generated */ 
  public String getCandidate1(int addr) {
        if (featOkTst && casFeat_Candidate1 == null)
      jcas.throwFeatMissing("Candidate1", "dkpro.similarity.type.WordChoiceProblem");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Candidate1);
  }
  /** @generated */    
  public void setCandidate1(int addr, String v) {
        if (featOkTst && casFeat_Candidate1 == null)
      jcas.throwFeatMissing("Candidate1", "dkpro.similarity.type.WordChoiceProblem");
    ll_cas.ll_setStringValue(addr, casFeatCode_Candidate1, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Candidate2;
  /** @generated */
  final int     casFeatCode_Candidate2;
  /** @generated */ 
  public String getCandidate2(int addr) {
        if (featOkTst && casFeat_Candidate2 == null)
      jcas.throwFeatMissing("Candidate2", "dkpro.similarity.type.WordChoiceProblem");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Candidate2);
  }
  /** @generated */    
  public void setCandidate2(int addr, String v) {
        if (featOkTst && casFeat_Candidate2 == null)
      jcas.throwFeatMissing("Candidate2", "dkpro.similarity.type.WordChoiceProblem");
    ll_cas.ll_setStringValue(addr, casFeatCode_Candidate2, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Candidate3;
  /** @generated */
  final int     casFeatCode_Candidate3;
  /** @generated */ 
  public String getCandidate3(int addr) {
        if (featOkTst && casFeat_Candidate3 == null)
      jcas.throwFeatMissing("Candidate3", "dkpro.similarity.type.WordChoiceProblem");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Candidate3);
  }
  /** @generated */    
  public void setCandidate3(int addr, String v) {
        if (featOkTst && casFeat_Candidate3 == null)
      jcas.throwFeatMissing("Candidate3", "dkpro.similarity.type.WordChoiceProblem");
    ll_cas.ll_setStringValue(addr, casFeatCode_Candidate3, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Candidate4;
  /** @generated */
  final int     casFeatCode_Candidate4;
  /** @generated */ 
  public String getCandidate4(int addr) {
        if (featOkTst && casFeat_Candidate4 == null)
      jcas.throwFeatMissing("Candidate4", "dkpro.similarity.type.WordChoiceProblem");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Candidate4);
  }
  /** @generated */    
  public void setCandidate4(int addr, String v) {
        if (featOkTst && casFeat_Candidate4 == null)
      jcas.throwFeatMissing("Candidate4", "dkpro.similarity.type.WordChoiceProblem");
    ll_cas.ll_setStringValue(addr, casFeatCode_Candidate4, v);}
    
  
 
  /** @generated */
  final Feature casFeat_CorrectAnswer;
  /** @generated */
  final int     casFeatCode_CorrectAnswer;
  /** @generated */ 
  public int getCorrectAnswer(int addr) {
        if (featOkTst && casFeat_CorrectAnswer == null)
      jcas.throwFeatMissing("CorrectAnswer", "dkpro.similarity.type.WordChoiceProblem");
    return ll_cas.ll_getIntValue(addr, casFeatCode_CorrectAnswer);
  }
  /** @generated */    
  public void setCorrectAnswer(int addr, int v) {
        if (featOkTst && casFeat_CorrectAnswer == null)
      jcas.throwFeatMissing("CorrectAnswer", "dkpro.similarity.type.WordChoiceProblem");
    ll_cas.ll_setIntValue(addr, casFeatCode_CorrectAnswer, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public WordChoiceProblem_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Target = jcas.getRequiredFeatureDE(casType, "Target", "uima.cas.String", featOkTst);
    casFeatCode_Target  = (null == casFeat_Target) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Target).getCode();

 
    casFeat_Candidate1 = jcas.getRequiredFeatureDE(casType, "Candidate1", "uima.cas.String", featOkTst);
    casFeatCode_Candidate1  = (null == casFeat_Candidate1) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Candidate1).getCode();

 
    casFeat_Candidate2 = jcas.getRequiredFeatureDE(casType, "Candidate2", "uima.cas.String", featOkTst);
    casFeatCode_Candidate2  = (null == casFeat_Candidate2) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Candidate2).getCode();

 
    casFeat_Candidate3 = jcas.getRequiredFeatureDE(casType, "Candidate3", "uima.cas.String", featOkTst);
    casFeatCode_Candidate3  = (null == casFeat_Candidate3) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Candidate3).getCode();

 
    casFeat_Candidate4 = jcas.getRequiredFeatureDE(casType, "Candidate4", "uima.cas.String", featOkTst);
    casFeatCode_Candidate4  = (null == casFeat_Candidate4) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Candidate4).getCode();

 
    casFeat_CorrectAnswer = jcas.getRequiredFeatureDE(casType, "CorrectAnswer", "uima.cas.Integer", featOkTst);
    casFeatCode_CorrectAnswer  = (null == casFeat_CorrectAnswer) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_CorrectAnswer).getCode();

  }
}



    