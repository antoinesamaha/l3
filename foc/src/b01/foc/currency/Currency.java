// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION
// RATES ARRAY

/*
 * Created on 01-Feb-2005
 */
package b01.foc.currency;

import java.sql.Date;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.event.FValidationListener;
import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.list.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class Currency extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public Currency(FocConstructor constr) {
    super(constr);

    FString name = new FString(this, FLD_NAME, "") ;
  }

  public String getName(){
    FProperty str = getFocProperty(FLD_NAME);
    return str.getString();
  }

  public static void printDebug2(FocList list){
    Globals.logString("");
    Globals.logString("Currency list");
    for(int i=0; i<list.size(); i++){
      Currency curr = (Currency) list.getFocObject(i);
      Globals.logString(curr.getReference().getInteger()+" "+curr.getName()+" "+curr.getReference());
    }
    Globals.logString("");
  }
  
  public static void printDebug(){
    FocList list = getList(false);
    printDebug2(list);
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel();
    FGTextField comp = (FGTextField) getGuiComponent(FLD_NAME);
    comp.setEnabled(false);
    comp.setColumns(4);
    panel.add(comp, 0, 0);
    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static final int COL_NAME = 1;
  
  private static FocList focList = null;

  public static final int VIEW_CURRENCY_N2N_SELECTION_PANEL = 2;
  
  public static FocList createList(){
    FocLink link = new FocLinkSimple(focDesc);
    FocList focList = new FocList(link);

    FocListOrder listOrder = new FocListOrder();
    listOrder.addField(FFieldPath.newFieldPath(FLD_NAME));
    focList.setListOrder(listOrder);
    
    return focList;
  }
  
  public static FocList getList(boolean doNotTryLoading){
    if(focList == null){
      focList = createList();
    }
    
    if(!doNotTryLoading){
      if(focList.loadIfNotLoadedFromDB()){
        DateLine.resetDescription();
      }
    }
    
    return focList;
  }

  public static void reloadList(){
    FocList list = getList(false);
    list.reloadFromDB();
    DateLine.resetDescription();
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FocDesc desc = getFocDesc();
    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
        list = createList();
        list.loadIfNotLoadedFromDB();
      }
      if (list != null) {
        list.setDirectImpactOnDatabase(false);

        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        
        FField currField = null;
        FTableColumn col = null;

        if(viewID == VIEW_CURRENCY_N2N_SELECTION_PANEL){
          tableView.addSelectionColumn();
        }
        
        currField = desc.getFieldByID(FLD_NAME);
        col = new FTableColumn(desc, FFieldPath.newFieldPath(currField.getID()), COL_NAME, MultiLanguage.getString(FocLangKeys.CURR_CURRENCY), 10, true);
        tableView.addColumn(col);

        selectionPanel.construct();

        //selectionPanel.getTable().setTableMinMax(50, 150, 500, 150);
        selectionPanel.getTable().setTableHeightByIndex(1);
        
        if(viewID == VIEW_CURRENCY_N2N_SELECTION_PANEL){
          selectionPanel.setDirectlyEditable(false);
          selectionPanel.showEditButton(false);
          selectionPanel.showModificationButtons(false);
        }else{
          selectionPanel.setDirectlyEditable(true);

          /*
          Currencies currencies = Currencies.getCurrencies();
          FPanel datesPanel = currencies.newDetailsPanel(0);
          mainPanel.add(datesPanel, 0, 0);
          */
          
          FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
          if (savePanel != null) {
            //list.setFatherSubject(null);
            savePanel.addSubject(list);
            //savePanel.addSubject(currencies);
            savePanel.setValidationListener(new FValidationListener(){
              public boolean proceedValidation(FValidationPanel panel) {
                return true;
              }
              public boolean proceedCancelation(FValidationPanel panel) {
                return true;
              }
              public void postValidation(FValidationPanel panel) {
                /*
                FocList list = getList();
                list.reloadFromDB();
                DateLine.resetDescription();
                */
                reloadList();
              }
              public void postCancelation(FValidationPanel panel) {
                /*
                FocList list = getList();
                list.reloadFromDB();
                DateLine.resetDescription();
                */
                reloadList();
              }
            });
          }
          
          selectionPanel.requestFocusOnCurrentItem();
          selectionPanel.showEditButton(false);
        }
      }
    }
    selectionPanel.setFrameTitle(MultiLanguage.getString(FocLangKeys.CURR_CURRENCY));
    
    return selectionPanel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final int FLD_NAME = 1;
  
  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(Currency.class, FocDesc.DB_RESIDENT, "CURR", false);

      focFld = focDesc.addReferenceField();

      focFld = new FCharField("NAME", "Name", FLD_NAME, true, 3);
      focFld.setLockValueAfterCreation(true);
      focFld.setMandatory(true);
      focDesc.addField(focFld);
    }
    return focDesc;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // RATES ARRAY
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private double[] ratesByDate = null;
  
  private double[] getRatesByDateArray(){
    if(ratesByDate == null){
      reconstructRatesArray();
    }
    return ratesByDate;
  }
  
  public void reconstructRatesArray(){
    try{
      Currencies currencies = Currencies.getCurrencies();
      FocList currDateList = CurrencyDate.getList(FocList.LOAD_IF_NEEDED);
  
      java.sql.Date firstDate = currencies.getFirstDate();
      java.sql.Date lastDate = Globals.getApp().getSystemDate();
      long deltaTime = lastDate.getTime() - firstDate.getTime();
      int nbOfDays = (int)(deltaTime / Globals.DAY_TIME);
      nbOfDays += 1; 
      
      ratesByDate = new double[nbOfDays];      
      java.sql.Date zDate = (java.sql.Date) firstDate.clone();
      double lastValidRate = 0;
      
      int lstIdx = 0;
      for(int dayIndex=0; dayIndex<nbOfDays ;dayIndex++){
        CurrencyDate currDate = (CurrencyDate) currDateList.getFocObject(lstIdx);
        if(currDate != null){
          Date iDdate = currDate.getDate();
          if(iDdate.compareTo(zDate) == 0){
            lstIdx++;
            double d = currDate.getCurrencyRateValue(this);
            if(d != 0){
              lastValidRate = d;                
            }
          }
        }
        ratesByDate[dayIndex] = lastValidRate;
        DateLine.incrementDate(zDate);
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public double getRate(java.sql.Date date){
    double d = 0;
    Currencies currencies = Currencies.getCurrencies();
    if(this == currencies.getBaseCurrency()){
      d = 1;
    }else{
      double[] ratesByDate = getRatesByDateArray();
      if(ratesByDate != null){
        java.sql.Date firstDate = currencies.getFirstDate();
        int dayIndex = (int) ((date.getTime() - firstDate.getTime()) / Globals.DAY_TIME);
        d = dayIndex < ratesByDate.length && dayIndex >= 0? ratesByDate[dayIndex] : 0;
      }else{
        Globals.logString("no rate for currency "+getName());
      }
    }
    
    return d;
  }
}
