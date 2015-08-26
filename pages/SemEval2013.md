---
layout: page-fullwidth
title: "SemEval2013"
permalink: "/semeval2013/"
---

This page is intended for all participants of the shared task of the [*SEM 2013 conference](http://ixa2.si.ehu.es/sts/). We describe one of the task's offical baseline systems, which is roughly the system ranked best in the [SemEval-2012 exercises](http://ixa2.si.ehu.es/starsem/proc/pdf/STARSEM-SEMEVAL051.pdf).

## System Description
The system described here uses a simple log-linear regression model, trained on the training data, to combine multiple text similarity measures of varying complexity. These range from simple character and word n-grams and common subsequences to complex features such as Explicit Semantic Analysis vector comparisons and aggregation of word similarity based on lexical-semantic resources. Our ﬁnal models, one per dataset, consist of a log-linear combination of about 20 features. For details, please refer to our system description paper.

The system presented here differs from the original implementation at SemEval-2012 in that it does not use a distributional thesaurus and only a single text expansion mechanism (lexical substitution only, no statistical machine translation), as the corresponding code and the accompanying resources cannot be readily made available to the public.

The performance of the presented system for the 2012 data is:

| Mode      | ALL       | Mean      | MSRpar    | MSRvid        | SMTeuroparl       | OnWN      | SMTnews   |
| --------- |----------:|----------:|----------:|--------------:|------------------:|----------:|----------:|
|Train      | .863      | .742      | .688      | .801      | .736                  | -         | -         |
|Test       | .694      | .584      | .620      | .808      | .376                  | .657      | .462      |

If you plan to refer to our original system in your publications, please cite

> Daniel Bär, Chris Biemann, Iryna Gurevych, and Torsten Zesch. *UKP: Computing Semantic Textual Similarity by Combining Multiple Content Similarity Measures*, in _Proceedings of the 6th International Workshop on Semantic Evaluation, in conjunction with the 1st Joint Conference on Lexical and Computational Semantics_, pages 435-440, June 2012, Montreal, Canada. (pdf)](http://www.aclweb.org/anthology/S12-1059) [(bib)](http://www.aclweb.org/anthology/S12-1059.bib)


## Installation Instructions
The presented system is implemented as part of DKPro Similarity, a collection of software components for similarity computation between texts of any length, which is intended to be used in conjunction with DKPro Core and Apache UIMA.

### Step 1: Maven Setup
The project is implemented as a Java Maven project. It makes use of some libraries that are not readily available in public Maven repositories. Therefore we have set up a Maven repository which provides these libraries as well as releases of this project.

In order to set up your Eclipse installation properly, we ask you to follow [these instructions](https://dkpro.github.io/dkpro-core/pages/setup-user.html) ("Eclipse Setup") where you find details on all required plugins. You also need to make sure that you have set up our open source Maven repository properly (instructions; "Configuring Maven for full access to the UKP Maven Repository").

### Step 2: Checkout
Next, check out the project ("Check out as Maven project" in Eclipse). It is enough to check out only the module `trunk/de.tudarmstadt.ukp.similarity.experiments.sts-2013-baseline-gpl`, which resides in the GPL variant of the similarity package. [Need help with the checkout?](https://zoidberg.ukp.informatik.tu-darmstadt.de/jenkins/job/DKPro%20Core%20Documentation%20(GitHub)/de.tudarmstadt.ukp.dkpro.core$de.tudarmstadt.ukp.dkpro.core.doc-asl/doclinks/2/)

### Step 3: Run
You should now be able to run the system. Therefore, execute the Pipeline with the `-D` program argument, to start in training mode.

### Extend the system
We explicitly encourage you to extend the system. New similarity measures can be added with ease by subclassing TextSimilarityMeasureBase. We added an example for a custom measure called `MyTextSimilarityMeasure` to the `*.example` package, and added the corresponding calls to the `FeatureGeneration` class.

#### Add a custom similarity measure
In general, a new text similarity measure can be added by doing two things: (a) setting up a concrete class for implementing the measure, which subclasses TextSimilarityMeasureBase, and (b) adding a wrapper class which subclasses TextSimilarityResourceBase which allows to use the measure in a DKPro language processing pipeline.

Please refer to the reference implementations in the *.example package to learn more about how to use them. For a first glance, we also list the code for the two classes below.

{% highlight xml %}
public class MyTextSimilarityMeasure
extends TextSimilarityMeasureBase
{
@SuppressWarnings("unused")
private int n;

public MyTextSimilarityMeasure(int n)
{
// The configuration parameter is not used right now and intended for illustration purposes only.
this.n = n;
}

@Override
public double getSimilarity(Collection<String> stringList1,
Collection<String> stringList2)
throws SimilarityException
{
// Your similarity computation goes here.
return 1.0;
}
}
public class MyTextSimilarityResource
extends TextSimilarityResourceBase
{
public static final String PARAM_N = "N";
@ConfigurationParameter(name=PARAM_N, mandatory=true)
private int n;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public boolean initialize(ResourceSpecifier specifier, Map additionalParams)
throws ResourceInitializationException
{
if (!super.initialize(specifier, additionalParams)) {
return false;
}

this.mode = TextSimilarityResourceMode.list;

measure = new MyTextSimilarityMeasure(n);

return true;
}
}
{% endhighlight xml %}


#### Use a different classifier
In case you do not want to use a Linear Regression classifier, but experiment with your own, please go to the `*.util.Evaluator` class, and just modify the line

{% highlight xml %}
Classifier baseClassifier = new LinearRegression();
{% endhighlight xml %}


to suit your needs. [Weka](Weka) is already available through the Maven dependencies, so feel free to experiment with any other classifier from that package.