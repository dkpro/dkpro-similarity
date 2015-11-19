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
package dkpro.similarity.algorithms.wikipedia.measures;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.wikipedia.api.MetaData;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

public class WikiLinkCache {

    private final Log logger = LogFactory.getLog(getClass());

    // maps pageIds to a list of inlinks represented by pageIds
    protected TIntObjectHashMap<int[]> cachedInLinks;

    private Wikipedia wiki;

    public WikiLinkCache(Wikipedia wiki) {
        this.wiki = wiki;
        this.cachedInLinks = new TIntObjectHashMap<int[]>();
    }

    public boolean isCacheEmpty() {
        if (cachedInLinks.size() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public void fillInLinkCache() throws WikiApiException, FileNotFoundException, IOException, ClassNotFoundException {

        logger.info("Filling InlinkCache ...");

        if (!isCacheEmpty()) {
            return;
        }

        File serializedCacheFile = getSerializedCacheFile(wiki);
        if (serializedCacheFile.exists()) {
            cachedInLinks = (TIntObjectHashMap<int[]>) deserializeObject(serializedCacheFile);
            return;
        }

        int i=0;
        for (Page article : wiki.getArticles()) {

            int[] inlinkIdIntArray = WikiLinkComparator.getInlinkIds(article);

            int pageId = article.getPageId();
            cachedInLinks.put(pageId, inlinkIdIntArray);

            if (i % 10000 == 0) {
                System.out.print(".");
            }
            i++;
        }
        System.out.println();

        serializeObject(cachedInLinks, serializedCacheFile);
    }

    public File getSerializedCacheFile(Wikipedia wiki) throws WikiApiException {
        MetaData metaData = wiki.getMetaData();
        StringBuilder sb = new StringBuilder();
        sb.append("WikipediaInlinkCache_");
        sb.append(metaData.getLanguage());
        sb.append("_");
        sb.append(metaData.getVersion());
        return new File(sb.toString());
    }

    /**
     * Serializes the cache and saves it to the given file.
     *
     * @param file the file to save the cache to
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IOException if the file cannot be read
     */
    public void serializeObject(Object o, File file) throws FileNotFoundException, IOException {
        logger.info("Writing cache to file: " + file.getAbsolutePath());
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(o);
        oos.flush();
        oos.close();
    }

    /**
     * Loads the cache from file
     *
     * @param file
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    public Object deserializeObject(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        logger.info("Loading cache from file: " + file.getAbsolutePath());

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Object o = ois.readObject();
        ois.close();

        logger.info("Done.");

        return o;
    }
}
