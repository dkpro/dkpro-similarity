
/* First created by JCasGen Fri Dec 07 15:43:31 CET 2012 */
package dkpro.similarity.uima.entailment.type;

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
 * Updated by JCasGen Sat Sep 28 15:25:40 CEST 2013
 * @generated */
public class EntailmentClassificationOutcome_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (EntailmentClassificationOutcome_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = EntailmentClassificationOutcome_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new EntailmentClassificationOutcome(addr, EntailmentClassificationOutcome_Type.this);
  			   EntailmentClassificationOutcome_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new EntailmentClassificationOutcome(addr, EntailmentClassificationOutcome_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = EntailmentClassificationOutcome.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("dkpro.similarity.uima.entailment.type.EntailmentClassificationOutcome");
 
  /** @generated */
  final Feature casFeat_outcome;
  /** @generated */
  final int     casFeatCode_outcome;
  /** @generated */ 
  public String getOutcome(int addr) {
        if (featOkTst && casFeat_outcome == null)
      jcas.throwFeatMissing("outcome", "dkpro.similarity.uima.entailment.type.EntailmentClassificationOutcome");
    return ll_cas.ll_getStringValue(addr, casFeatCode_outcome);
  }
  /** @generated */    
  public void setOutcome(int addr, String v) {
        if (featOkTst && casFeat_outcome == null)
      jcas.throwFeatMissing("outcome", "dkpro.similarity.uima.entailment.type.EntailmentClassificationOutcome");
    ll_cas.ll_setStringValue(addr, casFeatCode_outcome, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public EntailmentClassificationOutcome_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_outcome = jcas.getRequiredFeatureDE(casType, "outcome", "uima.cas.String", featOkTst);
    casFeatCode_outcome  = (null == casFeat_outcome) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_outcome).getCode();

  }
}



    