---
layout: page-fullwidth
title: "Setting up the Resources"
permalink: "/settinguptheresources/"
---

Some text similarity measures implemented in our framework operate on lexical-semantic resources, e.g. measures which determine pairwise word similarity on WordNet. In the following, we describe which resources are required by which measures, and how they can be obtained and installed.

## Prerequisite: DKPRO_HOME environment variable
Before continuing, please make sure that you have set up an environment_variable `DKPRO_HOME` either system-wide or per-project in the Eclipse run configuration. The variable should point to a (possibly yet empty) directory which is intended to store any sort of resources which are to be used by any DKPro component.

## Explicit Semantic Analysis: Vector Indexes
Explicit Semantic Analysis (ESA) (Gabrilovich and Markovitch, 2007) is a method which computes similarity based on word occurrences in a given document collection. While it was originally proposed on Wikipedia, other document collections with similar properties have also been found to work well, e.g. Wiktionary and WordNet.

The vector indexes can be downloaded [here](/dkpro-similarity/download/) for Wiktionary or WordNet. As the Wikipedia index is much larger (about 900 MB zipped), you can get it [here](https://public.ukp.informatik.tu-darmstadt.de/baer/wp_eng_lem_nc_c.zip).

After the download has finished, unzip the whole folder into `$DKPRO_HOME/ESA/VectorIndexes/<subdir>`, where subdir is an arbitrary name for each resource, e.g. "wordnet".

Please note that the indexes have been created on lemmatized texts. So please also lemmatize your input texts first, before passing them to the similarity measure.

### Understanding the difference between VectorIndexReader and LuceneVectorReader
The ESA indexes you have downloaded in the previous step are VectorIndexes that store for each word the full vector over the document space, i.e. when you want to compute the similarity between two words the similarity measure just retrieves the vectors stored for the words and computes the similarity. You need a VectorIndexReader to load them and you cannot change the vectors anymore (e.g. normalize or use some weighting scheme).

LuceneVectorReader are used to create the above vectors from a Lucene index build from a document collection (more about this in the next section). When a similarity measure wants to get a vector for a word, it queries Lucene for the term frequencies of that word in each document. This is more flexible, as we can do all sorts of weighting and normalizing, but it is also much, much slower.

### Build Your Own Index
If you want to create your own index to be used with ESA, you can do so with a few simple steps:

You can find an out-of-the-box solution for the ESA index creation in the Maven module `de.tudarmstadt.ukp.similarity.dkpro.vsm-asl` in the class EsaIndexer.
In the method `createLuceneWikipediaIndex()`, you need to specify where your document collection can be found. As a prerequisite, you need to have an SQL dump of Wikipedia ready, or any other (domain-specific) document collection which is formatted accordingly.
Make sure you set the right Language property throughout this method.
The EsaIndexer can then be run without any arguments. However, if you process a large document collection such as Wikipedia, you may want to increase the maxiumum Java heap size in the run configuration's VM arguments (`-Xmx<Size>`).


## Lexical Semantic Resources for Word Aggregation Measures
There are a number of word similarity measures which compute similarity based on their distance in a lexical-semantic resource graph, e.g. WordNet. By applying an aggregation strategy such as the one by Mihalcea et al. (2006), these scores are then aggregated to the document level. While WordNet is a classic example, we also constructed graphs on Wiktionary. Note: These resource graphs are different from the vector indexes above!

The resource graphs can be downloaded here: Wiktionary and WordNet.

After the download has finished, unzip the whole folder into `$DKPRO_HOME/LexSemResources/<subdir>`, where subdir is an arbitrary name for each resource, e.g. "wordnet".

Please note that these resources have been created on lemmatized texts. So please also lemmatize your input texts first, before passing them to the similarity measure.

For these resources, you further need to create the folder `$DKPRO_HOME/de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory`. Copy the `resources.xml` here. Then edit the XML file and set the absolute paths for the beans wordnet-en and wiktionary-en according to your system.

For WordNet, please also edit the file `wordnet_properties.xml` in `$DKPRO_HOME/LexSemResources/<wordnet>/`. Here, set the dictionary_path at the end of the file according to your system.

## Models for Lexical Substitution
The lexical substitution system based on supervised word sense disambiguation (Biemann, 2012) automatically provides substitutions for a set of about 1,000 frequent English nouns with high precision.

In order to use this system, download the word models here, and extract it to $DKPRO_HOME/TWSI2. In a final step, edit $DKPRO_HOME/TWSI2/conf/TWSI2_config.conf and set the correct absolute path for mainDir.