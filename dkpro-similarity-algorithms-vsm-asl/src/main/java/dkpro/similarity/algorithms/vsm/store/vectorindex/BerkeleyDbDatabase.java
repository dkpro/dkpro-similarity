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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

/**
 * Convenience class which wraps a BerkeleyDB database.
 * @author chmuelle
 *
 */
public class BerkeleyDbDatabase {
	protected Database db;
	protected TupleBinding entryBinding;
	protected TupleBinding keyBinding;
	
	public static final String KEY_ENCODING = "UTF-8";
	
	/**
	 * Uses Strings as keys.
	 * @param environment
	 * @param dbName
	 * @param isReadOnly
	 * @param allowCreateNew
	 * @param entryBinding
	 * @throws DatabaseException
	 */
	public BerkeleyDbDatabase(BerkeleyDbEnvironment environment, String dbName, boolean isReadOnly, boolean allowCreateNew, TupleBinding entryBinding) throws DatabaseException {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(allowCreateNew);
        dbConfig.setReadOnly(isReadOnly);
        dbConfig.setTransactional(false);
        db = environment.getEnvironment().openDatabase(null,dbName,dbConfig);
		this.entryBinding = entryBinding;
		this.keyBinding = TupleBinding.getPrimitiveBinding(String.class);
	}
	
	/**
	 * Uses custom bindings or int for keys.
	 * @param environment
	 * @param dbName
	 * @param isReadOnly
	 * @param allowCreateNew
	 * @param keyBinding
	 * @param entryBinding
	 * @throws DatabaseException
	 */
	public BerkeleyDbDatabase(BerkeleyDbEnvironment environment, String dbName, boolean isReadOnly, boolean allowCreateNew, TupleBinding keyBinding, TupleBinding entryBinding) throws DatabaseException {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(allowCreateNew);
        dbConfig.setReadOnly(isReadOnly);
        dbConfig.setTransactional(false);
        db = environment.getEnvironment().openDatabase(null,dbName,dbConfig);
		this.entryBinding = entryBinding;
		this.keyBinding = keyBinding;
	}
	
	public void put(Object id, Object obj) throws DatabaseException {
		DatabaseEntry key= new DatabaseEntry();
		
		keyBinding.objectToEntry(id, key);
		
		DatabaseEntry data = new DatabaseEntry();
		entryBinding.objectToEntry(obj, data);
		db.put(null, key, data);
	}
	
	public void put(String id, Object obj) throws DatabaseException {
		DatabaseEntry key;
		try {
			key = new DatabaseEntry(id.getBytes(KEY_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		DatabaseEntry data = new DatabaseEntry();
		entryBinding.objectToEntry(obj, data);
		db.put(null, key, data);
	}
	
	public Object get(Object id) throws DatabaseException {
		DatabaseEntry key = new DatabaseEntry();
		
		keyBinding.objectToEntry(id, key);
		
		DatabaseEntry data = new DatabaseEntry();
		OperationStatus status = db.get(null, key, data, LockMode.DEFAULT);
		Object obj = null;
		if(status == OperationStatus.SUCCESS) {
			obj = entryBinding.entryToObject(data);
		} 
		
		return obj;
	}
	
	public Object get(String id) throws DatabaseException {
		DatabaseEntry key;
		try {
			key = new DatabaseEntry(id.getBytes(KEY_ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		DatabaseEntry data = new DatabaseEntry();
		OperationStatus status = db.get(null, key, data, LockMode.DEFAULT);
		Object obj = null;
		if(status == OperationStatus.SUCCESS) {
			obj = entryBinding.entryToObject(data);
			//System.out.println("success");
		} 
		/*else {
			System.out.println("no success: status.toString()");
		}*/
		return obj;
	}
	
	/**
	 * Returns a cursor. Don't forget to close it.
	 * @return
	 * @throws DatabaseException
	 */
	public Cursor getCursor() throws DatabaseException {
		return db.openCursor(null, null);
	}
	
	/**
	 * Gets the keys of all stored entries.
	 * @return List of keys
	 * @throws DatabaseException
	 * @throws UnsupportedEncodingException
	 */
	public List<String> getKeys() throws DatabaseException, UnsupportedEncodingException {
		Cursor cursor = getCursor();
		DatabaseEntry foundKey = new DatabaseEntry();
	    DatabaseEntry foundData = new DatabaseEntry();

	    List<String> keys = new ArrayList<String>();
	    
	    try {
			while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			    String keyString = new String(foundKey.getData(), KEY_ENCODING);
			    keys.add(keyString);
			}
		} finally {
			cursor.close();
		}
		
		return keys;
	}
	
	public Iterator<String> keyIterator() throws DatabaseException {
		return new BerkeleyDbKeyIterator();
	}
	
	public Iterator<Integer> idIterator() throws DatabaseException {
		return new BerkeleyDbIdIterator();
	}
		
	public boolean hasKey(String key) throws DatabaseException {
		DatabaseEntry dbKey = new DatabaseEntry();
		
		keyBinding.objectToEntry(key, dbKey);
		
		DatabaseEntry data = new DatabaseEntry();
		OperationStatus status = db.get(null, dbKey, data, LockMode.DEFAULT);
		
		if (status == OperationStatus.NOTFOUND) {
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public String getContentInformation() throws DatabaseException, UnsupportedEncodingException {
		StringBuilder out = new StringBuilder();
		
		Cursor cursor = getCursor();
		DatabaseEntry foundKey = new DatabaseEntry();
	    DatabaseEntry foundData = new DatabaseEntry();

	    int i=0;
	    try {
			while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			    String keyString = new String(foundKey.getData(), KEY_ENCODING);
			    Object obj = entryBinding.entryToObject(foundData);
			    out.append(i);
			    out.append(". Element\n");
			    out.append("key: \n");
			    out.append(keyString);
			    out.append("\nvalue: \n");
			    out.append(obj.toString());
			    out.append("\n");
			    i++;
			}
		} finally {
			cursor.close();
		}
		
		return out.toString();
	}
	
	public void close()  {
		if (db != null) {
        	try {
				db.close();
			} catch (DatabaseException e) {
				// TODO use logger instead of out
				e.printStackTrace();
			}	
        }
	}
	
	class BerkeleyDbKeyIterator implements Iterator<String> {
		private Cursor cursor;
		private String currentKey;
		
		DatabaseEntry foundKey = new DatabaseEntry();
	    DatabaseEntry foundData = new DatabaseEntry();
		
		public BerkeleyDbKeyIterator() throws DatabaseException {
			cursor = getCursor();
		}
		
		@Override
		public boolean hasNext()
		{
			try {
				if (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
					currentKey = new String(foundKey.getData(), KEY_ENCODING);
					return true;
				}
				else {
					cursor.close();
					return false;
				}
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				return false;
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public String next()
		{
			return currentKey;
		}

		@Override
		public void remove()
		{
			// nothing
		}
	}
	
	class BerkeleyDbIdIterator implements Iterator<Integer> {
		private Cursor cursor;
		private Integer currentKey;
		
		DatabaseEntry foundKey = new DatabaseEntry();
	    DatabaseEntry foundData = new DatabaseEntry();
		
		public BerkeleyDbIdIterator() throws DatabaseException {
			cursor = getCursor();
		}
		
		@Override
		public boolean hasNext()
		{
			try {
				if (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
					currentKey = (Integer) keyBinding.entryToObject(foundKey);
					return true;
				}
				else {
					cursor.close();
					return false;
				}
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public Integer next()
		{
			return currentKey;
		}

		@Override
		public void remove()
		{
			// nothing
		}
	}
}
