# OTIC Rest API

This service executes the **"One Time Inverse Consultation"** algorithm to obtain translation pairs between two languages in order to get indirect translations. The results provide the following information:

> source_label	target_label	part_of_speech	score

This method has been used as baseline in the [TIAD task](http://tiad2019.unizar.es/) (Translation inference across dictionaries) in 2019, showing good results in comparison with the participants.

The One Time Inverse Consultation (OTIC) method was proposed by  Tanaka and Umemura [1] in 1994, and adapted by Lin et. al [2] for the  creation of multilingual lexicons. In short, the idea of the OTIC method  is to explore, for a given word, the possible candidate translations  that can be obtained through intermediate translations in the pivot  language. Then, a score is assigned to each candidate translation based on the degree of overlap between the pivot translations shared by both 
the source and target words.

Currently, the RESTA Api allows to 

1. get an indirect translation from a given lexical entry by specifying the language codes of source, pivot and target languages, 
2. get a full bilingual dictionary generated from the execution of the algorithm with a given source, pivot and target languages.

For more details, please check the Swagger documentation at the path `/swagger-ui.html.`

The data on which OTIC Rest API relies to infer new translations is the new RDF version of the Apertium family of dictionaries [3] . 

## References

[1]: K. Tanaka and K. Umemura. "Construction of a bilingual dictionary intermediated by a third language". In COLING, pages 297–303, 1994.

[2]: L. T. Lim, B. Ranaivo-Malançon, and E. K. Tang. "Low cost construction of a multilingual lexicon from bilingual lists". Polibits, 43:45–51, 2011. 

[3]: Gracia, J., Fäth, C., Hartung, M., Ionov, M., Bosque-Gil, J., Veríssimo, S., ... & Orlikowski, M. (2020, November). Leveraging Linguistic Linked Data for Cross-Lingual Model Transfer in the Pharmaceutical Domain. In International Semantic Web Conference (pp. 499-514). Springer, Cham.