package sid.otic.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class OneTimeInverseConsultation_FromArrays {

	// list of dictionaries stores in memory
	private ArrayList<BasicDictInfo> storedDicts = new ArrayList<BasicDictInfo>();
	
	public void addDictInMemory(BasicDictInfo dict) {
		
	   this.storedDicts.add(dict);
		
	}
	
	
	/**
	 * Given a lemma in a source language, it returns their possible translations into a target language through a given pivot language,
	 * along with their score, in an array of TranslatablePair objects. The scores are obtained based on the "One Time Single Consultation" algorithm
	 * @param sourceLemma
	 * @param sourceLanguage
	 * @param pivotLanguage
	 * @param targetLanguage
	 * @return translatable pairs
	 */
	public ArrayList<TranslatablePair> obtainTranslationScoresFromLemma(
			String sourceLemma, 
			String sourceLanguage, 
			String pivotLanguage, 
			String targetLanguage){
		ArrayList<TranslatablePair> translatablePairs = new ArrayList<TranslatablePair>();		
		
		//Obtain in-memory dictionary from source and pivot languages, and from pivot and target languages 
		BasicDictInfo dict1 = null, dict2 = null; 
		
		for (BasicDictInfo dict: this.storedDicts) { 
			
			if ((dict.getSourceLang() == sourceLanguage) && (dict.getTargetLang() == pivotLanguage) ||
				(dict.getTargetLang() == sourceLanguage) && (dict.getSourceLang() == pivotLanguage))	
					dict1 = dict;
			if ((dict.getSourceLang() == pivotLanguage) && (dict.getTargetLang() == targetLanguage) ||
				(dict.getTargetLang() == pivotLanguage) && (dict.getSourceLang() == targetLanguage))	
					dict2 = dict;
		}
		if ((dict1 == null) || (dict2 == null)) 
			System.out.println("Dictionaries not found in list of in-memory dictionaries");
		
		
		// Get Lexical entries associated to source label
		ArrayList<String> sourceLexicalEntries = null;
		if (sourceLanguage == dict1.getSourceLang())  // e.g., EN in an EN-ES dictionary
			sourceLexicalEntries = dict1.getHashSourceLemmaInfo().get(sourceLemma);
		else if (sourceLanguage == dict1.getTargetLang()) // e.g., ES in an EN-ES dictionary
			sourceLexicalEntries = dict1.getHashTargetLemmaInfo().get(sourceLemma);
			
			
		
		//ArrayList<String> sourceLexicalEntries = SPARQLSearchesV2.obtainLexicalEntriesFromLemma(sourceLexicon, sourceLemma, sourceLanguage);		
		
		for (String sourceLexicalEntry:sourceLexicalEntries)
			translatablePairs.addAll(obtainTranslationScoresFromLexicalEntry(sourceLemma, 
					sourceLexicalEntry, 
					dict1, 
					dict2));
	
		
				
		for (String sourceLexicalEntry:sourceLexicalEntries)
			translatablePairs.addAll(obtainTranslationScoresFromLexicalEntry(sourceLemma, 
					sourceLexicalEntry, 
					dict1, 
					dict2));
		return translatablePairs;
	}
	
	
	
	/**
	 * Given a lexical entry (and its associated lemma) in a source language, it returns its possible translations into a target language through a given pivot language,
	 * along with their score, in an array of TranslatablePair objects. The scores are obtained based on the "One Time Single Consultation" algorithm
	 * 
	 * @param sourceLemma
	 * @param sourceLexicalEntry
	 * @param dictSourceToPivot basic lexical entry info from source language into pivot language
	 * @param dictPivotToTarget basic lexical entry info from pivot language into target language
	 * @return translatable pairs with scores
	 */
	public  ArrayList<TranslatablePair> obtainTranslationScoresFromLexicalEntry(
			String sourceLemma, 
			String sourceLexicalEntry, 
			BasicDictInfo dictSourceToPivot,  
			BasicDictInfo dictPivotToTarget){
		
		ArrayList<TranslatablePair> translatablePairs = new ArrayList<TranslatablePair>(); // to collect the output
		Set<String> P1 = new HashSet<String>();  // to store translations in the pivot language (P1)
		Set<String> T = new HashSet<String>();  //  to store translations in the target language (T)
		
		// 1. For the source lexical entry, look up all translations in the pivot language (P1).			
		//get translations (lexical entries) for the source lexical entry
		ArrayList<String> pivotTranslations = dictSourceToPivot.getHashLexicalEntriesInfo ().get(sourceLexicalEntry).getTranslatedLexicalEntries();
		
		//populate set of pivot translations (P1)
		P1.addAll(pivotTranslations);

		// 2. For every pivot translation of every source lexical entry, look up its target translations (T).
		for (String pivotTranslation: pivotTranslations){			
			
			ArrayList<String> newTargetTranslations = dictPivotToTarget.getHashLexicalEntriesInfo().get(pivotTranslation).getTranslatedLexicalEntries();
		
			newTargetTranslations.removeAll(T); //retains only those translations that are really new ones			
	
			//Create the array of translatable pairs leaving the scores empty for the moment
			for (String targetTranslation: newTargetTranslations){				
	
				String pos = dictPivotToTarget.getHashLexicalEntriesInfo().get(targetTranslation).getPos();
				String targetLemma = dictPivotToTarget.getHashLexicalEntriesInfo().get(targetTranslation).getLemma();
				
				translatablePairs.add(new TranslatablePair(sourceLemma, sourceLexicalEntry.toString(), targetTranslation.toString(), targetLemma, pos.toString(), -1.0));				
				T.addAll(newTargetTranslations);
				
			}
		}
			
		// 3. For every target translation, look up its pivot translations (P2)
		for (TranslatablePair ts:translatablePairs){	
				Set<String> P2 = new HashSet<String>();				
				
				P2.addAll(dictPivotToTarget.getHashLexicalEntriesInfo().get(ts.getTargetLexicalEntry()).getTranslatedLexicalEntries());
				
				// 4. Measure how translations in P2 match those in P1. For each t in T, the more matches 
				// between P1 and P2, the better t is as a candidate translation of the original
				// Formula: 	score(t) = 2 * (P1 ∩ P2)/P1+P2      
				Set<String> intersection = new HashSet<String>();
				intersection.addAll(P1);
				intersection.retainAll(P2);
				double score = 2.0 * intersection.size()/(P1.size() + P2.size());				
				// add all the information in the translation score object				
				ts.setScore(score);
				P2.clear();
		}		
		return translatablePairs;
	}
	
	public void printTranslationstoFile(String outputFile, ArrayList<TranslatablePair> translatablePairs) {
		try {
			PrintWriter writer = new PrintWriter(outputFile);			
			for (TranslatablePair ts: translatablePairs){
				ts.print();
				ts.printToFile(writer);
			}			
			writer.close();
		}catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}
	
	
	//uncomment for testing
	public static void main(String[] args) { 
		//String outputFile = "OTIC_trans-PRUEBA.tsv";
		//OneTimeInverseConsultation_FromArrays otic = new OneTimeInverseConsultation_FromArrays();
		//ArrayList<TranslatablePair> translatablePairs = otic.obtainTranslationScoresFromLemma("table", "en", "es", "fr");
		//otic.printTranslationstoFile(outputFile, translatablePairs);
		
		
		OneTimeInverseConsultation_FromArrays otic = new OneTimeInverseConsultation_FromArrays();
		
		
		// Emulate input strings (dictionaries)
		String input1 = "";
		String input2 = "";
		
		// get the input string from a test file
		try {
			input1 = new String(Files.readAllBytes(Paths.get("data/test_EN-ES.tsv")));
			input2 = new String(Files.readAllBytes(Paths.get("data/test_FR-ES.tsv")));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
		ArrayList<TranslationPair> tps1 = PreprocessInputData.parseStringIntoTranslationPairs(input1, "en", "es");
		ArrayList<TranslationPair> tps2 = PreprocessInputData.parseStringIntoTranslationPairs(input2, "fr", "es");
		
		BasicDictInfo bd1 = new BasicDictInfo(tps1, "en", "es");
		otic.addDictInMemory(bd1);
		
		BasicDictInfo bd2 = new BasicDictInfo(tps2, "fr", "es");
		otic.addDictInMemory(bd2);
		
		ArrayList<TranslatablePair> translatablePairs = otic.obtainTranslationScoresFromLemma("book", "en", "es", "fr");
		for (TranslatablePair tp : translatablePairs) {
			tp.print();
		}
		
	}

}
