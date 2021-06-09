// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION
// DATE HASHTABLE
// STATIC

/*
 * Created on 01-Feb-2005
 */
package b01.foc.currency;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.event.*;
import b01.foc.gui.*;
import b01.foc.list.*;
import b01.foc.property.*;

import java.awt.*;
import java.util.*;

import javax.swing.*;

/**
 * @author 01Barmaja
 */
public class Currencies extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public Currencies(FocConstructor constr) {
    super(constr);

    new FDate(this, FLD_FIRST_DATE, new java.sql.Date(0)) ;
    
    //FocConstructor curConstr = new FocConstructor(Currency.getFocDesc(), constr, null);
    new FObject(this, FLD_BASE_CURRENCY, null) ;
  }
  
  public Currency getBaseCurrency(){
    FObject baseCurrency = (FObject) getFocProperty(FLD_BASE_CURRENCY);
    return baseCurrency != null ? (Currency) baseCurrency.getObject_CreateIfNeeded() : null;
  }

  public java.sql.Date getFirstDate(){
    FDate firstDate = (FDate) getFocProperty(FLD_FIRST_DATE);
    return firstDate != null ? (java.sql.Date) firstDate.getDate() : null;
  }
  
  public boolean isFirstDateOk(){
    java.sql.Date firstDate = getFirstDate();
    return firstDate.compareTo(nullDate) > 0;
  }
  
  public boolean isBaseCurrencyOk(){
    Currency baseCurrency = getBaseCurrency();
    return baseCurrency.getName().trim().compareTo("") != 0;
  }
  
  public boolean hasInitError(){
    return !isFirstDateOk() || !isBaseCurrencyOk();
  }

  public boolean hasInitErrorWithMessage(String message){
    boolean b = hasInitError();
    if(b){
      JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
          (message != null) ? message : "The multi currency module is not initialized.\nPlease initalize it !",
          "01Barmaja",
          JOptionPane.ERROR_MESSAGE,
          null);
    }
    return b;
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    FPanel panel =new FPanel();
    
    Component comp = getGuiComponent(FLD_FIRST_DATE);
    if(!isCreated()){
      comp.setEnabled(false);
    }
    panel.add(MultiLanguage.getString(FocLangKeys.CURR_FIRST_DATE), comp, 0, 0);
    
    FObjectPanel objPanel = (FObjectPanel) getGuiComponent(FLD_BASE_CURRENCY);
    objPanel.setSelectButtonVisible(isCreated());
    panel.add(MultiLanguage.getString(FocLangKeys.CURR_BASE_CURRENCY), objPanel, 0, 1);
        
    FValidationPanel validPanel = panel.showValidationPanel(true);
    if(validPanel != null){
      validPanel.addSubject(this);
      
      FValidationListener validListener = new FValidationListener(){
        public boolean proceedValidation(FValidationPanel panel) {
          return !hasInitErrorWithMessage(null);
        }

        public boolean proceedCancelation(FValidationPanel panel) {
          hasInitErrorWithMessage(null);
          return true;
        }

        public void postValidation(FValidationPanel panel) {
        }

        public void postCancelation(FValidationPanel panel) {
        }
      };
      validPanel.setValidationListener(validListener);
    }
    
    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocList getList(){
    FocLink link = new FocLinkSimple(focDesc);
    FocList list = new FocList(link);
    list.reloadFromDB();
    
    return list;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final int FLD_FIRST_DATE = 1;
  public static final int FLD_BASE_CURRENCY = 2;

  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(Currencies.class, FocDesc.DB_RESIDENT, "CURR_CONFIG", false);

      focFld = focDesc.addReferenceField();

      focFld = new FDateField("FIRST_DATE", "First date", FLD_FIRST_DATE, true);
      focDesc.addField(focFld);
      //focFld.setLockValueAfterCreation(true);
      
      FObjectField focObjFld = new FObjectField("BASE_CURR", "Base currency", FLD_BASE_CURRENCY, true, Currency.getFocDesc(), "CURR_");
      focObjFld.setSelectionList(Currency.getList(true));
      focObjFld.setDisplayField(Currency.FLD_NAME);
      focDesc.addField(focObjFld);
      //focFld.setLockValueAfterCreation(true);      
    }
    return focDesc;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DATE HASHTABLE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static HashMap currMap = null;
  
  public static void reconstructCurrMap(){
    if(currMap != null){
      currMap.clear();
    }else{
      currMap = new HashMap();  
    }
    
    FocList currList = Currency.getList(true);
    if(currList != null){
      Iterator iter = (Iterator) currList.focObjectIterator();
      while(iter != null && iter.hasNext()){
        Currency curr = (Currency) iter.next();
        if(curr != null){
          currMap.put(curr.getName(), curr);
          curr.reconstructRatesArray();
        }
      }
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // STATIC
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static Currencies currencies = null;
  private static final java.sql.Date nullDate = new java.sql.Date(0);
  
  public static Currencies getCurrencies(){
    Currencies ret = currencies;
    if(ret == null){
      FocList list = getList();
      if(list.size() > 0){
        currencies = (Currencies) list.getFocObject(0);
        ret = currencies;
      }
      if(ret == null){
        FocConstructor constr = new FocConstructor(Currencies.getFocDesc(), null, null);
        ret = (Currencies) constr.newItem();
        ret.setCreated(true);
      }
    }
    return ret;
  }
  
  public static double getRate(java.sql.Date date, Currency curr){
    double rate = 0;
    if(!getCurrencies().hasInitErrorWithMessage(null)){
      if(getCurrencies().getBaseCurrency().getName().compareTo(curr.getName()) != 0){
        rate = curr.getRate(date);        
      }else{
        rate = 1;
      }
    }
    return rate;
  }
  
  public static double getRate(java.sql.Date date, Currency curr1, Currency curr2){
    double rate = 0;
    if(!getCurrencies().hasInitErrorWithMessage(null)){
      if(curr1.getName().compareTo(curr2.getName()) == 0){
        rate = 1;
      }else{
        double r1 = getRate(date, curr1);
        double r2 = getRate(date, curr2);
        
        if(r1 > 0 && r2 > 0){
          rate = r1 / r2;
        }
      }      
    }    
    return rate;
  }
}

