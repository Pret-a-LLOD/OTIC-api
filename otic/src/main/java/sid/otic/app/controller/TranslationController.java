package sid.otic.app.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
//import otic.tutorial.controller.UrlEndpoint;
//import otic.tutorial.common.OneTimeInverseConsultationV2;
//import otic.tutorial.common.TranslatablePair;
//import otic.tutorial.controller.RequestBody;
//import otic.tutorial.controller.ResponseBody;
//import otic.tutorial.controller.UrlEndpoint;
import sid.otic.common.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.ArrayList;

@RestController
@Tag(name = "translation")
public class TranslationController {
	
	static OneTimeInverseConsultation_FromArrays otic = new OneTimeInverseConsultation_FromArrays();
	
	@Operation(description = "Given a source, pivot, and target languages, returns a list of inferred TranslatablePair instances for the given entry in the source language. This assumes that Apertium RDF is stored in the following SPARQL endpoint: http://dbserver.acoli.cs.uni-frankfurt.de:5005/ds/query. For a different SPARQL endpoint, try the call 'translationPost' instead  (RECOMMENDED).")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "An array of TranslatablePair instances") })
	@RequestMapping(path = "/translation/{source}-{pivot}-{target}/{entry}", method = RequestMethod.GET)
	public ArrayList<TranslatablePair>  translationsFromEntry (
			@Parameter(example = "en") @PathVariable(value = "source") String source,
			@Parameter(example = "es") @PathVariable (value = "pivot") String pivot, 
			@Parameter(example = "fr") @PathVariable (value = "target") String target, 
			@Parameter(example = "dog") @PathVariable(value = "entry") String entry) {
		OneTimeInverseConsultationV2 otic = new OneTimeInverseConsultationV2();
		ArrayList<TranslatablePair> translatablePairs = otic.obtainTranslationScoresFromLemma(entry, source, pivot, target);
		return translatablePairs;
	}
		
	@Operation(description = "Given a source, pivot, and target languages, returns an inferred dictionary in the form of a list of TranslatablePair instances. This assumes that Apertium RDF is stored in the following SPARQL endpoint: http://dbserver.acoli.cs.uni-frankfurt.de:5005/ds/query. For a different SPARQL endpoint, try the call 'translationPost' instead  (RECOMMENDED). ")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "An array of TranslatablePair instances") })
	@RequestMapping(path = "/translation/{source}-{pivot}-{target}", method = RequestMethod.GET)
	public ArrayList<TranslatablePair> inferredBilingualDictionary (
			@Parameter(example = "en") @PathVariable(value = "source") String source, 
			@Parameter(example = "es") @PathVariable (value = "pivot") String pivot, 
			@Parameter(example = "fr") @PathVariable (value = "target") String target) {
		String sparqlEndpoint = "http://dbserver.acoli.cs.uni-frankfurt.de:5005/ds/query";
		IndirectTranslationsExperimentV2 myExperiment = new IndirectTranslationsExperimentV2();
		String output = "OTIC_" + source + "-" + pivot + "-" + target + "_APv2.tsv";
		ArrayList<TranslatablePair> translatablePairs = myExperiment.createTranslatablePairsWithScoreFile(sparqlEndpoint, source , pivot, target, output);
		return translatablePairs;
		
	}
	
	@Operation(description = "Stores in memory a given dictionary received as a single String (following the TIAD format, see https://tiad2020.unizar.es/task.html 'shortcut')")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dictionary upploaded in memory") })
		@RequestMapping(path = "/uploadDict/{source}-{target}", method = RequestMethod.POST)
		//@RequestMapping(path = "/uploadDict/{source}-{target}/{dict}", method = RequestMethod.GET)
		@ResponseBody
		public String  uploadDict (
				@Parameter(example = "en") @PathVariable(value = "source") String source,
				//@Parameter(example = "es") @PathVariable (value = "pivot") String pivot, 
				@Parameter(example = "es") @PathVariable (value = "target") String target, 
				//@Parameter(example = "dict") @PathVariable(value = "dict") String dict
				@RequestBody String dict 
				){
	
		System.out.println("Starting uploadDict..." );
		System.out.println("Num of stored dicts: " + otic.numStoredDicts());
			
			ArrayList<TranslationPair> tps = PreprocessInputData.parseStringIntoTranslationPairs(dict, source, target);
			
			System.out.println("check content of dictionary");
			for (TranslationPair tp : tps) {
				tp.print();
			}
			
			BasicDictInfo bd = new BasicDictInfo(tps, source, target);
			otic.addDictInMemory(bd);
			

		System.out.println("Num of stored dicts: " + otic.numStoredDicts());
			
			return "finished";
	}

	
	@Operation(description = "Given a source, pivot, and target languages, returns a list of inferred TranslatablePair instances for the given entry in the source language. This call will use the dictionaries stored in memory")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "An array of TranslatablePair instances") })
	@RequestMapping(path = "/translationFromMem/{source}-{pivot}-{target}/{entry}", method = RequestMethod.GET)
	public ArrayList<TranslatablePair>  translationsFromMem (
			@Parameter(example = "en") @PathVariable(value = "source") String source,
			@Parameter(example = "es") @PathVariable (value = "pivot") String pivot, 
			@Parameter(example = "fr") @PathVariable (value = "target") String target, 
			@Parameter(example = "book") @PathVariable(value = "entry") String entry) {
		
		System.out.println("Starting translationFromMem..." );
		System.out.println("Num of stored dicts: " + otic.numStoredDicts());
		
		ArrayList<TranslatablePair> translatablePairs = otic.obtainTranslationScoresFromLemma(entry, source, pivot, target);
		
		return translatablePairs;
	}
		

	
	//igual pero con POST
	@Operation(description = "Given a source, pivot, and target languages, and an SPARQL Endpoint returns a list of inferred TranslatablePair instances for the given entry in the source language")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "An array of TranslatablePair instances") })
		@RequestMapping(path = "/translationPost/{source}-{pivot}-{target}/{entry}", method = RequestMethod.POST)
		@ResponseBody
		public ArrayList<TranslatablePair>  translationsFromEntryPost (
				@Parameter(example = "en") @PathVariable(value = "source") String source,
				@Parameter(example = "es") @PathVariable (value = "pivot") String pivot, 
				@Parameter(example = "fr") @PathVariable (value = "target") String target, 
				@Parameter(example = "dog") @PathVariable(value = "entry") String entry,
				@RequestBody UrlEndpoint urlEndpoint ){
	
	OneTimeInverseConsultationV2 otic = new OneTimeInverseConsultationV2(urlEndpoint.getUrlEndpoint());
	ArrayList<TranslatablePair> translatablePairs = otic.obtainTranslationScoresFromLemma(entry, source, pivot, target);
	return translatablePairs;
}
	

	
	
	// extra class to allow JSON based communication (Springboot does the magic)
	static class UrlEndpoint{
		public String urlEndpoint ;
			
		public String getUrlEndpoint() {
			return urlEndpoint;
		}

		public void setUrlEndpoint(String urlEndpoint) {
			this.urlEndpoint = urlEndpoint;
		}

	}

}



	