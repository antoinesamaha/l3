/*
 * Created on 09-Jun-2005
 */
package b01.foc;

import b01.foc.desc.field.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class MultiLanguage {
  private static ArrayList<Language> languageList = null;
  
  private static Language currentLanguage = null;
  
  public static int DEFAULT_LANGUAGE = 0;
  
  private static ArrayList<Language> getLanguageList(){
    if(languageList == null){
      languageList = new ArrayList<Language>();
    }
    return languageList;
  }
  
  public static void addLanguage(Language language){
    getLanguageList().add(language);
  }

  public static Language getCurrentLanguage(){
    if(languageList == null){
      Language english = new Language(DEFAULT_LANGUAGE, "English", Locale.UK);      
      addLanguage(english);
    }
    
    if(currentLanguage == null){
      ArrayList array = getLanguageList();
      currentLanguage = (Language) array.get(0); 
    }
    return currentLanguage;
  }
  
  public static void setCurrentLanguage(int langID){
    ArrayList array = getLanguageList();
    for(int i=0; i<array.size(); i++){
      Language language = (Language) array.get(i);
      if(langID == language.getId()){
        currentLanguage = language;
      }
    }
  }

  public static Locale getCurrentLocale(){
    Language lang = getCurrentLanguage();
    return lang.getLocale();
  }
  
  public static boolean isMultiLanguage(){
    return getLanguageNumber() > 1;
  }
  
  public static int getLanguageNumber(){
    return languageList != null ? languageList.size() : 1;
  }
  
  public static Language getLanguageAt(int i){
    return languageList != null ? (Language)languageList.get(i) : null;
  }
  
  public static void fillMutipleChoices(FMultipleChoiceField multiFld){
    ArrayList array = (ArrayList) getLanguageList();
    if(array != null){
      for(int i=0; i<array.size(); i++){
        Language lang = (Language)array.get(i);
        multiFld.addChoice(lang.getId(), lang.getName());
      }
    }
  }

  // --------------------
  
  public static String getString(Language language, String bundle, String key){
    String str = key;
    if(key != null && language != null){
      str = language.getString(bundle, key);
    }
    return str;
  }
  
  public static int getMnemonic(Language language, String bundle, String key){
    int mnemonic = -9;
    if(key != null && language != null){
      mnemonic = language.getMnemonic(bundle, key);
    }
    return mnemonic;
  }

  public static String getToolTipText(Language language, String bundle, String key){
    String toolTipText = null;
    if(key != null && language != null){
      toolTipText = language.getToolTipText(bundle, key);
    }
    return toolTipText;
  }
  
  // --------------------
  
  public static String getString(String bundle, String key){
    return getString(getCurrentLanguage(), bundle, key);
  }
  
  public static int getMnemonic(String bundle, String key){
    return getMnemonic(getCurrentLanguage(), bundle, key);
  }

  public static String getToolTipText(String bundle, String key){
    return getToolTipText(getCurrentLanguage(), bundle, key);
  }
  
  // --------------------
  
  public static String getString(LanguageKey langKey){
    return langKey != null ? getString(getCurrentLanguage(), langKey.getBundle(), langKey.getKey()) : null;
  }
  
  public static int getMnemonic(LanguageKey langKey){
    return langKey != null ? getMnemonic(getCurrentLanguage(), langKey.getBundle(), langKey.getKey()) : -9;
  }

  public static String getToolTipText(LanguageKey langKey){
    return langKey != null ? getToolTipText(getCurrentLanguage(), langKey.getBundle(), langKey.getKey()) : null;
  }
  
  // --------------------
  
  public static String getString(Language language, LanguageKey langKey){
    return langKey != null ? getString(language, langKey.getBundle(), langKey.getKey()) : null;
  }
  
  public static int getMnemonic(Language language, LanguageKey langKey){
    return langKey != null ? getMnemonic(language, langKey.getBundle(), langKey.getKey()) : -9;
  }

  public static String getToolTipText(Language language, LanguageKey langKey){
    return langKey != null ? getToolTipText(language, langKey.getBundle(), langKey.getKey()) : null;
  }
  
  // --------------------
  
  private static FFieldArrayMultilangPlug plug = null; 
  
  public static FFieldArrayMultilangPlug getFieldArrayPlug(){
    if(plug == null){
      plug = new FFieldArrayMultilangPlug();
    }
    return plug;
  }
}
