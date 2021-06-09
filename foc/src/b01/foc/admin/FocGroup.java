// EXTERNAL PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION

/*
 * Created on 20-May-2005
 */
package b01.foc.admin;

import b01.foc.Globals;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.event.FValidationListener;
import b01.foc.gui.*;
import b01.foc.gui.table.*;
import b01.foc.list.*;
import b01.foc.property.*;

import java.awt.*;
import java.util.Iterator;

/**
 * @author 01Barmaja
 */
public class FocGroup extends FocObject{
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // EXTERNAL PROPERTIES
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo  
  
  private static FocDesc applicationGroupFocDesc = null;
  
  //rr
  private MenuRightsDisplayList menuRightsDisplayList = null;
  private MenuRightsGuiTreePanel menuRightsGuiTreePanel = null;
  
  public static void setApplicationGroup(FocDesc focDesc, int displayField){
    applicationGroupFocDesc = focDesc;
  }  
  
  public static FocDesc getApplicationGroup(){
    return applicationGroupFocDesc;
  }  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static final int VIEW_READ_ONLY = 2;  
  
  // ---------------------------------
  //    MAIN
  // ---------------------------------

  /*public FocGroup(FocConstructor constr) {
    super(constr);
    
    FString name = new FString(this, FocGroupDesc.FLD_NAME, "") ;
    FString description = new FString(this, FocGroupDesc.FLD_DESCRIPTION, "") ;
    FBoolean allowNamingModif = new FBoolean(this, FocGroupDesc.FLD_ALLOW_NAMING_MODIF, false) ;
    //FocConstructor appGroupConstr = new FocConstructor(applicationGroupFocDesc, constr, null);
    //FocObject appGroup = appGroupConstr.newItem();
    FList pAppGroup = new FList(this, FocGroupDesc.FLD_APP_GROUP, new FocList(this, FocGroupDesc.getInstance().getLink_AppGroup(), null));
    if(Globals.getApp().isCurrencyModuleIncluded()){
      new FBoolean(this, FocGroupDesc.FLD_ALLOW_CURRENCY_RATES_MODIF, false) ;    
    }
    if(Globals.getApp().isWithReporting()){
      new FBoolean(this, FocGroupDesc.FLD_ALLOW_REPORT_ACCESS, false) ;    
    }
    if(Globals.getApp().isCashDeskModuleIncluded()){
      new FMultipleChoice(this, FocGroupDesc.FLD_CASH_DESKS_ACCESS, FocGroupDesc.CASH_ACCESS_NONE);
    }
    if(Globals.getApp().isWithRightsByLevel()){
      FMultipleChoice rightsLevel = new FMultipleChoice(this, FocGroupDesc.FLD_RIGHTS_LEVEL, 1);
      //rightsLevel.setPropertyValidator(new FNumLimitValidator(1, Globals.getApp().getRightsByLevel().getNbOfLevels()));
    }
  }*/
  
  public FocGroup(FocConstructor constr) {
    super(constr);
    
    new FString(this, FocGroupDesc.FLD_NAME, "") ;
    new FString(this, FocGroupDesc.FLD_DESCRIPTION, "") ;
    new FBoolean(this, FocGroupDesc.FLD_ALLOW_NAMING_MODIF, false) ;
    //FocConstructor appGroupConstr = new FocConstructor(applicationGroupFocDesc, constr, null);
    //FocObject appGroup = appGroupConstr.newItem();
    if(FocGroupDesc.getInstance().getLink_AppGroup() != null){
    	new FList(this, FocGroupDesc.FLD_APP_GROUP, new FocList(this, FocGroupDesc.getInstance().getLink_AppGroup(), null));
    }
    if(Globals.getApp().isCurrencyModuleIncluded()){
      new FBoolean(this, FocGroupDesc.FLD_ALLOW_CURRENCY_RATES_MODIF, false) ;    
    }
    if(Globals.getApp().isWithReporting()){
      new FBoolean(this, FocGroupDesc.FLD_ALLOW_REPORT_ACCESS, false) ;    
    }
    new FBoolean(this, FocGroupDesc.FLD_ALLOW_DATABASE_BACKUP, false) ;
    new FBoolean(this, FocGroupDesc.FLD_ALLOW_DATABASE_RESTORE, false) ;    
    
    if(Globals.getApp().isCashDeskModuleIncluded()){
      new FMultipleChoice(this, FocGroupDesc.FLD_CASH_DESKS_ACCESS, FocGroupDesc.CASH_ACCESS_NONE);
    }
    if(Globals.getApp().isWithRightsByLevel()){
      new FMultipleChoice(this, FocGroupDesc.FLD_RIGHTS_LEVEL, 1);
      //rightsLevel.setPropertyValidator(new FNumLimitValidator(1, Globals.getApp().getRightsByLevel().getNbOfLevels()));
    }
    
    FocGroupDesc focDesc = FocGroupDesc.getInstance();
    for(int i=0; i< focDesc.getNumberOfAppGroupListFieldID(); i++){
      FListField listField = (FListField) focDesc.getFieldByID(FocGroupDesc.FLD_START_APP_GROUPS + i);
      if(listField != null){
      	listField.newProperty(this);
      }
    }
    
    //rr I NEED IT FOR THE PROPERTY OF MENU LIST TO NOT BE NULL
    newFocProperties();
  }

  public void dispose(){
    super.dispose();
  }
  
  public String getName(){
    FString nameProp = (FString) getFocProperty(FocGroupDesc.FLD_NAME);
    return (nameProp != null) ? nameProp.getString() : (String)null;
  }
  
  public FocObject getAppGroup(){
  	FocList groupList = null;
  	if(applicationGroupFocDesc != null){
	    FList pGroupList = (FList) getFocProperty(FocGroupDesc.FLD_APP_GROUP);
	    groupList = pGroupList.getList();
  	}
    return groupList != null ? groupList.getOrInsertAnItem() : null;
  }

  public boolean allowNamingModif(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_NAMING_MODIF);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }

  public boolean allowCurrencyRateModif(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_CURRENCY_RATES_MODIF);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }

  public boolean allowReportAccess(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_REPORT_ACCESS);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }

  public boolean allowDatabaseBackup(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_DATABASE_BACKUP);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }

  public boolean allowDatabaseRestore(){
    FBoolean pAllowNaingModif = (FBoolean) getFocProperty(FocGroupDesc.FLD_ALLOW_DATABASE_RESTORE);
    return pAllowNaingModif != null ? pAllowNaingModif.getBoolean() : false;
  }
  
  public int getCashDeskAccess(){
    FInt pCashDeskAccess = (FInt) getFocProperty(FocGroupDesc.FLD_CASH_DESKS_ACCESS);
    return pCashDeskAccess != null ? pCashDeskAccess.getInteger() : 0;
  }

  public int getRightsLevel(){
    FInt pCashDeskAccess = (FInt) getFocProperty(FocGroupDesc.FLD_RIGHTS_LEVEL);
    return pCashDeskAccess != null ? pCashDeskAccess.getInteger() : 0;
  }

  /*public FocObject getAppGroupAt(int fieldID){
    FocGroup fg = Globals.getApp().getGroup();
    FocList list = fg.getPropertyList(fieldID);
    return list.getOrInsertAnItem();
  }*/
  
  public FocObject getAppGroupAt(int fieldID){
  	FocList list = getPropertyList(fieldID);
    return list.getOrInsertAnItem();
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newGeneralFocPanel(int viewID){
    FPanel panel = new FPanel();
    
    int x = 0;
    int y = 0;
    
    Component comp = null;
    
    if(Globals.getApp().isWithRightsByLevel()){
      comp = panel.add(this, FocGroupDesc.FLD_RIGHTS_LEVEL, x, y++);
      if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
    }
    
    if(Globals.getApp().isCashDeskModuleIncluded()){
      comp = getGuiComponent(FocGroupDesc.FLD_CASH_DESKS_ACCESS);
      if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
      panel.add("Cash desk access", comp, x, y++);
    }
        
    FPanel checkFlagPanel = new FPanel();
    
    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_NAMING_MODIF, 0, 0, 2, 1, GridBagConstraints.NONE);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    if(Globals.getApp().isCurrencyModuleIncluded()){
      comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_CURRENCY_RATES_MODIF, 0, 1, 2, 1, GridBagConstraints.NONE);
      if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
    }

    if(Globals.getApp().isWithReporting()){
      comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_REPORT_ACCESS, 0, 2, 2, 1, GridBagConstraints.NONE);
      if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
    }
    
    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_DATABASE_BACKUP, 0, 3, 2, 1, GridBagConstraints.NONE);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    comp = checkFlagPanel.add(this, FocGroupDesc.FLD_ALLOW_DATABASE_RESTORE, 0, 4, 2, 1, GridBagConstraints.NONE);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    panel.add(checkFlagPanel, x, y++, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    FocObject focAppGroup = getAppGroup();
    if(focAppGroup != null){
	    comp = focAppGroup.newDetailsPanel(viewID);
	    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);
	    panel.add(comp, x, y++, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
	 
	    focAppGroup.forceControler(true);
    }
    return panel;
  }
  
  public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel();
    
    int x = 0;
    int y = 0;
    
    Component comp = panel.add(this, FocGroupDesc.FLD_NAME, x, y++);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    comp = panel.add(this, FocGroupDesc.FLD_DESCRIPTION, x, y++);
    if(viewID == VIEW_READ_ONLY) comp.setEnabled(false);

    FGTabbedPane tabbedPanel = new FGTabbedPane();
    FPanel generalPanel = newGeneralFocPanel(viewID);
         
    tabbedPanel.add("General", generalPanel);
    
    if(FocGroupDesc.getInstance().getNumberOfAppGroupListFieldID() > 0){
	    for(int i=0; i< FocGroupDesc.getInstance().getNumberOfAppGroupListFieldID(); i++){
	      FocObject appGroup = getAppGroupAt(FocGroupDesc.FLD_START_APP_GROUPS + i);
	      
	      FPanel groupPanelAtI = appGroup.newDetailsPanel(viewID);
	      String title = groupPanelAtI.getFrameTitle();
	      if(title == null || title.compareTo("") == 0){
	      	title = "Group "+i;
	      }
	      if(viewID == VIEW_READ_ONLY) groupPanelAtI.setEnabled(false);
	      tabbedPanel.add(title, groupPanelAtI);
	   
	      appGroup.forceControler(true);
	    }
    }
    //rr Begin
    menuRightsDisplayList = new MenuRightsDisplayList(getMenuRightsList(), this);
    menuRightsGuiTreePanel = new MenuRightsGuiTreePanel(menuRightsDisplayList.getDisplayList(), FocObject.DEFAULT_VIEW_ID);
    tabbedPanel.add(menuRightsGuiTreePanel, "Menu Rights");
    //rr End
    
    panel.add(tabbedPanel, x, y++, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH);//GridBagConstraints.NONE
    
    forceControler(true);
    if(viewID != VIEW_READ_ONLY){
      FValidationPanel validPanel = panel.showValidationPanel(true);
      validPanel.addSubject(this);
      FocObject focAppGroup = getAppGroup();
      if(focAppGroup != null){
      	validPanel.addSubject(focAppGroup);
      }
      //rr Begin
      validPanel.setValidationListener(new FValidationListener(){

        public void postCancelation(FValidationPanel panel) {
          
        }

        public void postValidation(FValidationPanel panel) {
          
        }

        public boolean proceedCancelation(FValidationPanel panel) {
          return true;
        }

        public boolean proceedValidation(FValidationPanel panel) {
          if(menuRightsDisplayList != null){
            menuRightsDisplayList.updateRealList();
          }
          return true;
        }
      });
      
    }
    panel.setMainPanelSising(FPanel.FILL_VERTICAL);
    //rr End
    return panel;
  }

  
  public FocList getMenuRightsList(){
    FocList menuRightsList = getPropertyList(FocGroupDesc.FLD_MENU_RIGHTS_LIST);
    return menuRightsList;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static final int COL_NAME = 1;  
  public static final int COL_DESCRIPTION = 2;  
  
  public static FocList focList = null; 
  
  public static FocList getList(int loadMode){
    if(focList == null){
      focList = newList(loadMode);
    }else{
      if(loadMode == FocList.FORCE_RELOAD){
        focList.reloadFromDB();      
      }else if(loadMode == FocList.LOAD_IF_NEEDED){
        focList.loadIfNotLoadedFromDB();
      }
    }
    return focList;
  }

  public static FocList newList(int loadMode){
    FocList list = null;
    FocLink link = new FocLinkSimple(getFocDesc());
    list = new FocList(link);

    FocListOrder listOrder = new FocListOrder();
    listOrder.addField(FFieldPath.newFieldPath(FocGroupDesc.FLD_NAME));
    list.setListOrder(listOrder);    
    
    if(loadMode == FocList.FORCE_RELOAD){
      list.reloadFromDB();      
    }else if(loadMode == FocList.LOAD_IF_NEEDED){
      list.loadIfNotLoadedFromDB();      
    }
    
    return list;
  }
  
  public static FocGroup getAnyGroup(){
    FocList list = getList(FocList.LOAD_IF_NEEDED);
    return (FocGroup)list.getAnyItem();
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FocDesc desc = getFocDesc();
    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
        list = getList(FocList.LOAD_IF_NEEDED);
      }
      if (list != null) {
        list.setDirectImpactOnDatabase(false);

        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        
        FTableColumn col = null;

        col = new FTableColumn(desc, FFieldPath.newFieldPath(FocGroupDesc.FLD_NAME), COL_NAME, "Name", true);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(FocGroupDesc.FLD_DESCRIPTION), COL_DESCRIPTION, "Description", true);
        tableView.addColumn(col);
        
        selectionPanel.construct();

        selectionPanel.setDirectlyEditable(false);

        FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
        if (savePanel != null) {
          list.forceControler(true);
          savePanel.addSubject(list);
          savePanel.setValidationListener(new FValidationListener(){
            public boolean proceedValidation(FValidationPanel panel) {
              return true;
            }

            public boolean proceedCancelation(FValidationPanel panel) {
              return true;
            }

            public void postValidation(FValidationPanel panel) {
              getList(FocList.FORCE_RELOAD);
            }

            public void postCancelation(FValidationPanel panel) {
              getList(FocList.FORCE_RELOAD);              
            }
          });
        }

        selectionPanel.requestFocusOnCurrentItem();
      }
    }
    selectionPanel.setFrameTitle("Group");
    
    return selectionPanel;
  }

  public static void printDebug(String title){
    FocList list = getList(FocList.LOAD_IF_NEEDED);
    if(list != null){
      Globals.logString("");
      Globals.logString(title);
      Iterator iter = list.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocGroup group = (FocGroup)iter.next();
        if(group != null){
          Globals.logString("Group "+group.toString()+" "+group.getReference().getInteger()+" : "+group.getName());
        }
      }
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getFocDesc() {
    return FocGroupDesc.getInstance();
  }
}
