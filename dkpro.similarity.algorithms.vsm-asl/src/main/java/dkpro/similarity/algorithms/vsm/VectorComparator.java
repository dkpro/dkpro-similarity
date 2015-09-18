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
package dkpro.similarity.algorithms.vsm;

import java.util.Collection;
import java.util.Comparator;

import no.uib.cipr.matrix.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeIdf;
import dkpro.similarity.algorithms.vsm.store.IndexedDocumentsVectorReaderBase.WeightingModeTf;
import dkpro.similarity.algorithms.vsm.store.VectorReader;

/**
 * Compare two texts or terms represented by vectors. Combining this comparator with different
 * {@link VectorReader vector readers} and {@link InnerVectorProduct inner products}, various
 * methods such as ESA or LSA can be instantiated.
 * <p>
 * Example for LSA (configuration as used in SSpace):
 * <pre>
 * {@code
 * VectorReader lsaReader = new SSpaceVectorReader(new File("my/text/files/directory"));
 * lsaReader.setVectorAggregation(VectorAggregation.SUM);
 * VectorComparator cmp = new VectorComparator(lsaReader);
 * cmp.setInnerVectorProduct(InnerVectorProduct.COSINE);
 * cmp.setNormalization(VectorNorm.L2);
 * }
 * </pre>
 * Cf. SSpace {@code Similarity#cosineSimilarity cosine} and
 * {@code DenseVector#magnitude() vector norm} (L2) implementations.
 * <p>
 * Example for ESA using a Lucene index (e.g. of the Wikipedia):
 * <pre>
 * {@code
 * LuceneVectorReader wikipediaReader = new LuceneVectorReader(new File("my/wikipedia/lucene/index"));
 * wikipediaReader.setVectorAggregation(VectorAggregation.CENTROID);
 * wikipediaReader.setNorm(VectorNorm.L2);
 * wikipediaReader.setWeightingModeTf(WeightingModeTf.normalized);
 * wikipediaReader.WeightingModeIdf(WeightingModeIdf.constantOne);
 * wikipediaReader.setWeightingThreshold(0.0f);
 * wikipediaReader.setVectorLengthThreshold(1.0f);
 * TextMeasure cmp = new VectorComparator(wikipediaReader);
 * cmp.setInnerProduct(InnerVectorProduct.AVERAGE_PRODUCT);
 * cmp.setNormalization(VectorNorm.NONE); // Vectors already normalized by vector reader
 * }
 * </pre>
 * Several combinations were tried and evaluated by
 * [<a href="http://www.ukp.tu-darmstadt.de/fileadmin/user_upload/Group_UKP/publikationen/2011/cicling2011gstzig.pdf">Szarvas et. al, 2011</a>].
 * The following table shows these combinations:
 *
 * <pre>
 *                      |              |   inner    |
 *                      |    weights   |  product   | norm |   pruning  |
 * ---------------------+--------------+------------+------+------------+
 * Szarvas et al., 2011 | log-TF       | avg. prod. |  L2  | --         |
 * H&M (reimpl)         | norm-TF      | Lesk       |  L1  | --         |
 * H&M, 2009            | norm-TF      | Lesk       |  ??  | ??         |
 * G&M (reimpl)         | log-TF * IDF | dot prod.  |  L2  | --         |
 * G&M, 2007            | log-TF * IDF | dot prod.  |  L2  | sliding w. |
 * Zesch et al., 2008   | log-TF * IDF | dot prod.  |  L2  | --         |
 *
 *                      | weidhtingModeTf | weightingModeIdf |
 * ---------------------+-----------------+------------------+
 * log-TF * IDF         | logPlusOne      | normal           |
 * norm-TF              | normalized      | constantOne      |
 * </pre>
 *
 * <ul>
 *   <li>[<a href="http://www.jair.org/media/2669/live-2669-4346-jair.pdf">G&M, 2007</a>]: Wikipedia-based semantic interpretation for natural language processing</li>
 *   <li>[<a href="www.aclweb.org/anthology/D/D09/D09-1124.pdf">H&M, 2009</a>]: Cross-lingual Semantic Relatedness Using Encyclopedic Knowledge</li>
 *   <li>[<a href="http://www.ukp.tu-darmstadt.de/fileadmin/user_upload/Group_UKP/publikationen/2011/cicling2011gstzig.pdf">Szarvas et al., 2011</a>]: Combining heterogeneous knowledge resources for improved distributional semantic models</li>
 *   <li>[<a href="http://www.ukp.tu-darmstadt.de/fileadmin/user_upload/Group_UKP/publikationen/2008/Zesch.pdf">Zesch et al., 2008</a>]: Using Wiktionary for Computing Semantic Relatedness</li>
 * </ul>
 *
 * @see InnerVectorProduct
 * @see VectorNorm
 * @see WeightingModeTf
 * @see WeightingModeIdf
 * @author Richard Eckart de Castilho
 */
public class VectorComparator
	extends TextSimilarityMeasureBase
{
	private final Log log = LogFactory.getLog(getClass());

    private final VectorReader readerA;
    private final VectorReader readerB;

    private InnerVectorProduct innerProduct = InnerVectorProduct.COSINE;
    private VectorNorm normalization = VectorNorm.L2;

    private boolean reportNotFoundAsZero = false;

	public VectorComparator(VectorReader aIndex)
	{
		this(aIndex, aIndex);
	}

	public VectorComparator(VectorReader aIndexA, VectorReader aIndexB)
	{
		readerA = aIndexA;
		readerB = aIndexB;
	}

	/**
	 * Normally {@link #getSimilarity(String, String)} returns {@code Comparator#NOT_FOUND} when
	 * one of the terms was not found in an index. If you do not care about that and simply want to
	 * treat this case as a relatedness of zero, set this flag.
	 *
	 * @param aReportNotFoundAsZero
	 */
	public void setReportNotFoundAsZero(boolean aReportNotFoundAsZero)
	{
		reportNotFoundAsZero = aReportNotFoundAsZero;
	}

	public boolean isReportNotFoundAsZero()
	{
		return reportNotFoundAsZero;
	}

	private double getNotFound()
	{
		return reportNotFoundAsZero ? 0.0 : NOT_FOUND;
	}

	@Override
	public double getSimilarity(String term1, String term2)
		throws SimilarityException
	{
		Double preScore = preScore(term1, term2);
		if (preScore != null) {
			return preScore;
		}

		Vector v1 = readerA.getVector(term1);

		if (v1 == null) {
			// The first index usually contains the terms which have quite short concept
			// vectors. If a term concept vector is not found, there is no need to load
			// the heavy-weight document concept vector.
			if (log.isDebugEnabled()) {
				log.debug("Cutting short on term ["+term1+"] without concept vector");
			}
			return getNotFound();
		}

		Vector v2 = readerB.getVector(term2);
		if (v2 == null) {
			return getNotFound();
		}

		return innerProduct.apply(v1, v2) / normalization.apply(v1, v2);
	}

	@Override
	public double getSimilarity(Collection<String> tokenList1, Collection<String> tokenList2)
		throws SimilarityException
	{
		Vector v1 = readerA.getVector(tokenList1);

		if (v1 == null) {
			// The first index usually contains the terms which have quite short concept
			// vectors. If a term concept vector is not found, there is no need to load
			// the heavy-weight document concept vector.
			return getNotFound();
		}

		Vector v2 = readerB.getVector(tokenList2);
		if (v2 == null) {
			return getNotFound();
		}

		return innerProduct.apply(v1, v2) / normalization.apply(v1, v2);
	}

	@Override
	public String getName()
	{
		return getClass().getSimpleName() + " (" + readerA.getId() + ", " + readerB.getId() + ")";
	}

	public void setInnerProduct(InnerVectorProduct aInnerProduct)
	{
		innerProduct = aInnerProduct;
	}

	public InnerVectorProduct getInnerProduct()
	{
		return innerProduct;
	}

	public void setNormalization(VectorNorm aNormalization)
	{
		normalization = aNormalization;
	}

	public VectorNorm getNormalization()
	{
		return normalization;
	}
}