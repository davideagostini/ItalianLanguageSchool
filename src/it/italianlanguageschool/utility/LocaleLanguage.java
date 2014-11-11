package it.italianlanguageschool.utility;

import java.util.Locale;

public class LocaleLanguage {
	
	int lang;
	
	public LocaleLanguage() {
		this.lang = setLanguage();
	}
	
	public int setLanguage() {
		String language = Locale.getDefault().getLanguage().toString();
		int var = 2;
		
		if(language.equals("it")) var = 1;
		else if(language.equals("en")) var = 2;
		else if(language.equals("es")) var = 3;
		else if(language.equals("de")) var = 4;
		else if(language.equals("fr")) var = 5;
		
		return var;	
	}

	public int getLang() {
		return lang;
	}

}
