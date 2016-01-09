/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package dkpro.similarity.algorithms.lexsub.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Christian Kirschner
 */
public class BingTranslator
{
	public static final String ERROR = "#error#";
	private static final String DEFAULT_APP_ID = "2012CC64163D1CB5F806F7F2389ED2DF6A170CFF";
	private static final Pattern RESULT_PATTERN = Pattern.compile("<string.*>(.+)</string>");
	
	//public enum LANGUAGE {de, en, error};
	
	protected String appId;
	
	public BingTranslator() {
		appId = DEFAULT_APP_ID;
	}
	
	//public String translate(String textToTranslate, final LANGUAGE from, final LANGUAGE to) throws IOException {
	public String translate(String textToTranslate, final String from, final String to)
		throws IOException
	{
		// Convert to UTF-8.
		try {
			textToTranslate = URLEncoder.encode(textToTranslate, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		String uri = "http://api.microsofttranslator.com/v2/Http.svc/Translate"
				+ "?appId="	+ appId 
				+ "&text=" + textToTranslate 
				+ "&from=" + from
				+ "&to=" + to;
		
		String result;
		try {
			result = read(uri);		
		}
		catch (IOException e) {
			System.err.println(" -- could not translate!");
			
			return textToTranslate;
		}	
		
		Matcher m = RESULT_PATTERN.matcher(result);
        if (m.matches())
        	return m.group(1);

        return ERROR;
	}
	
	/*public LANGUAGE detectLanguage(String textToDetect) {
		try {
			textToDetect = URLEncoder.encode(textToDetect, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        String uri = "http://api.microsofttranslator.com/v2/Http.svc/Detect?appId=" + appId + "&text=" + textToDetect;
        String result = read(uri); //<string xmlns="http://schemas.microsoft.com/2003/10/Serialization/">de</string>
        if (result.equals(ERROR)) return LANGUAGE.error;
        Matcher m = RESULT_PATTERN.matcher(result);
        if (m.matches()){
        	try{
        		return LANGUAGE.valueOf(m.group(1));
        	} catch (IllegalArgumentException e){
        		System.err.println("Language not supported");
        	}
        } 
        return LANGUAGE.error;          
    }*/

	protected String read(final String uri)
			throws IOException
	{
		URL url = new URL(uri);
        InputStream input = url.openStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        String text = "";
        String line = null;
        while ((line = bufferedReader.readLine()) != null)
        	text = text.concat(line);
       	bufferedReader.close();
       	return text;
	}
	
	public void setAppId(final String appId){
		this.appId = appId;
	}
	
}
