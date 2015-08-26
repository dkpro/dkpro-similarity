---
layout: page-fullwidth
title: "Documentation"
permalink: "/documentation/"
---

{% assign stable = (site.data.releases | where:"status", "stable" | first) %}
{% assign unstable = (site.data.releases | where:"status", "unstable" | first) %}

## Introduction

* [Getting started guide](/dkpro-similarity/gettingstarted)
* [SemEval 2013](/dkpro-similarity/semeval2013)
* [Setting up the resources](/dkpro-similarity/settinguptheresources)
* [Mailing lists](/dkpro-similarity/mailinglists)
* [Word pair similarity](/dkpro-similarity/wordpairsimilarity)


## Reference Documentation

{% unless stable.version == null %}
### {{ site.title }} {{ stable.version }}
_latest release_

{% unless stable.user_guide_url == null %}* [User Guide]({{ stable.user_guide_url }}){% endunless %}
{% unless stable.developer_guide_url == null %}* [Developer Guide]({{ stable.developer_guide_url }}){% endunless %}
{% endunless %}


{% unless unstable.version == null %}
### {{ site.title }} {{ unstable.version }}
_upcoming release - links may be temporarily broken while a build is in progress_

{% unless unstable.user_guide_url == null %}* [User Guide]({{ unstable.user_guide_url }}){% endunless %}
{% unless unstable.developer_guide_url == null %}* [Developer Guide]({{ unstable.developer_guide_url }}){% endunless %}
{% endunless %}
