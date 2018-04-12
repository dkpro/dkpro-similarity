---
layout: page-fullwidth
title: "Documentation"
permalink: "/documentation/"
---

{% assign stable = (site.data.releases | where:"status", "stable" | first) %}
{% assign unstable = (site.data.releases | where:"status", "unstable" | first) %}

## Introduction

* [Getting started guide](/dkpro-similarity/gettingstarted)
* [SemEval 2013](/dkpro-similarity/semeval2013)
* [Setting up the resources](/dkpro-similarity/settinguptheresources)
* [Mailing lists](/dkpro-similarity/mailinglists)
* [Word pair similarity](/dkpro-similarity/wordpairsimilarity)
