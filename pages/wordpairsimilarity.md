---
layout: page-fullwidth
title: "Word Pair Similarity"
permalink: "/wordpairsimilarity/"
---

## Introduction
DKPro Similarity comes with a ready-made experiment pipeline for evaluating word pair similarity/relatedness. The most common evaluation datasets are already included (see below).

## Datasets
The following datasets are included in the experiment


### English
* Rubenstein & Goodenough (RG65)
    * the classical similarity dataset
﻿   * Rubenstein, H., & Goodenough, J. B. (1965). Contextual Correlates of Synonymy. Communications of the ACM, 8(10), 627-633.
* Miller & Charles (MC30)
    * subset of Rubenstein & Goodenough dataset
    ﻿* Miller, G. A., & Charles, W. G. (1991). Contextual Correlates of Semantic Similarity. Language and Cognitive Processes, 6(1), 1-28.
* Finkelstein et al. (WS353)
    * the full finkelstein dataset, as well as the two parts that were annotated by different groups of annotators
    ﻿* Finkelstein, L., Gabrilovich, E., Matias, Y., Rivlin, E., Solan, Z., & Wolfman, G. (2002). Placing Search in Context: The Concept Revisited. ACM Transactions on Information Systems, 20(1), 116-131.
* Hill et al. 2014 (Sim999)
    * SimLex-999: Evaluating Semantic Models with (Genuine) Similarity Estimation. 2014. Felix Hill, Roi Reichart and Anna Korhonen. Preprint pubslished on arXiv. arXiv:1408.3456
    * semantic similarity
    * http://www.cl.cam.ac.uk/~fh295/simlex.html
* Li et al. (MWE300)
    * multi-word similarity
    * http://adapt.seiee.sjtu.edu.cn/similarity/
* Yang & Powers (YP130)
    * verb similarity
﻿    * Yang, D., & Powers, D. M. W. (2006). Verb Similarity on the Taxonomy of WordNet. Proceedings of the Third International WordNet Conference (GWC-06) (pp. 121-128). Jeju Island, Korea.
* Szumlanski et al. (2013) (SGS130)
    * relatedness dataset
    * Note: leaves/rake has been changed to leaf/rake as the plural is not found in WordNet and most other pairs are singular, too.
    * Szumlanski, S., Gomez, F. & Sims, V. K. (2013). A New Set of Norms for Semantic Relatedness Measures. Proceedings of the 51st Annual Meeting of the Association for Computational Linguistics (Volume 2: Short Papers) (pp. 890-895). Sofia, Bulgaria.

### German
* Translated and re-annotated data for Miller & Charles (Gur30) and Rubenstein & Goodenough (Gur65), as well as 350 cross POS word pairs (Gur350)
    * Gurevych, I. (2005). Using the Structure of a Conceptual Network in Computing Semantic Relatedness. Proceedings of IJCNLP (pp. 767-778).
* 222 German word pairs annotated on the sense level (ZG222)
    * Zesch, T., & Gurevych, I. (2006). Automatically Creating Datasets for Measures of Semantic Relatedness. Proceedings of the Workshop on Linguistic Distances (pp. 16-24). Sydney, Australia

### Arabic, Romanian, Spanish
* Translated and re-annotated data for Miller & Charles and Finkelstein
    * ﻿Hassan, S., & Mihalcea, R. (2009). Cross-lingual Semantic Relatedness Using Encyclopedic Knowledge. Proceedings of the 2009 Conference on Empirical Methods in Natural Language Processing (pp. 1192-1201).


*Is your dataset missing from the list? Contact us and we will be glad to add it.*