// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// DESCRIPTION
// LIST

/*
 * Created on 01-Feb-2005
 */
package b01.foc.currency;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import b01.foc.*;
import b01.foc.admin.FocGroup;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.event.*;
import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.list.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class DateLine extends FocObject {
  
  private boolean isNewlyAdded = false ;
    
  public static void incrementDate(java.sql.Date date){
    date.setTime(date.getTime() + Globals.DAY_TIME);
  }

  public static void addDate(ArrayList list, java.sql.Date date){
    java.sql.Date newDate = (java.sql.Date) date.clone();
    list.add(newDate);
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public DateLine(FocConstructor constr) {
    super(constr);
    FocConstructor dateConstr = new FocConstructor(CurrencyDate.getFocDesc(), constr, null);
    FObject date = new FObject(this, FLD_CURR_DATE, dateConstr.newItem()) ;
    
    FDouble rate = new FDouble(this, FLD_CURR_RATE+1, 0) ;
    FPropertyArray propArray = new FPropertyArray(this, FLD_CURR_RATE, rate) ;
  }

  public boolean isNewlyAdded() {
    return isNewlyAdded;
  }
  public void setNewlyAdded(boolean isNewlyAdded) {
    this.isNewlyAdded = isNewlyAdded;
  }
  
  public CurrencyDate getCurrencyDate(){
    FObject currDate = (FObject) getFocProperty(FLD_CURR_DATE);
    return (CurrencyDate) currDate.getObject();
  }
  
  public FDate getDateProperty(){
    CurrencyDate currencyDate = getCurrencyDate();
    return (FDate) currencyDate.getFocProperty(CurrencyDate.FLD_DATE);
  }
  
  public void fillFromCurrencyDate(CurrencyDate currDate){
    FObject pCurrDate = (FObject) getFocProperty(FLD_CURR_DATE);
    pCurrDate.setObject(currDate);
    FocList rateList = currDate.getRateList();       
    
    for(int i=0; i<nbOfCurrencies; i++){
      Currency curr = currencyOrderArray[i];
      CurrencyRate currRate = (CurrencyRate) rateList.searchByPropertyObjectValue(CurrencyRate.FLD_CURRENCY, curr);
      if(currRate != null){
        FDouble rateValue = currRate.getRateProperty();
        FDouble displayRateValue = (FDouble) getFocProperty(FLD_CURR_RATE+1+i);
        displayRateValue.setDouble(rateValue.getDouble());
        //if(group == null || !group.allowCurrencyRateModif()) displayRateValue.setValueLocked(true);
      }
    }
  }

  public void fillCurrencyDate(){
    FObject pCurrDate = (FObject) getFocProperty(FLD_CURR_DATE);
    CurrencyDate currDate = (CurrencyDate) pCurrDate.getObject();
    FocList rateList = currDate.getRateList();
    boolean atLeastOneNotZeroRate = false;
    boolean[] ratesIsNewlyAddedArray = new boolean[nbOfCurrencies];    
    
    for(int i=0; i<nbOfCurrencies; i++){
      Currency curr = currencyOrderArray[i];
      CurrencyRate currRate = (CurrencyRate) rateList.searchByPropertyObjectValue(CurrencyRate.FLD_CURRENCY, curr);
      if(currRate == null){
        currRate = (CurrencyRate) rateList.newEmptyItem();
        currRate.setCurrency(curr);
        rateList.add(currRate);
        ratesIsNewlyAddedArray[i] = true;
      }else{
        ratesIsNewlyAddedArray[i] = false;
      }
     
      FDouble rateValue = currRate.getRateProperty();
      FDouble displayRateValue = (FDouble) getFocProperty(FLD_CURR_RATE+1+i);
      
      double rate1 = displayRateValue.getDouble();
      double rate2 = rateValue.getDouble();
      
      //ratesIsNewlyAddedArray[i] = ratesIsNewlyAddedArray[i] && rate1 != 0;
      atLeastOneNotZeroRate = atLeastOneNotZeroRate || rate1 != 0;
      if(rate1 != rate2 && rate1 != 0){
        rateValue.setDouble(displayRateValue.getDouble());
        
        if(ratesIsNewlyAddedArray[i] || isNewlyAdded()){
          currRate.setModified(false);
          currRate.setCreated(true);
        }else{
          currRate.setModified(true);
          currRate.setCreated(false);
        }
      }else{
        currRate.setModified(false);
        currRate.setCreated(false);
      }

      //currRate.setModified(rate1 != 0);
      //currRate.setCreated(rate1 != 0 && (ratesIsNewlyAddedArray[i] || isNewlyAdded()));
      //currRate.setModified(rate1 != 0);
    }
    
    currDate.setCreated(isNewlyAdded() && atLeastOneNotZeroRate);
    currDate.setModified(false);
    currDate.forceControler(true);
    /*else if(shouldBeInserted){
      currDate.setCreated(false);
      currDate.setModified(true);
      
    }*/
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static final int COL_DATE = 1;  
  public static final int COL_RATE = 2;
  
  public static FocList listCurrentlyOnGUI = null; 
  
  public static FocList getList(){
      
    FocList focList = null;    
    Currencies currencies = Currencies.getCurrencies();
    if(currencies != null && !currencies.hasInitErrorWithMessage("Currency module should be configured first!")){
      FocLink link = new FocLinkSimple(focDesc);
      focList = new FocList(link);
  
      FocListOrder listOrder = new FocListOrder();
      FFieldPath fieldPath = new FFieldPath(2);
      fieldPath.set(0, FLD_CURR_DATE);
      fieldPath.set(1, CurrencyDate.FLD_DATE);
      
      listOrder.addField(fieldPath);
      focList.setListOrder(listOrder);
        
      FocList currDateList = CurrencyDate.getList(FocList.LOAD_IF_NEEDED);
      for(int i=0; i<currDateList.size(); i++){
        CurrencyDate currDate = (CurrencyDate) currDateList.getFocObject(i);
        DateLine line = (DateLine) focList.newEmptyItem();
        line.fillFromCurrencyDate(currDate);
        focList.add(line);
      }
      
      ArrayList datesToBeAdded = new ArrayList();
      Date firstConfigDate = currencies.getFirstDate();
      Date zDate = (Date) firstConfigDate.clone();
      CurrencyDate firstCurrencyDate = (CurrencyDate) currDateList.getFocObject(0);
      CurrencyDate lastCurrencyDate = currDateList.size() > 0 ? (CurrencyDate) currDateList.getFocObject(currDateList.size()-1) : null;
      Calendar calendar = Calendar.getInstance();

      if(firstCurrencyDate != null && lastCurrencyDate != null){
        while(zDate.before(firstCurrencyDate.getDate())){
          addDate(datesToBeAdded, zDate);
          incrementDate(zDate);
        }
        
        int realListIndex = 0;
        while(zDate.before(lastCurrencyDate.getDate())){
          CurrencyDate realCurrencyDate = (CurrencyDate) currDateList.getFocObject(realListIndex);
  
          while(zDate.before(realCurrencyDate.getDate())){
            addDate(datesToBeAdded, zDate);
            incrementDate(zDate);
          }
          
          incrementDate(zDate);
          realListIndex++;
        }
        
        incrementDate(zDate);
      }
      
      while(zDate.before(Globals.getApp().getSystemDate()) || zDate.equals(Globals.getApp().getSystemDate())){
        addDate(datesToBeAdded, zDate);
        incrementDate(zDate);
        
        Globals.logString(zDate.toString());        
      }
      
      for(int i=0; i<datesToBeAdded.size(); i++){
        java.sql.Date dateToAdd = (java.sql.Date) datesToBeAdded.get(i);
        DateLine line = (DateLine) focList.newEmptyItem();

        FDate lineDate = line.getDateProperty();
        lineDate.setDate(dateToAdd);
        focList.add(line);
        line.setNewlyAdded(true);
      }
    }
    
    /*
    Globals.logString("Total getList() :"+(t2 - t1));
    Globals.logString("addding the items :"+(t4 - t3));
    Globals.logString("list.add :"+t7);
    Globals.logString("newItem :"+t10);
    Globals.logString("newInternal :"+t13);
    Globals.logString("FocList.newItem :"+t14);
    */
    return focList;
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FListPanel selectionPanel = null;
    
    Currencies currencies = Currencies.getCurrencies();
    if(!currencies.hasInitErrorWithMessage("Currency module should be configured first!")){
      resetDescription();
      FocDesc focDesc = getFocDesc();
      
      if(currencyOrderArray != null && nbOfCurrencies > 0){
        FocDesc desc = getFocDesc();
        
        if (desc != null) {
          if(list == null){
            list = getList();
          }
          if (list != null) {
            listCurrentlyOnGUI = list;
            list.setDirectImpactOnDatabase(false);
    
            selectionPanel = new FListPanel(list);
            FTableView tableView = selectionPanel.getTableView();
            
            FField currField = null;
            FTableColumn col = null;
            FTableColumn dateCol = null;
    
            FFieldPath fieldPath = new FFieldPath(2);
            fieldPath.set(0, FLD_CURR_DATE);
            fieldPath.set(1, CurrencyDate.FLD_DATE);
            
            dateCol = new FTableColumn(desc, fieldPath, COL_DATE, MultiLanguage.getString(FocLangKeys.CURR_DATE), 12, false);
            tableView.addColumn(dateCol);
            
            Currency baseCurr = currencies.getBaseCurrency();
            
            FocGroup group = Globals.getApp().getGroup();
            boolean allowEdit = group != null && group.allowCurrencyRateModif(); 
            for(int n=0; n<nbOfCurrencies; n++){
              Currency currency = currencyOrderArray[n];
              col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_CURR_RATE+n+1), COL_RATE+n+1, currency.getName()+"/"+baseCurr.getName(), allowEdit);
              tableView.addColumn(col);
            }
                    
            selectionPanel.construct();
    
            selectionPanel.setDirectlyEditable(true);
            dateCol.setEditable(false);
            
            FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
            savePanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
            if (savePanel != null) {
              savePanel.setValidationListener(new FValidationListener(){
    
                public boolean proceedValidation(FValidationPanel panel) {
                  FocList list = listCurrentlyOnGUI;
                  for(int i=0; i<list.size(); i++){
                    DateLine dateLine = (DateLine) list.getFocObject(i);
                    if(dateLine != null){
                      dateLine.fillCurrencyDate();
                      dateLine.getCurrencyDate().validate(false);
                    }
                  }

                  CurrencyDate.getList(FocList.FORCE_RELOAD);

                  /*
                  CurrencyDate.getList().forceControler(true);
                  CurrencyDate.getList().validate();
                  */
                  
                  listCurrentlyOnGUI = null;
                  return true;
                }
    
                public boolean proceedCancelation(FValidationPanel panel) {
                  listCurrentlyOnGUI = null;
                  return true;
                }

                public void postValidation(FValidationPanel panel) {
                }

                public void postCancelation(FValidationPanel panel) {
                }
                
              });
            }
    
            selectionPanel.requestFocusOnCurrentItem();
            selectionPanel.showEditButton(false);
            selectionPanel.showModificationButtons(false);
          }
        }
      }
    }
    selectionPanel.setMainPanelSising(FPanel.FILL_VERTICAL);
    return selectionPanel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  private static int nbOfCurrencies = 0;
  private static Currency[] currencyOrderArray = null;
  
  public static final int FLD_CURR_DATE = 1;
  public static final int FLD_CURR_RATE = 2;

  public static void resetDescription(){
    Globals.logString("! RESETING DESCRIPTION FOR DATELINE!");
    focDesc = null;
    nbOfCurrencies = 0;
    currencyOrderArray = null;
  }
  
  public static FocDesc getFocDesc() {
    FocList currList = Currency.getList(false);
    int localNbOfCurrencies = currList != null ? currList.size() - 1: 0;
    
    if (focDesc == null || localNbOfCurrencies != nbOfCurrencies) {
      nbOfCurrencies = localNbOfCurrencies;
      currencyOrderArray = new Currency[nbOfCurrencies];
      
      Globals.logString("! NEW DESCRIPTION FOR DATELINE!");
      Currency.printDebug();
      
      Currencies currencies = Currencies.getCurrencies();
      int index = 0;
      for(int i=0; i<currList.size(); i++){
        Currency curr = (Currency) currList.getFocObject(i);
        if(curr == null){
          Globals.logString("! CURR == null !");
        }
        
        if(currencies.getBaseCurrency().getReference().getInteger() != curr.getReference().getInteger()){
          currencyOrderArray[index++] = curr;
        }else{
          Globals.logString("curr == basicCurr: "+curr.getDebugInfo());
        }
      }
      
      Globals.logString("! End of curr array index= "+index+" !");
      FField focFld = null;
      focDesc = new FocDesc(DateLine.class, FocDesc.NOT_DB_RESIDENT, "CURR", false);

      focFld = focDesc.addReferenceField();

      focFld = new FObjectField("CUR_DATE", "Currency date", FLD_CURR_DATE, true, CurrencyDate.getFocDesc(), "DATE_");
      focDesc.addField(focFld);
      
      FFieldArrayPlug fieldArrayPlug = new FFieldArrayPlug(){
        public int getCurrentIndex(){
          return MultiLanguage.getCurrentLanguage().getId();    
        }
      };
      
      if(nbOfCurrencies > 0){
        focFld = new FNumField("RATE", "Rate", FLD_CURR_RATE+1, false, 7, 2);
        FFieldArray focFldArray = new FFieldArray(focFld, nbOfCurrencies, fieldArrayPlug);
        focDesc.addFieldArray(focFldArray);
      }
    }
    return focDesc;
  }
  
}
