
/* First created by JCasGen Fri Sep 21 09:55:49 CEST 2012 */
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

/** 
 * Updated by JCasGen Wed Sep 25 09:03:58 CEST 2013
 * @generated */
public class SemRelWordPair_Type extends WordPair_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SemRelWordPair_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SemRelWordPair_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SemRelWordPair(addr, SemRelWordPair_Type.this);
  			   SemRelWordPair_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SemRelWordPair(addr, SemRelWordPair_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SemRelWordPair.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("dkpro.similarity.type.SemRelWordPair");
 
  /** @generated */
  final Feature casFeat_GoldValue;
  /** @generated */
  final int     casFeatCode_GoldValue;
  /** @generated */ 
  public double getGoldValue(int addr) {
        if (featOkTst && casFeat_GoldValue == null)
      jcas.throwFeatMissing("GoldValue", "dkpro.similarity.type.SemRelWordPair");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_GoldValue);
  }
  /** @generated */    
  public void setGoldValue(int addr, double v) {
        if (featOkTst && casFeat_GoldValue == null)
      jcas.throwFeatMissing("GoldValue", "dkpro.similarity.type.SemRelWordPair");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_GoldValue, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public SemRelWordPair_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_GoldValue = jcas.getRequiredFeatureDE(casType, "GoldValue", "uima.cas.Double", featOkTst);
    casFeatCode_GoldValue  = (null == casFeat_GoldValue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_GoldValue).getCode();

  }
}



    