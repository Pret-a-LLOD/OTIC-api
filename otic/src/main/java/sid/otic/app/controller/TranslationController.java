package sid.otic.app.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import sid.otic.common.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.ArrayList;

@RestController
@Tag(name = "translation")
public class TranslationController {
	
	@Operation(description = "Given a source, pivot, and target languages, returns a list of inferred TranslatablePair instances for the given entry in the source language")
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
		
	@Operation(description = "Given a source, pivot, and target languages, returns an inferred dictionary in the form of a list of TranslatablePair instances")
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
	
	

}



	