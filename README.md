# OTIC Rest API

This service executes the **"One Time Inverse Consultation"** algorithm to obtain translation pairs between two languages in order to get indirect translations.

This method has been used as baseline in the [TIAD task](http://tiad2019.unizar.es/) (Translation inference across dictionaries) in 2019, showing good results in comparison with the participants.

The One Time Inverse Consultation (OTIC) method was proposed by  Tanaka and Umemura [1] in 1994, and adapted by Lin et. al [2] for the  creation of multilingual lexicons. In short, the idea of the OTIC method  is to explore, for a given word, the possible candidate translations  that can be obtained through intermediate translations in the pivot  language. Then, a score is assigned to each candidate translation based on the degree of overlap between the pivot translations shared by both the source and target words.

The data on which this OTIC Rest API relies to infer new translations is the new RDF version of the Apertium family of dictionaries [3] . 

Currently, the **REST Api** has the following endpoints:

1. `GET /translation/{source}-{pivot}-{target}/{entry}` , e.g. `/translation/en-es-fr/dog`.  Given a source, pivot, and target languages, returns a list of inferred TranslatablePair instances for the given entry in the source language. This service assumes that Apertium RDF is stored in the following SPARQL endpoint: http://dbserver.acoli.cs.uni-frankfurt.de:5005/ds/query. For a different SPARQL endpoint, try the call `translationPost` instead (recommended).
2. `GET /translation/{source}-{pivot}-{target}`, e.g. `/translation/en-es-fr`. Get a full bilingual dictionary between source and target languages, inferred through the given pivot language, in the form of a list of TranslatablePair instances. This process can take several hours to finish. This process assumes that Apertium RDF is stored in the following SPARQL endpoint: http://dbserver.acoli.cs.uni-frankfurt.de:5005/ds/query. For a different SPARQL endpoint, try the call `translationPost` instead (recommended).
3. `POST /translation/{source}-{pivot}-{target}/{entry}`, e.g. `/translation/en-es-fr/dog` request body: `"urlEndpoint": "http://localhost:3030/apertium_tdb2/sparql"`  Given a source, pivot, and target languages, and a SPARQL Endpoint, returns a list of inferred TranslatablePair instances for the given entry in the source language.
4. `POST /uploadDict/{source}-{target}` , e.g. `/translation/en-es-fr`. Stores in memory a given dictionary received as a single String in the request body (following the TIAD format, see 'shortcut' at https://tiad2020.unizar.es/task.html) 
5. `GET /translationFromMem/{source}-{pivot}-{target}/{entry}`, e.g. `/translation/en-es-fr/dog`. Given a source, pivot, and target languages, returns a list of inferred TranslatablePair instances for the given entry in the source language. This call will use the dictionaries previously stored in memory through the service `uploadDict`, therefore not needing a SPARQL endpoint and giving a much faster response than the `translation` services above.  


Basically, this OTIC implementation has two operating modes: 
1. Through a **SPARQL endpoint**, for which a pre-condition is that a triple store with an accessible SPARQL endpoint has been already loaded with the latest Apertium RDF data. To that end, the `translation` services (1, 2, and 3) can be used.
2. By loading the relevant dictionaries **in memory** and then computing OTIC based on them. To that end, several calls to the `uploadDict` service need to be done first (e.g., to load the `en-es`and `es-fr`dictionaries) and then a call to the `translationFromMem` service, e.g. to translate a word from `en` into `fr` using `es` as a pivot. *This mode is primarily intended to integrate OTIC with other LD and NLP services in a Teanga workflow (https://pret-a-llod.github.io/teanga/)*.  



For more details, please check the Swagger documentation at the path `/swagger-ui.html.` 
Example Curl command for endpoint 1: `curl -X 'GET' 'http://localhost:8001/translation/en-es-fr/dog' -H 'accept: */* `




## Installation 

Requirements: To run this API locally, please ensure that you have Java and the build automation tool Maven installed. 

To install the project locally, follow these steps: 

1. In the `/otic` directory, run the command `mvn install` . 
2. Once the project has been successfully built, run in the same directory the command `java -jar target/otic-0.0.1-SNAPSHOT.jar` to start the server.
3. Access the API at http://localhost:8080 . 

As an alternative, there is a Docker image at https://hub.docker.com/r/pretallod/link-otic . 

## References

[1]: K. Tanaka and K. Umemura. "Construction of a bilingual dictionary intermediated by a third language". In COLING, pages 297–303, 1994.

[2]: L. T. Lim, B. Ranaivo-Malançon, and E. K. Tang. "Low cost construction of a multilingual lexicon from bilingual lists". Polibits, 43:45–51, 2011. 

[3]: Gracia, J., Fäth, C., Hartung, M., Ionov, M., Bosque-Gil, J., Veríssimo, S., ... & Orlikowski, M. (2020, November). Leveraging Linguistic Linked Data for Cross-Lingual Model Transfer in the Pharmaceutical Domain. In International Semantic Web Conference (pp. 499-514). Springer, Cham.

## License

OTIC-api was developed by the SID group (University of Zaragoza).

>    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

>    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

>    You should have received a copy of the GNU General Public License
    along with this program.  If not, see [http://www.gnu.org/licenses/].
