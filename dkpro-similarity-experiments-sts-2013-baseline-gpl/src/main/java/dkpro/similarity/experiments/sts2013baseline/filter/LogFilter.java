/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 ******************************************************************************/
package dkpro.similarity.experiments.sts2013baseline.filter;

import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Capabilities.Capability;
import weka.filters.SimpleStreamFilter;


public class LogFilter
	extends SimpleStreamFilter
{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected Instances determineOutputFormat(Instances inst)
		throws Exception
	{
		// Filter leaves the instance format unchanged
		return inst;
	}

	@Override
	protected Instance process(Instance inst)
		throws Exception
	{
	     Instance newInst = new DenseInstance(inst.numAttributes());
	     
	     newInst.setValue(0, inst.value(0));

	     for(int i = 1; i < inst.numAttributes() - 1; i++)
	     {
	    	 double newVal = Math.log(inst.value(i) + 1);	    	 
	    	 newInst.setValue(i, newVal);
	     }
	     
	     newInst.setValue(inst.numAttributes() - 1, inst.value(inst.numAttributes() - 1));
	     
	     return newInst;
	}
	
	@Override
	public Capabilities getCapabilities()
	{
		Capabilities result = super.getCapabilities();
	    result.enableAllAttributes();
	    result.enableAllClasses();
	    result.enable(Capability.MISSING_VALUES);
	    result.enable(Capability.NO_CLASS);  		// filter doesn't need class to be set
	    return result;
	}

	@Override
	public String globalInfo()
	{
		return "A simple filter which applies the log function to all attribute values.";
	}

}