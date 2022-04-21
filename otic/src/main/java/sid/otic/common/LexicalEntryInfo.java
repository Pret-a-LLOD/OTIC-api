package sid.otic.common;

import java.util.ArrayList;

class LexicalEntryInfo{
	
	String lemma = "";
	String pos = "";
	ArrayList<String> translatedLexicalEntries = new ArrayList<String>() ;

	protected LexicalEntryInfo (String lemma, String pos, ArrayList<String> transLEs) {
		
		this.lemma = lemma;
		this.pos = pos;
		this.translatedLexicalEntries = transLEs;
	
	}

	String getLemma() {
		return this.lemma;
	}
	
	String getPos() {
		return this.pos;
	}
	
	ArrayList<String> getTranslatedLexicalEntries() {
		return this.translatedLexicalEntries;
	}
	
}