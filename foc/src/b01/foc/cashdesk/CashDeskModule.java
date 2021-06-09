/*
 * Created on Oct 14, 2004
 */
package b01.foc.cashdesk;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.list.FocList;
import b01.foc.menu.*;
import b01.foc.gui.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author 01Barmaja
 */
public class CashDeskModule implements FocModuleInterface {

  public CashDeskModule() {
  }

  public void declareFocObjects() {
    Application app = Globals.getApp();

    app.declaredObjectList_DeclareObject(CashDesk.class);
    app.declaredObjectList_DeclareObject(CashDeskConfig.class);
    app.declaredObjectList_DeclareObject(CashMovement.class);
    app.declaredObjectList_DeclareObject(CashOpenClose.class);
  }
  
  public static void includeCashDeskModule(CashMovementExtension extension){
    CashMovement.setExtension(extension);
    Globals.getApp().declareModule(new b01.foc.cashdesk.CashDeskModule());
    Globals.getApp().setCashDeskModuleIncluded(true);
  }
  
  public static FMenuList newMenuList(){
    FMenuList mainMenu = null;
    FocList cashDeskList = CashDesk.getList(FocList.LOAD_IF_NEEDED);
    if(cashDeskList != null && cashDeskList.size() > 0){
      mainMenu = new FMenuList(FocLangKeys.CASH_CASHDESK);
      
      AbstractAction viewAction = new AbstractAction(){
        public void actionPerformed(ActionEvent e) {
          FocList cashDeskList = CashDesk.getList(FocList.LOAD_IF_NEEDED);
          if(cashDeskList != null && cashDeskList.size() > 0){
            FocConstructor focConstr = new FocConstructor(CashDeskViewer.getFocDesc(), null);
            
            CashDeskViewer viewer = (CashDeskViewer) focConstr.newItem();
            
            FPanel panel = viewer.newDetailsPanel(CashDeskViewer.VIEW_SELECT_CASH_DESK); 
            Globals.getDisplayManager().newInternalFrame(panel);
            //Globals.getDisplayManager().changePanel(panel);
          }
        }
      };
  
      AbstractAction errorCheckAction = new AbstractAction(){
        public void actionPerformed(ActionEvent e) {
          FocList errorList = CashMovement.createErrorList();
          FPanel panel = CashMovement.newBrowsePanel(errorList, CashMovement.BRW_FOR_MOVEMENTS_ERROR_LIST);
          Globals.getDisplayManager().newInternalFrame(panel);
        }
      };
  
      //FMenuItem cashdesk = new FMenuItem(FocLangKeys.CASH_CASHDESK, new FMenuAction(CashDesk.getFocDesc(), true));
      FMenuItem cashdeskViewer = new FMenuItem(FocLangKeys.CASH_CASHDESK, viewAction);
      FMenuItem cashMovementErrors = new FMenuItem(FocLangKeys.CASH_MOVEMENT_ERROR_CHECK, errorCheckAction);
      
      mainMenu.addMenu(cashdeskViewer);
      mainMenu.addMenu(cashMovementErrors);
    }
    return mainMenu;
  }
  
  public static FMenuList newAdminMenuList(){
    FMenuList mainMenu = new FMenuList(FocLangKeys.CASH_CASHDESK);
    
    /*
    AbstractAction configAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        FPanel panel = CashDeskConfig.getCashDeskConfig().newDetailsPanel(0); 
        Globals.getDisplayManager().newInternalFrame(panel);
      }
    };
    */
    
    FMenuItem config = new FMenuItem(FocLangKeys.CASH_CASHDESK_CONFIG, new FMenuAction(CashDesk.getFocDesc(), true));
    //FMenuItem cashdesk = new FMenuItem(FocLangKeys.CASH_CASHDESK, new FMenuAction(CashDesk.getFocDesc(), true));
    //FMenuItem ratesByDate = new FMenuItem(FocLangKeys.CURR_RATES, ratesAction);
    
    mainMenu.addMenu(config);
    //mainMenu.addMenu(cashdesk);
    //mainMenu.addMenu(ratesByDate);
    return mainMenu;
  }
  
}
