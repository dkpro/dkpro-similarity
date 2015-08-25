---
layout: page-fullwidth
title: "Getting Started"
permalink: "/gettingstarted/"
---

## Step 1: Maven Setup
This is a Maven project and makes use of some libraries that are not readily available in public Maven repositories. Therefore we have set up a Maven repository which provides these libraries as well as releases of this project.

In order to set up your Eclipse installation properly and use this repository, we ask you to follow these instructions.

## Step 2: Create a new Project
In Eclipse, choose to create new a Maven Project.
On the first page of the properties dialog, tick Create a simple project (skip archetype selection).
On the next page, choose a Group ID and an Artifact ID for your project. We suggest to use com.yourdomain.yourname as the Group ID and com.yourdomain.yourname.sandbox as the Artifact ID.
Set the parent project to de.tudarmstadt.ukp.dkpro.core (Group ID), dkpro-parent-pom (Artifact ID), in the latest version (2 or above)
Select this one, leave all other options on default and click Finish.
In case you run into problems (i.e. dependencies are not resolvable), try adding these lines to your pom.xml:

{% highlight xml %}
<repositories>
<repository>
<id>ukp-oss-releases</id>
<url>http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-releases</url>
</repository>
<repository>
<id>ukp-oss-snapshots</id>
<url>http://zoidberg.ukp.informatik.tu-darmstadt.de/artifactory/public-snapshots</url>
</repository>
</repositories>
{% endhighlight xml %}


## Step 3: Add the Dependencies
In your newly created project, open the pom.xml file with the Maven POM editor.
On the Dependencies tab, choose Add next to the Dependencies list. Search for similarity.algorithms.api-asl and add de.tudarmstadt.ukp.similarity.algorithms.api-asl from the result list. Make sure that in the dependency properties section, the type is set to an empty string (which corresponds to the default and means: JAR), and the scope is compile.
In case the above step doesn't work (as the search index may not be up-to-date), edit the pom.xml with a plain text editor. Add the following child to the dependencies element:

{% highlight xml %}
<dependency>
<groupId>dkpro.similarity</groupId>
<artifactId>dkpro.similarity.algorithms.api-asl</artifactId>
<version>2.1.0</version>
<scope>compile</scope>
</dependency>
{% endhighlight xml %}

With other Maven modules which you intend to use, repeat these steps. These may be, for example, string-based relatedness measures in the module *.algorithms.lexical-asl, or measures based on lexical-semantic resources such as WordNet in the module *.algorithms.lsr-asl.


## Step 4: Additional Resources
Several measures in DKPro Similarity require additional resources, e.g. WordNet. Continue reading [here](/dkpro-similarity/settinguptheresources) for details on how to obtain and install them.