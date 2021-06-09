/*
 * Created on Oct 14, 2004
 */
package b01.foc.admin;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;

import b01.foc.*;
import b01.foc.desc.*;
import b01.foc.gui.*;
import b01.foc.gui.table.ColumnsConfig;
import b01.foc.db.*;
import b01.foc.db.lock.LockManager;
import b01.foc.list.*;
import b01.foc.menu.*;
import b01.foc.util.Encriptor;
import b01.foc.cashdesk.*;

/**
 * @author 01Barmaja
 */
public class AdminModule implements FocModuleInterface {
 
  public static final String ADMIN_USER = "ADMIN";
  
  private boolean activateLogin = false;
  
  private boolean newUserTables = false;
  private int versionDifference = 0;
  
  public AdminModule(boolean activateLogin) {
    this.activateLogin = activateLogin;
  }

  public void declareFocObjects() {
    Application app = Globals.getApp();
    app.declaredObjectList_DeclareObject(FocVersion.class);    
    if(activateLogin){
      app.declaredObjectList_DeclareObject(FocUser.class);
      app.declaredObjectList_DeclareObject(FocGroup.class);
      app.declaredObjectList_DeclareObject(ColumnsConfig.class);
      
      //rr
      app.declaredObjectList_DeclareDescription(MenuRightsDesc.class);
    }
  }
  
  public void checkTables() {
    Application app = Globals.getApp();
    if(app != null){
      DBManager dbMan = app.getDBManager();
      if(dbMan != null){
        Hashtable allRealTables = dbMan.getAllRealTables();
        if(allRealTables != null){
          FocDesc userDesc = FocUser.getFocDesc();              
          FocDesc versionDesc = FocVersion.getFocDesc();
          
          boolean userExist = allRealTables.get(userDesc.getStorageName()) != null;
          boolean versionExist = allRealTables.get(versionDesc.getStorageName()) != null;
          
          newUserTables = !userExist && activateLogin;
          versionDifference = (!versionExist) ? 1 : 0;
          if(versionDifference == 0){
            versionDifference = FocVersion.compareWithDatabaseVersion();
          }
          
          if(versionDifference < 0){
            String text = "Your executable version is less than the database version.\nPlease update your application directory."; 
            text = text + "     * Module : DB version -> EXE version\n";
            FocList verList = FocVersion.getVersionList();
            Iterator iter = verList.focObjectIterator();
            while(iter != null && iter.hasNext()){
              FocVersion ver = (FocVersion)iter.next();
              FocVersion dbVer = ver.getDbVersion();
              
              text = text + "     * " + ver.getJar()+" : " ;
              if(dbVer != null){
                text = text + dbVer.getName() + " ("+dbVer.getId()+") -> ";                    
              }else{
                text = text + " not available -> ";                    
              }
              text = text + ver.getName() + " ("+ver.getId()+")\n";
            }
            Globals.getDisplayManager().popupMessage(text);
            Globals.getApp().exit();
          }else if(newUserTables || versionDifference > 0){
            String text = "You need to adapt data model for the following reasons:\n";
            if(newUserTables){
              text = text + "  - User tables don't exist\n";
            }
            if(versionDifference > 0){
              text = text + "  - New version installation:\n";
              text = text + "     * Module : DB version -> EXE version\n";
              FocList verList = FocVersion.getVersionList();
              Iterator iter = verList.focObjectIterator();
              while(iter != null && iter.hasNext()){
                FocVersion ver = (FocVersion)iter.next();
                FocVersion dbVer = ver.getDbVersion();
                
                text = text + "     * " + ver.getJar()+" : " ;
                if(dbVer != null){
                  text = text + dbVer.getName() + " ("+dbVer.getId()+") -> ";                    
                }else{
                  text = text + " not available -> ";                    
                }
                text = text + ver.getName() + " ("+ver.getId()+")\n";
              }
            }
            
            text = text + "Adapt data model now ?";
            
            int dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                text,
                "01Barmaja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null);
            
            switch(dialogRet){
            case JOptionPane.YES_OPTION:
              app.declareFocObjects();
              app.adaptDataModel();
              break;
            case JOptionPane.NO_OPTION:
              //app.exit();
              break;
            }
          }
          
          if(activateLogin){
            FocUser adminUser = FocUser.findUser(ADMIN_USER);
            if(adminUser == null){
              adminUser = new FocUser(new FocConstructor(FocUser.getFocDesc(), null, null));
              adminUser.setCreated(true);
              adminUser.setName(ADMIN_USER);
              adminUser.setPassword(Encriptor.encrypt_MD5(ADMIN_USER));
              adminUser.save();
            }
          }
        }
      }
    }
  }
  
  public FMenu getAdminMenu(){
    FMenuList mainMenu = new FMenuList(FocLangKeys.MENU_ADMIN_MENU);

    //User
    FMenuList userMenu = new FMenuList(FocLangKeys.MENU_USER);
    FMenuItem userItem = new FMenuItem(FocLangKeys.MENU_USER, new FMenuAction(FocUser.getFocDesc(), true));
    userMenu.addMenu(userItem);
    mainMenu.addMenu(userMenu);
    
    //Group
    FMenuList groupMenu = new FMenuList(FocLangKeys.MENU_GROUP);
    FMenuItem groupItem = new FMenuItem(FocLangKeys.MENU_GROUP, new FMenuAction(FocGroup.getFocDesc(), true));
    groupMenu.addMenu(groupItem);
    mainMenu.addMenu(groupMenu);

    //Cash
    if(Globals.getApp().isCashDeskModuleIncluded()){
      //rr Begin
      FMenuList cashDeskMenu = CashDeskModule.newAdminMenuList();
      mainMenu.addMenu(cashDeskMenu);

      
      /*FMenuList cashierMenu = CashierModule.newAdminMenuList();
      mainMenu.addMenu(cashierMenu);*/
      //rr End
      
    }
    
    //Adapt data model
    FMenuList adaptMenu = new FMenuList(FocLangKeys.MENU_ADAPT_DATA_MODEL);
    AbstractAction adaptAction = new AbstractAction(){
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = 3256719593778393655L;

      public void actionPerformed(ActionEvent e){
        Application app = Globals.getApp();
        app.declareFocObjects();
        app.adaptDataModel();
      }
    };
    FMenuItem adaptItem = new FMenuItem(FocLangKeys.MENU_ADAPT_DATA_MODEL, adaptAction);
    adaptMenu.addMenu(adaptItem);
    mainMenu.addMenu(adaptMenu);

    //Check table locks
    FMenuList checkLocksMenu = new FMenuList(FocLangKeys.MENU_CHECK_LOCKS);
    AbstractAction checkLockAction = new AbstractAction(){
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = 3257848783563142706L;

      public void actionPerformed(ActionEvent e){
        LockManager lockManager = new LockManager();
        FPanel panel = lockManager.newDetailsPanel();
        Globals.getDisplayManager().popupDialog(panel, MultiLanguage.getString(FocLangKeys.MENU_CHECK_LOCKS), true);
      }
    };
    FMenuItem lockItem = new FMenuItem(FocLangKeys.MENU_CHECK_LOCKS, checkLockAction);
    checkLocksMenu.addMenu(lockItem);
    mainMenu.addMenu(checkLocksMenu);
    
    return mainMenu;
  }
  
  private static FGButton addEntry(FPanel panel, AbstractAction action, String title, int x, int y) {
    FGButton button = new FGButton(title);
    panel.add(button, x, y, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
    button.addActionListener(action);
    return button;
  }

  public boolean isNewUserTables() {
    return newUserTables;
  }
}
