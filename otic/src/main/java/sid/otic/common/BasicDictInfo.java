package sid.otic.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is designed to store a lightweight version of the dictionary data, in particular the collection of tuplas: 
 * <lexical entry(URI), lemma, part of speech(URI), translated lexical entry(URI)>
 * This collection of tuplas is implemented as a HashMap using the source lexical entry as key and the rest of fields as the value 
 * (the latter contained in an object of the class lexicalEntryInfo). The HashMap comes along two attributes to indicate source and 
 * target languages respectively. The URIs will be treated as Strings.
 * 
 * The purpose of this class it to allow in memory storage of the dictionary data for a fast querying, as an alternative to 
 * the SPARQL-based methods
 *   
 * @author JGR
 *
 */
public class BasicDictInfo {
	
	
	
	private String sourceLang;
	private String targetLang;
	private HashMap <String, LexicalEntryInfo> hashLexicalEntriesInfo; // associates a lexical entry its lemma, POS and translated lexical entry
	private HashMap <String, ArrayList<String>> hashSourceLemmaInfo; // associates a source lemma with its associated list of lexical entry URIs
	private HashMap <String, ArrayList<String>> hashTargetLemmaInfo; // associates a source lemma with its associated list of lexical entry URIs
	
	// constructor
	public BasicDictInfo (ArrayList<TranslationPair> tps, String sourceLang, String targetLang){
		
		this.hashLexicalEntriesInfo =  loadHashLexicalEntriesInfo(tps);
		this.sourceLang = sourceLang;
		this.targetLang = targetLang;
		this.hashSourceLemmaInfo = loadHashSourceLemmaInfo(tps); 
		this.hashTargetLemmaInfo = loadHashTargetLemmaInfo(tps);
	}
	
	public String getSourceLang() { return this.sourceLang;}
	public String getTargetLang() { return this.targetLang;}
	public HashMap <String, LexicalEntryInfo> getHashLexicalEntriesInfo() {return this.hashLexicalEntriesInfo;}
	public HashMap <String, ArrayList<String>> getHashSourceLemmaInfo() {return this.hashSourceLemmaInfo;}
	public HashMap <String, ArrayList<String>> getHashTargetLemmaInfo() {return this.hashTargetLemmaInfo;}
	
	//extract the relevant info from the list of translation pairs to create the HashMaps
	protected HashMap <String, LexicalEntryInfo> loadHashLexicalEntriesInfo(ArrayList<TranslationPair> tplist) {
		
		HashMap<String, LexicalEntryInfo> hashLexicalEntriesInfo = new HashMap<String, LexicalEntryInfo>();
		
		//Store source lexical entries 
		for (TranslationPair tp: tplist) {			
			String lexicalEntry = tp.getSourceLexicaEntry().toString();
			if (!hashLexicalEntriesInfo.containsKey(lexicalEntry)){
				ArrayList<String> translatedLEs = new ArrayList<String>() ;
				// add first occurrence of a translated lexical entry (it might be more, that will be treated in the else block)
				translatedLEs.add(tp.getTargetLexicaEntry().toString());
				LexicalEntryInfo leInfo = new LexicalEntryInfo(tp.getSourceLemma(), tp.getPos().toString(), translatedLEs);
				// create <key, value> pair in the hash map for the lexical entry
				hashLexicalEntriesInfo.put(lexicalEntry, leInfo);
			}
			else {
				// if it exists, we update the list of translations
				hashLexicalEntriesInfo.get(lexicalEntry).getTranslatedLexicalEntries().add(tp.getTargetLexicaEntry().toString());
			}
		}
		
		//Store target lexical entries 
		for (TranslationPair tp: tplist) {			
			String lexicalEntry = tp.getTargetLexicaEntry().toString();
			if (!hashLexicalEntriesInfo.containsKey(lexicalEntry)){
				ArrayList<String> translatedLEs = new ArrayList<String>() ;
				// add first occurrence of a translated lexical entry (it might be more, that will be treated in the else)
				translatedLEs.add(tp.getSourceLexicaEntry().toString());
				LexicalEntryInfo leInfo = new LexicalEntryInfo(tp.getTargetLemma(), tp.getPos().toString(), translatedLEs);
				// create <key, value> pair in the hash map for the lexical entry
				hashLexicalEntriesInfo.put(lexicalEntry, leInfo);
			}
			else {
				// if it exists, we update the list of translations
				hashLexicalEntriesInfo.get(lexicalEntry).getTranslatedLexicalEntries().add(tp.getSourceLexicaEntry().toString());
			}
		}
		
		return hashLexicalEntriesInfo;
	}
		
	protected HashMap<String, ArrayList<String>> loadHashSourceLemmaInfo (ArrayList<TranslationPair> tplist) {
		
		HashMap<String, ArrayList<String>> hashLemmaInfo = new HashMap<String, ArrayList<String>>();
		
		//Store source lexical entries 
		 for (TranslationPair tp: tplist) {			
			String lemma = tp.getSourceLemma();
			if (!hashLemmaInfo.containsKey(lemma)){
				ArrayList<String> lexicalEntries = new ArrayList<String>();
				// add first occurrence of a lexical entry (it might be more, that will be treated in the else block)
				lexicalEntries.add(tp.getSourceLexicaEntry().toString());
				// create <key, value> pair in the hash map for the lemma info
				hashLemmaInfo.put(lemma, lexicalEntries);
			}
			else {
				// if it exists, we update the list of lexical entries
				hashLemmaInfo.get(lemma).add(tp.getSourceLexicaEntry().toString());
			}
		}	
		 
		return hashLemmaInfo;
 		
	}

	protected HashMap<String, ArrayList<String>> loadHashTargetLemmaInfo (ArrayList<TranslationPair> tplist) {
		
		HashMap<String, ArrayList<String>> hashLemmaInfo = new HashMap<String, ArrayList<String>>();
		
		//Store target lexical entries 
		 for (TranslationPair tp: tplist) {			
			String lemma = tp.getTargetLemma();
			if (!hashLemmaInfo.containsKey(lemma)){
				ArrayList<String> lexicalEntries = new ArrayList<String>();
				// add first occurrence of a lexical entry (it might be more, that will be treated in the else block)
				lexicalEntries.add(tp.getTargetLexicaEntry().toString());
				// create <key, value> pair in the hash map for the lemma info
				hashLemmaInfo.put(lemma, lexicalEntries);
			}
			else {
				// if it exists, we update the list of lexical entries
				hashLemmaInfo.get(lemma).add(tp.getSourceLexicaEntry().toString());
			}
		}	
		 
		 
		return hashLemmaInfo;
			 
		
	}

	

}
