// INSTANCE
//    MAIN
//    PANEL
// ERROR LIST
// LIST
// DESCRIPTION
// RATES ARRAY

/*
 * Created on 01-Feb-2005
 */
package b01.foc.cashdesk;

import java.awt.GridBagConstraints;
import java.sql.Date;
import java.util.Iterator;
import java.util.HashMap;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.event.FValidationListener;
import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.list.*;
import b01.foc.property.*;

import b01.foc.currency.Currency;

/**
 * @author 01Barmaja
 */
public class CashMovement extends FocObject {
  
  private static CashMovementExtension extension = null;  
  
  public static boolean hasExtension(){
    return extension != null;
  }
  
  public static void setExtension(CashMovementExtension exten){
    extension = exten;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private boolean editable = true;
  
  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public CashMovement(FocConstructor constr) {
    super(constr);

    FocConstructor cashDescConstr = new FocConstructor(CashDesk.getFocDesc(), constr, this);
    FocConstructor currConstr = new FocConstructor(Currency.getFocDesc(), constr, this);

    FObject cashDesk = new FObject(this, FLD_CASHDESK, cashDescConstr.newItem()) ;
    FDate date = new FDate(this, FLD_DATE, new Date(0)) ;
    FObject currency = new FObject(this, FLD_CURRENCY, currConstr.newItem()) ;
    FDouble ammount = new FDouble(this, FLD_AMMOUNT, 0) ;
    FString descrip = new FString(this, FLD_DESCRIPTION, "") ;
    new FBoolean(this, FLD_IS_LINKED, false);
    
    if(hasExtension()){
      FocConstructor extConstr = new FocConstructor(extension.getFocDesc(), constr, this);
      FInLineObject extensionObject = new FInLineObject(this, FLD_IN_LINE_EXTENSION, extConstr.newItem()) ;
    }
  }

  public CashDesk getCashDesk(){
    FObject str = (FObject)getFocProperty(FLD_CASHDESK);
    return (CashDesk) str.getObject();
  }

  public void setCashDesk(CashDesk cashDesk){
    FObject str = (FObject)getFocProperty(FLD_CASHDESK);
    str.setObject(cashDesk);
  }

  public Date getDate(){
    FDate dateProp = (FDate) getFocProperty(FLD_DATE);
    return dateProp != null ? dateProp.getDate() : null;
  }

  public void setDate(java.sql.Date date){
    FDate dateProp = (FDate) getFocProperty(FLD_DATE);
    if(dateProp != null){
      dateProp.setDate(date);
    }
  }
  
  public Currency getCurrency(){
    FObject str = (FObject)getFocProperty(FLD_CURRENCY);
    return (Currency) str.getObject();
  }

  public void setCurrency(Currency curr){
    FObject str = (FObject)getFocProperty(FLD_CURRENCY);
    str.setObject(curr);
  }
  
  public double getAmmount(){
    FProperty str = (FProperty)getFocProperty(FLD_AMMOUNT);
    return str.getDouble();
  }  

  public void setAmmount(double amm){
    FDouble str = (FDouble)getFocProperty(FLD_AMMOUNT);
    str.setDouble(amm);
  }
  
  public void dispose(){
  	super.dispose();
  }
  
  public String getDescription(){
    FProperty str = (FProperty)getFocProperty(FLD_DESCRIPTION);
    return str.getString();
  }
  
  public void setDescription(String description){
    FProperty str = (FProperty)getFocProperty(FLD_DESCRIPTION);
    str.setString(description);
  }
  
  public boolean isLinked(){
    FBoolean str = (FBoolean)getFocProperty(FLD_IS_LINKED);
    return str.getBoolean();
  }
  
  public void setLinked(boolean isLinked){
    FBoolean str = (FBoolean)getFocProperty(FLD_IS_LINKED);
    str.setBoolean(isLinked);
  }
  
  public FocObject getExtensionObject(){
    FInLineObject inLineExtension = (FInLineObject)getFocProperty(FLD_IN_LINE_EXTENSION);
    return (FocObject)inLineExtension.getObject();
  }
  
  public FocObject getNewExtensionObject(FocObject fatherFocObject){
    FInLineObject thisInLineExtension = (FInLineObject)getFocProperty(FLD_IN_LINE_EXTENSION);
    FInLineObject newInLineExtension = new FInLineObject (fatherFocObject,FLD_IN_LINE_EXTENSION);
    return (FocObject)newInLineExtension.getObject();
    
  }

  public void setExtensionObject(FocObject extensionObj){
    FInLineObject inLineExtension = (FInLineObject)getFocProperty(FLD_IN_LINE_EXTENSION);
    inLineExtension.setObject(extensionObj);
  }

  public boolean isContentValid(boolean displayMessage){
    boolean valid = true;
    Date date = getDate();
    /*
    if(date.compareTo(Globals.getApp().getSystemDate()) != 0){
      valid = false;
      Globals.getApp().getDisplayManager().popupMessage("Date is different then system date");
    }
    */
    return valid;
  }
    
  public boolean isEditable() {
    return editable;
  }
  
  public void setEditable(boolean editable) {
    this.editable = editable;

    if(isLinked()){
      this.editable = false;
    }
    
    FProperty prop = getFocProperty(FLD_AMMOUNT);
    prop.setValueLocked(!this.editable);

    prop = getFocProperty(FLD_CURRENCY);
    prop.setValueLocked(!this.editable);
    
    prop = getFocProperty(FLD_DESCRIPTION);
    prop.setValueLocked(!this.editable);
    
    FocObject extObj = getExtensionObject();
    FocFieldEnum enumer = (FocFieldEnum) extObj.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_DB);
    while(enumer != null && enumer.hasNext()){
      FField field = (FField) enumer.next();
      prop = enumer.getProperty();
      prop.setValueLocked(!this.editable);
    }
    
    enumer = (FocFieldEnum) extObj.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(enumer != null && enumer.hasNext()){
      FField field = (FField) enumer.next();
      prop = enumer.getProperty();
      prop.setValueLocked(!this.editable);
    }
  }
  
  public void setCreated(boolean b){
    if(b){
      super.setCreated(b);
    }else{
      super.setCreated(b);
    }
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel();
    FPanel userLevelPanel = newUserLevelPanel();
    if(userLevelPanel != null){
      panel.add(userLevelPanel, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    }

    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ERROR LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocList createErrorList(){
    //Creating the lists
    FocList allList = createList();
    FocList errorList = createList();
    
    allList.loadIfNotLoadedFromDB();
    
    FocListOrder order = new FocListOrder();
    order.addField(FFieldPath.newFieldPath(CashMovement.FLD_CASHDESK));
    order.addField(FFieldPath.newFieldPath(CashMovement.FLD_CURRENCY));
    order.addField(FFieldPath.newFieldPath(CashMovement.FLD_DATE));

    //Getting the min and last date items 
    FocListOrder orderByDate = new FocListOrder();
    orderByDate.addField(FFieldPath.newFieldPath(CashMovement.FLD_DATE));
    
    allList.setListOrder(orderByDate);

    CashMovement first = allList.size() > 0 ? (CashMovement) allList.getFocObject(0) : null;
    CashMovement last = allList.size() > 0 ? (CashMovement) allList.getFocObject(allList.size() - 1) : null;
    
    allList.setListOrder(order);

    //Creating the CashDesk Map
    HashMap cashDeskMap = new HashMap();
    FocList cashDeskList = CashDesk.createList();
    cashDeskList.loadIfNotLoadedFromDB();
    Iterator iter = cashDeskList.focObjectIterator(); 
    while(iter != null && iter.hasNext()){
      CashDesk cashDesk = (CashDesk) iter.next();
      if(cashDesk != null){
        FocConstructor constr = new FocConstructor(CashDeskViewer.getFocDesc(), null);
        CashDeskViewer cdv = (CashDeskViewer) constr.newItem();
        //cdv.construct(false);
        FProperty cashDeskProperty = cdv.getFocProperty(CashDeskViewer.FLD_CASHDESK);
        boolean isDesactivateListenerBackUp = cashDeskProperty.isDesactivateListeners();
        cashDeskProperty.setDesactivateListeners(true);
        cdv.setCashDesk(cashDesk);
        cashDeskProperty.setDesactivateListeners(isDesactivateListenerBackUp);
        
        cdv.construct(false);
        
        FProperty firstDateProperty = cdv.getFocProperty(CashDeskViewer.FLD_FIRST_DATE);
        isDesactivateListenerBackUp = firstDateProperty.isDesactivateListeners();
        firstDateProperty.setDesactivateListeners(true);
        cdv.setFirstDate(first.getDate());
        firstDateProperty.setDesactivateListeners(isDesactivateListenerBackUp);
        
        FProperty lastDateProperty = cdv.getFocProperty(CashDeskViewer.FLD_LAST_DATE);
        isDesactivateListenerBackUp = lastDateProperty.isDesactivateListeners();
        lastDateProperty.setDesactivateListeners(true);
        cdv.setLastDate(last.getDate());
        lastDateProperty.setDesactivateListeners(isDesactivateListenerBackUp);
        
        cashDeskMap.put(cashDesk.getName(), cdv);
      }
    }

    //Doing the scanning and filling errorList
    CashDeskViewer currentCashDeskViewer = null; 
    CashDeskByCurrencyViewer currentByCurrencyViewer = null;

    iter = allList.focObjectIterator();
    while(iter != null && iter.hasNext()){
      CashMovement cashMove = (CashMovement) iter.next();
      boolean error = false;
      
      //Searching for the CashDeskViewer
      error = cashMove.getCashDesk() == null;   
      if(!error){
        if(currentCashDeskViewer != null && currentCashDeskViewer.getCashDesk() != null && currentCashDeskViewer.getCashDesk().getName().compareTo(cashMove.getCashDesk().getName()) == 0){
          
        }else{
          currentCashDeskViewer = (CashDeskViewer) cashDeskMap.get(cashMove.getCashDesk().getName());           
        }
        error = currentCashDeskViewer == null;
      }
      
      //Searching for the ByCurrencyViewer      
      if(!error){
        if(currentByCurrencyViewer != null && currentByCurrencyViewer.getCurrency() != null && currentByCurrencyViewer.getCurrency().getName().compareTo(cashMove.getCurrency().getName()) == 0){
          
        }else{
          currentByCurrencyViewer = (CashDeskByCurrencyViewer) currentCashDeskViewer.findCashDeskByCurrencyViewer(cashMove.getCurrency());
        }
        error = currentByCurrencyViewer == null;
      }

      //Searching for the date
      if(!error){
        FocList openCloseList = (FocList) currentByCurrencyViewer.getOpenCloseList();
        CashOpenClose cashOpenClose = (CashOpenClose) openCloseList.searchByPropertyDateValue(CashOpenClose.FLD_DATE, cashMove.getDate());
        error = cashOpenClose == null;
        if(!error){
          error = !cashOpenClose.isOpened();
        }
      }

      if(error){
        CashMovement newCashMove = (CashMovement) errorList.newEmptyItem();
        newCashMove.setCashDesk(cashMove.getCashDesk());
        newCashMove.setCurrency(cashMove.getCurrency());
        newCashMove.setAmmount(cashMove.getAmmount());
        newCashMove.setDate(cashMove.getDate());
        newCashMove.setDescription(cashMove.getDescription());
        //newCashMove.setExtensionObject(cashMove.getExtensionObject());
        newCashMove.setExtensionObject(cashMove.getNewExtensionObject(newCashMove));
        newCashMove.lockAllproperties();
        
        errorList.add(newCashMove);
      }
    }
    cashDeskMap = null;
    cashDeskList.dispose();
    cashDeskList = null;
    cashDeskList = null;
    allList.dispose();
    allList = null;
    return errorList;
  }
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static final int COL_CASHDESK = 1;
  public static final int COL_DATE = 2;
  public static final int COL_CURRENCY = 3;
  public static final int COL_AMMOUNT = 4;
  public static final int COL_DESCRIPTION = 5;  
  public static final int COL_IN_LINE_EXTENSION_FIRST = 10;
  
  public static final int BRW_FOR_MOVEMENTS_ERROR_LIST = 2;
  
  private static FocList focList = null;

  public static FocList createList(){
    FocLink link = new FocLinkSimple(focDesc);
    FocList focList = new FocList(link);

    /*
    FocListOrder listOrder = new FocListOrder();
    listOrder.addField(FFieldPath.newFieldPath(FLD_DATE));
    focList.setListOrder(listOrder);
    */
    
    return focList;
  }
  
  public static FocList getList(boolean doNotTryLoading){
    if(focList == null){
      focList = createList();
    }
    if(!doNotTryLoading){
      focList.loadIfNotLoadedFromDB();
    }
    return focList;
  }

  public static void reloadList(){
    FocList list = getList(false);
    list.reloadFromDB();
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
        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        
        FField currField = null;
        FTableColumn col = null;
        
        col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_CASHDESK), COL_CASHDESK, MultiLanguage.getString(FocLangKeys.CASH_CASHDESK), false);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_CURRENCY), COL_CURRENCY, MultiLanguage.getString(FocLangKeys.CURR_CURRENCY), 7,  false);
        tableView.addColumn(col);        

        col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_DATE), COL_DATE, MultiLanguage.getString(FocLangKeys.CASH_DATE), false);
        tableView.addColumn(col);        
        
        col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_AMMOUNT), COL_AMMOUNT, MultiLanguage.getString(FocLangKeys.CASH_AMMOUNT), true);
        tableView.addColumn(col);
        
        col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_DESCRIPTION), COL_DESCRIPTION, MultiLanguage.getString(FocLangKeys.GEN_DESCRIPTION), true);
        tableView.addColumn(col);        

        if(hasExtension()){
          extension.fillTableView(tableView, viewID);
        }
        
        selectionPanel.construct();

        //selectionPanel.setDirectlyEditable(true);
        //selectionPanel.getTable().setTableMinMax(50, 400, 1300, 400);        
        selectionPanel.getTable().setTableHeightByIndex(2);
        /*
        FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
        if (savePanel != null) {
          //list.setFatherSubject(null);
          savePanel.addSubject(list);
        }
        */

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(false);
        if(viewID == BRW_FOR_MOVEMENTS_ERROR_LIST){
          selectionPanel.showAddButton(false);
          selectionPanel.showEditButton(false);

          FValidationPanel validPanel = selectionPanel.showValidationPanel(true);
          validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
          
          class LocalValidationListener implements FValidationListener{
            FocList list = null;
            
            public LocalValidationListener(FocList list){
              this.list = list;
            }
            
            public void postCancelation(FValidationPanel panel) {
              list.dispose();
              list = null;
            }

            public void postValidation(FValidationPanel panel) {
              list.dispose();
              list = null;
            }

            public boolean proceedCancelation(FValidationPanel panel) {
              return true;
            }

            public boolean proceedValidation(FValidationPanel panel) {
              return true;
            }
          }
          
          validPanel.setValidationListener(new LocalValidationListener(list));
        }
        
        //Never allow delete of a cash movement
        selectionPanel.showRemoveButton(false);
        
        FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(selectionPanel, 0);
        selectionPanel.getTotalsPanel().add(currentItemPanel);
      }
    }
    
    return selectionPanel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final int FLD_CASHDESK = 1;
  public static final int FLD_DATE = 2;
  public static final int FLD_CURRENCY = 3;
  public static final int FLD_AMMOUNT = 4;
  public static final int FLD_DESCRIPTION = 5;
  public static final int FLD_IS_LINKED = 6;
  public static final int FLD_IN_LINE_EXTENSION = 7;
  
  
  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(CashMovement.class, FocDesc.DB_RESIDENT, "CASH_MVT", false);

      focFld = focDesc.addReferenceField();
      focDesc.setRightsByLevelEnabled(FocDesc.RIGHTS_BY_LEVEL_MODE_TRACE_ONLY);

      FObjectField focObjFld = new FObjectField("CASH_DESK", "Cash desk", FLD_CASHDESK, false, CashDesk.getFocDesc(), "CASH_");
      focObjFld.setComboBoxCellEditor(CashDesk.FLD_NAME);
      focObjFld.setSelectionList(CashDesk.getList(FocList.NONE));
      focDesc.addField(focObjFld);

      focFld = new FDateField("DATE", "Date", FLD_DATE, false);
      focDesc.addField(focFld);

      focObjFld = new FObjectField("CURRENCY", "Currency", FLD_CURRENCY, false, b01.foc.currency.Currency.getFocDesc(), "CURR_");
      focObjFld.setComboBoxCellEditor(Currency.FLD_NAME);
      focObjFld.setSelectionList(Currency.getList(true));
      focDesc.addField(focObjFld);

      focFld = new FNumField("AMMOUNT", "Ammount", FLD_AMMOUNT, false, 9, 2);
      focDesc.addField(focFld);
      
      focFld = new FCharField("DESCRIP", "Description", FLD_DESCRIPTION, false, 50);
      focDesc.addField(focFld);
      
      focFld = new FBoolField("IS_LINKED", "Is linked", FLD_IS_LINKED, false);
      focDesc.addField(focFld);
      
      if(hasExtension()){
        FInLineObjectField objField = new FInLineObjectField("EXTENSION", "In line cash movement details", FLD_IN_LINE_EXTENSION, false, extension.getFocDesc(), "EXT_");
        focDesc.addField(objField);
      }
    }
    return focDesc;
  }  
}
