package sid.otic.common;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class PreprocessInputData {

	static String FIELD_SEP = "\t";
	static String LINE_SEP = "\n"; //System.lineSeparator();//"\n"; // 
	
	/**
	 * The method that receives a String that codifies a set of translations that follows the TIAD format, tab separated:
	 * "source written representation" 
	 * "source lexical entry (URI)" 
	 * "source sense (URI)" 
	 * "translation (URI)" 
	 * "target sense (URI)"
	 * "target lexical entry (URI)"
	 * "target written representation"
	 * "part of speech (URI)"
	 * 
	 * Each translation is separated by a "\n" 
	 * The method converts it into an array of translations, for its later processing by the OTIC algorithm. This method is used
	 * when the data of the initial dictionaries is passed through an input String instead of by accessing it via SPARQL endpoint
	 *  
	 * @param input, sourceLang, targetLang
	 * @return
	 */
	public static ArrayList<TranslationPair> parseStringIntoTranslationPairs (String input, String sourceLang, String targetLang) {
		
		
		ArrayList<TranslationPair> translationPairs = new ArrayList<TranslationPair>(); 
		
		// Split in "rows" (translations)
		String[] translations = input.split(LINE_SEP);
		
		// For each translation it split the row in the different fields and create a TranslationPair out of it, added to the array of translation pairs
		for (String tr : translations) {
			
			String trans = tr.replaceAll("\"", ""); // to prevent malformed URIs in case of double quotes
			trans = trans.replaceAll("\r", ""); // to prevent the use of Windows based line separator 
			String[] fields = trans.split(FIELD_SEP);
			TranslationPair tp = new TranslationPair(fields[0], URI.create(fields[1]), URI.create(fields[2]), URI.create(fields[3]), URI.create(fields[4]), URI.create(fields[5]), fields[6], URI.create(fields[7]), sourceLang, targetLang);
			translationPairs.add(tp);
		}
		
		return translationPairs;
		
	}	

	
	//uncomment for testing
	public static void main(String[] args) {
		
		String input = "";
		
		// get the input string from a test file
		try {
			input = new String(Files.readAllBytes(Paths.get("data/test_FR-ES_livre.tsv")));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// print it on screen to inspect the data
		System.out.println(input);
		
		ArrayList<TranslationPair> tps = PreprocessInputData.parseStringIntoTranslationPairs(input, "EN", "ES");
		
		for (TranslationPair tp : tps) {
			tp.print();
		}
	}
	
}
