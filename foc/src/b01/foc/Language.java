/*
 * Created on 09-Jun-2005
 */
package b01.foc;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class Language {
  private int id = 0;  
  private String name = "";
  private Locale locale = null;
  private HashMap<String, ResourceBundle> resourceBundles = null;
  
  public Language(int id, String name, Locale locale){
    this.id = id;
    this.name = name;
    this.locale = locale;
    resourceBundles = new HashMap<String, ResourceBundle>();
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public ResourceBundle getResourceBundle(String bundleName){
    ResourceBundle bundle = (ResourceBundle)resourceBundles.get(bundleName);
    if(bundle == null){
    	try{
    		bundle = ResourceBundle.getBundle("properties/languages/foc/"+bundleName, locale);
    	}catch(Exception e){
    		//Empty on purpose because the next search will find the bundle.
    	}
      if(bundle == null){
      	bundle = ResourceBundle.getBundle("properties/languages/app/"+bundleName, locale);
      }
      if(bundle != null){
        resourceBundles.put(bundleName, bundle);
      }
    }
    return bundle ;
  }
  
  public String getString(String bundleName, String strKey){
    String str = strKey;
    ResourceBundle resource = getResourceBundle(bundleName);
    resource = getResourceBundle(bundleName);
    if(resource != null){
      str = resource.getString(strKey);
    }
    return str;
  }
  
  public int getMnemonic(String bundleName, String strKey){
    int mnemonic = -1;
    String str = getString(bundleName, strKey+"Mnemonic");
    if(str != null && str.length() == 1){
      char c[] = new char[1];
      str.getChars(0, 1, c, 0);
      mnemonic = (int)c[0];
    }
    return mnemonic;
  }

  public String getToolTipText(String bundleName, String strKey){
    return getString(bundleName, strKey+"TTT");
  }
  
  public Locale getLocale() {
    return locale;
  }
}
