/*
 * Created on 22-May-2005
 */
package b01.foc.menu;

import b01.foc.*;

/**
 * @author 01Barmaja
 */
public abstract class FMenu {
  private LanguageKey langKey = null;
  private String title = null;
  private int mnemonic = -1;
  private boolean enabled = true;
  private String code = null;
  
  public abstract boolean isList();

  //rr Begin
  public FMenu(LanguageKey langKey){
    /*this.langKey = langKey;*/
    this(langKey, null);
  }

  public FMenu(String title, int mnemonic){
    /*this.title = title;
    this.mnemonic = mnemonic;*/
    this(title, mnemonic, null);
  }
  
  public FMenu(String title, int mnemonic, String code){
    this.title = title;
    this.mnemonic = mnemonic; 
    this.code = code;
  }
  
  public FMenu(LanguageKey langKey, String code){
    this.langKey = langKey;
    this.code = code;
  }
  //rr End
  
  public String getTitle() {
    return (title != null) ? title : MultiLanguage.getString(langKey); 
  }

  public int getMnemonic() {
    return (title != null) ? mnemonic : MultiLanguage.getMnemonic(langKey); 
  }
  
  public void setTitle(String title){
  	this.title = title;
  }
  
  public void setMnemonic(int mnemonic){
  	this.mnemonic = mnemonic;
  }
  
  public void setEnabled(boolean enabled){
  	this.enabled = enabled;
  }
  
  public boolean isEnabled(){
  	return this.enabled;
  }

  public String getCode() {
    return code;
  }
}
