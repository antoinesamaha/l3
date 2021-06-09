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

import java.awt.Component;

import b01.foc.FocLangKeys;
import b01.foc.MultiLanguage;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.list.*;
import b01.foc.property.*;

/**
 * @author 01Barmaja
 */
public class CurrencyDate extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public CurrencyDate(FocConstructor constr) {
    super(constr);

    FDate date = new FDate(this, FLD_DATE, new java.sql.Date(0)) ;
    
    FocList rateList = new FocList(this, getRatesLink(), null);
    FList pRateList = new FList(this, FLD_RATE_LIST, rateList);    
  }

  public java.sql.Date getDate(){
    FDate pDate = (FDate) getFocProperty(FLD_DATE);
    return pDate != null ? pDate.getDate() : null;
  }
  
  public FocList getRateList(){
    FList list = (FList) getFocProperty(FLD_RATE_LIST);
    return list != null ? list.getList() : null;
  }

  public CurrencyRate getCurrencyRate(Currency curr){
    CurrencyRate currRate = null;
    FocList rl = getRateList();
    return (CurrencyRate) rl.searchByPropertyObjectValue(CurrencyRate.FLD_CURRENCY, curr);
  }

  public double getCurrencyRateValue(Currency curr){
    CurrencyRate currRate = getCurrencyRate(curr);
    return currRate != null && currRate.getRateProperty() != null ? currRate.getRateProperty().getDouble() : 0;
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel(); 
    
    Component comp = getGuiComponent(FLD_DATE);
    comp.setEnabled(false);
    panel.add(MultiLanguage.getString(FocLangKeys.CURR_DATE), comp, 0, 0);

    FocList list = getRateList();
    FPanel ratesPanel = CurrencyRate.newBrowsePanel(list, 0);
    
    panel.add(ratesPanel, 0, 1, 2, 1);
    
    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static int COL_DATE =  1; 
  
  private static FocList focList = null;
  
  public static FocList getList(int mode){
    if(focList == null){
      FocLink link = new FocLinkSimple(focDesc);
      focList = new FocList(link);

      FocListOrder listOrder = new FocListOrder();
      listOrder.addField(FFieldPath.newFieldPath(FLD_DATE));
      focList.setListOrder(listOrder);
    }
    
    if(mode == FocList.FORCE_RELOAD){
      focList.reloadFromDB();
      Currencies.reconstructCurrMap();
    }else if(mode == FocList.LOAD_IF_NEEDED){
      if(focList.loadIfNotLoadedFromDB()){
        Currencies.reconstructCurrMap();
      }
    }

    return focList;
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FocDesc desc = getFocDesc();
    FPanel panel = new FPanel();
    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
        list = getList(FocList.LOAD_IF_NEEDED);
      }
      list.setDirectImpactOnDatabase(false);

      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();
      
      FField currField = null;
      FTableColumn col = null;

      col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_DATE), COL_DATE, MultiLanguage.getString(FocLangKeys.CURR_DATE), true);
      tableView.addColumn(col);

      selectionPanel.construct();

      selectionPanel.setDirectlyEditable(true);
      
      FGCurrentItemPanel dateValuesContainer = new FGCurrentItemPanel(selectionPanel, 0);
      panel.add(selectionPanel, 0, 0);
      panel.add(dateValuesContainer, 1, 0);

      FValidationPanel savePanel = panel.showValidationPanel(true);
      if (savePanel != null) {
        list.setFatherSubject(null);
        savePanel.addSubject(list);
      }

      selectionPanel.requestFocusOnCurrentItem();
      selectionPanel.showEditButton(false);
      selectionPanel.setFrameTitle(MultiLanguage.getString(FocLangKeys.CURR_RATES));
    }
    
    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final int FLD_DATE = 1;
  public static final int FLD_RATE_LIST = 2;
  
  private static FocLink ratesLink = null;
  
  private static FocLink getRatesLink() {
    if (ratesLink == null) {
      ratesLink = new FocLinkOne2N(getFocDesc(), CurrencyRate.getFocDesc());
    }
    return ratesLink;
  }  
  
  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(CurrencyDate.class, FocDesc.DB_RESIDENT, "CURR_DATES", false);

      focFld = focDesc.addReferenceField();

      focFld = new FDateField("DATE", "Date", FLD_DATE, true);
      focDesc.addField(focFld);
      
      focFld = new FListField("RATES", "Rates", FLD_RATE_LIST, getRatesLink());
      focDesc.addField(focFld);
    }
    return focDesc;
  }
}
