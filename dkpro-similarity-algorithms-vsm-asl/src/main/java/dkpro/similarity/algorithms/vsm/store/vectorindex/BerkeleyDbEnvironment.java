/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package dkpro.similarity.algorithms.vsm.store.vectorindex;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * Convenience class which wraps a BerkeleyDb environment.
 * Set cacheSize to null or -1 to use default.
 * Can be used as UIMA resource
 * @author chmuelle
 *
 */
public class BerkeleyDbEnvironment {
	private Environment environment;

	/**
	 * Used by UIMA
	 */
	public BerkeleyDbEnvironment() {

	}

	/**
	 *
	 * @param dbEnvPath path to db environment
	 * @param isReadOnly If true, then all databases opened in this environment must be opened as read-only. If you are writing a multi-process application, then all but one of your processes must set this value to true.
	 * @param allowCreateNew If true, the database environment is created when it is opened. If false, environment open fails if the environment does not exist. This property has no meaning if the database environment already exists.
	 * @param cacheSizeInMB
	 * @throws DatabaseException
	 */
	public BerkeleyDbEnvironment(String dbEnvPath, boolean isReadOnly, boolean allowCreateNew, Long cacheSizeInMB) throws DatabaseException {
		EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(allowCreateNew);
        envConfig.setReadOnly(isReadOnly);
        envConfig.setTransactional(false);
        if(cacheSizeInMB!=null && cacheSizeInMB != -1) {
        	long cacheSizeInByte = cacheSizeInMB * 1024 * 1024;
        	envConfig.setCacheSize(cacheSizeInByte);
        }
        environment = new Environment(new File(dbEnvPath), envConfig);
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.ir.util.berkeleydb.BerkeleyDbEnvironmentResource#getEnvironment()
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/* (non-Javadoc)
	 * @see de.tudarmstadt.ukp.dkpro.ir.util.berkeleydb.BerkeleyDbEnvironmentResource#close()
	 */
	public void close() {
		if (environment != null) {
        	try {
				environment.close();
			} catch (DatabaseException e) {
				// TODO use logger instead of out
				e.printStackTrace();
			}
        }
	}
}
