// MEMORY LEVEL
// DATABASE LEVEL
// COMMON LEVEL

/*
 * Created on Jul 9, 2005
 */
package b01.foc.list.filter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import b01.foc.db.SQLFilter;
import b01.foc.db.SQLJoin;
import b01.foc.desc.*;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FAbstractListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;
import b01.foc.list.FocListElement;
import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public abstract class FocListFilter extends FocObject {  
  public abstract FilterDesc getThisFilterDesc();
  
  public static final int LEVEL_MEMORY = 1;
  public static final int LEVEL_DATABASE = 2;
  public static final int LEVEL_DATABASE_AND_MEMORY = 3;
  
  private boolean allwaysActive = false;
  private boolean active = false;
  private FAbstractListPanel selectionPanel = null;
  private ArrayList<Integer> visibleArray = null;
  private Color colorBackup = null;
  private int filterLevel = LEVEL_MEMORY;
  private boolean shouldColorRed = false;
  //private SQLJoinMap joinMap = null;
  
  private FValidationPanel validationPanel = null;
  
  public static final int VIEW_SUMMARY = -2;
  
  public FocListFilter(FocConstructor constr){
    super(constr);
    fillProperties(this);
    if(isRevisionSupportEnabled()){
      allwaysActive = true;
    }
  }

  public void dispose(){    
    selectionPanel = null;
    if(visibleArray != null){
      visibleArray.clear();
      visibleArray = null;
    }
    colorBackup = null;
    
    if(validationPanel != null){
      validationPanel.dispose();
      validationPanel = null;
    }
    super.dispose();
  }
  
  public boolean isActive() {
    return active || allwaysActive;
  }
    
  public void fillProperties(FocObject fatherObject){
    FilterDesc filterDesc = getThisFilterDesc();    
    
    if(filterDesc != null){
      filterDesc.fillProperties(fatherObject);
    }
  
  }
  
  public FilterCondition findFilterCondition(String conditionName){
  	FilterCondition foundCond = null;
  	FilterDesc filterDesc = getThisFilterDesc();
  	
  	for(int i=0; i<filterDesc.getConditionCount() && foundCond == null; i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      if(cond != null){
        if(cond.getFieldPrefix().compareTo(conditionName) == 0){
        	foundCond = cond;
        }
      }
    }
  	
  	return foundCond;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // MEMORY LEVEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public ArrayList getVisibleArray(){
    if(visibleArray == null){
      visibleArray = new ArrayList<Integer>();
      FocList list = getGuiFocList();
      
      for(int i=0; i<list.size(); i++){
        FocObject obj = list.getFocObject(i);
        boolean include = false;
        switch(getFilterLevel()){
        case LEVEL_DATABASE:
          include = true;
          break;
        case LEVEL_DATABASE_AND_MEMORY:
          include = includeObject(obj);
          break;
        case LEVEL_MEMORY:
          include = includeObject(obj);
          break;          
        }
        
        if(include){
          visibleArray.add(Integer.valueOf(i));
        }
      }
    }
    //Globals.logString("visible array : " + visibleArray.size());
    return visibleArray;
  }
  
  public void resetVisibleArray(){
    visibleArray = null;
  }
  
  public int getListVisibleElementCount(){
    return getVisibleArray().size();
  }

  public FocListElement getListVisibleElementAt(int row){
  	FocList list = getGuiFocList();
    ArrayList visibleArray = (ArrayList) getVisibleArray();
    return (visibleArray != null && visibleArray.size() > row) ? list.getFocListElement(((Integer)visibleArray.get(row)).intValue()) : null;
  }

  public FocObject getListVisibleObjectAt(int row){
    FocListElement listElement = getListVisibleElementAt(row);
    return listElement.getFocObject();
  }
  
  public boolean includeObject(FocObject focObject){
    boolean include = true;
    FilterDesc filterDesc = getThisFilterDesc();
    if(filterDesc != null && isActive()){
      for(int i=0; i<filterDesc.getConditionCount() && include; i++){
        FilterCondition cond = filterDesc.getConditionAt(i);
        include = cond.includeObject(this, focObject);
      }
    }
    return include;
  }

  public void refreshDisplay(){
    FAbstractListPanel listPanel = getSelectionPanel();
    if(listPanel != null){  
      listPanel.getTableModel().fireTableDataChanged();
    	/*SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					getSelectionPanel().getTableModel().fireTableDataChanged();					
				}
    	});*/
    }
  }
  
  public void setActive_MemoryLevel(boolean active) {
    resetVisibleArray();
    refreshDisplay();
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DATABASE LEVEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private void internalExec(SQLFilter filter, boolean addingJoins /*first time only*/){ 
    FilterDesc filterDesc = getThisFilterDesc();
    StringBuffer buffer = new StringBuffer();
    
    shouldColorRed = false;
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      SQLJoin lastJoin = null; 
      StringBuffer condWhere = cond.buildSQLWhere(this, cond.getDBFieldName());
      if(condWhere != null && condWhere.length() > 0){
      	shouldColorRed = true;
        FFieldPath condFieldPath = cond.getFieldPath();
        FocDesc cMasterDesc = getThisFilterDesc().getSubjectFocDesc();
        FocDesc cSlaveDesc = null;
        
        String currentAlias = filter.getJoinMap().getMainTableAlias();
        for (int fi = 0; fi < condFieldPath.size() && cMasterDesc != null; fi++) {
          int fieldId = condFieldPath.get(fi);
          FField field = cMasterDesc.getFieldByID(fieldId);
          cSlaveDesc = field.getFocDesc();
  
          if(cSlaveDesc != null && condFieldPath.size() > 1){
            lastJoin = new SQLJoin(cSlaveDesc.getStorageName(),currentAlias,field.getNameInSourceTable(),field.getNameInTargetTable());
            lastJoin = filter.getJoinMap().addJoin(lastJoin);
            currentAlias = lastJoin.getNewAlias();                           
          }
          
          cMasterDesc = cSlaveDesc;
        }
        if(!addingJoins){
          String tableAlias = (lastJoin != null) ? lastJoin.getNewAlias() : SQLJoin.MAIN_TABLE_ALIAS;
          String prefixForTable = filter.hasJoinMap()?tableAlias+".":"";
          //We have to recall the build SQL where with the correct full field name (Alias)
          condWhere = cond.buildSQLWhere(this, prefixForTable+cond.getDBFieldName());
          boolean appending = buffer.length() > 0; 
          if(appending){
            buffer.append(" and ("); 
          }
          buffer.append(condWhere);
          if(appending){
            buffer.append(") ");
          }
        }
      }            
    }
    
    if(!addingJoins) filter.addAdditionalWhere(buffer);
  }  
  
  /*public void setActive_DatabaseLevel(boolean active){
    FocList list = getFocList();
    
    SQLFilter filter = list.getFilter();
    if(filter != null){
      filter.setObjectTemplate(null);
      filter.setFilterFields(SQLFilter.FILTER_ON_SELECTED);
      filter.resetSelectedFields();
      
      filter.setAdditionalWhere(null);
      filter.getJoinMap().clearJoinMap();
      if(isActive()){
        internalExec(filter, true);
        internalExec(filter, false);
      }else{
        //filter.getJoinMap().clearJoinMap();      
      }
    }
    resetVisibleArray();
    reloadListFromDatabase();
    //list.fireEvent(null, FocEvent.ID_ITEM_ADD);
    refreshDisplay();
  }*/
  
  public void setActive_DatabaseLevel(boolean active){
    FocList list = getFocList();
    
    SQLFilter filter = list.getFilter();
    if(filter != null){
      filter.setObjectTemplate(null);
      filter.setFilterFields(SQLFilter.FILTER_ON_SELECTED);
      filter.resetSelectedFields();
      
      filter.setAdditionalWhere(null);
      filter.getJoinMap().clearJoinMap();
      internalExec(filter, true);
      internalExec(filter, false);
      
    }
    resetVisibleArray();
    reloadListFromDatabase();
    //list.fireEvent(null, FocEvent.ID_ITEM_ADD);
    refreshDisplay();
  }
  
  private void resetAllNotLockedPropertiesToDefaultValue(){
  	FilterDesc filterDesc = getThisFilterDesc();

    for(int i=0; i < filterDesc.getConditionCount(); i++){
    	FilterCondition cond = filterDesc.getConditionAt(i);
    	if(cond != null){
    		if(!cond.isValueLocked(this)){
    			cond.resetToDefaultValue(this);
    		}
    	}
    }
  }
  
  public void reloadListFromDatabase(){
  	FocList list = getFocList();
    list.reloadFromDB();  	
  }
  
  private boolean isAnyConditionActive(){
  	boolean active = false;
    FilterDesc filterDesc = getThisFilterDesc();
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      StringBuffer condWhere = cond.buildSQLWhere(this, cond.getDBFieldName());
      if(condWhere != null && condWhere.length() > 0){
      	active = true;
      }
    }
    return active;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // COMMON LEVEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  //BElie
  private boolean firstTime = true;

  public  boolean isRevisionSupportEnabled() {
    return getThisFilterDesc().getSubjectFocDesc().isRevisionSupportEnabled();
  }
  
  private RevisionCondition getRevisionCondition() {
    FilterDesc filterDesc = getThisFilterDesc();
    RevisionCondition cond = null;
    for(int i = 0; i < filterDesc.getConditionCount() && cond == null; i++){
      if(filterDesc.getConditionAt(i).getFieldPrefix().compareTo(FField.CREATION_REVISION_FIELD_ID_NAME) == 0){
        cond = (RevisionCondition)filterDesc.getConditionAt(i);
      }
    }
    return cond;
  }
  
  private FProperty getRevisionProperty() {
    FilterDesc filterDesc = getThisFilterDesc();
    FProperty revNumber = null;
    FocList list = getFocList();
    
    //list.getForeignObjectsMap().get(arg0);
    
    revNumber =  filterDesc.getSubjectFocDesc().getRevisionPath().getPropertyFromObject(list.getFocObject(0));
    
    return revNumber;
  }
  
  public void setActive(boolean active) {
    if(isRevisionSupportEnabled() && firstTime ){
      FProperty revNumber =  getRevisionProperty();
      RevisionCondition cond = getRevisionCondition();
      if(revNumber != null && cond != null ){
        cond.setRev(this, revNumber.getInteger());
        cond.setOperator(this, RevisionCondition.OPERATOR_EQUALS);  
      }
      firstTime = false;
    }
    
    if(isRevisionSupportEnabled()){
      unlockPropertiesForRevisionForFocList();
    }
    
    this.active = active || allwaysActive;
    if(!isActive()){
    	resetAllNotLockedPropertiesToDefaultValue();
    }
  	
    if(filterLevel == LEVEL_MEMORY){
      setActive_MemoryLevel(this.active);
    }else{
      setActive_DatabaseLevel(this.active);
    }
   	  
	  if(selectionPanel != null){
	  	shouldColorRed = isAnyConditionActive();
	    if(shouldColorRed){
	      if(colorBackup == null){ 
	        colorBackup = selectionPanel.getFilterButton().getBackground();
	      }
	      selectionPanel.getFilterButton().setBackground(java.awt.Color.RED);
	    }else if(colorBackup != null){
	      selectionPanel.getFilterButton().setBackground(colorBackup);
	    }
	  }
    
	  if(isRevisionSupportEnabled()){
	    FProperty revNumber =  getRevisionProperty();
	    RevisionCondition cond = getRevisionCondition();
	    if( revNumber != null && cond != null ){
	      if( cond.getRev(this) < revNumber.getInteger() ){
	        lockPropertiesForRevisionForFocList();
	        lockRelatedComponents();
	        getSelectionPanel().enableModificationButtons(false);
	      }else{
	        unlockRelatedComponents();
	        getSelectionPanel().enableModificationButtons(true);
	      }  
	    }
	  }
  }

  private void lockPropertiesForRevisionForFocList() {
    FocList list = getFocList();
    for(int j = 0; j < list.size(); j++){
      list.getFocObject(j).lockPropertiesForRevision();
    }
  }
  
  private void lockRelatedComponents() {
    FocObject obj = getParentRevisionFocObject();
    obj.disableRelatedGuiComponents();
  }
  
  private void unlockRelatedComponents() {
    FocObject obj = getParentRevisionFocObject();
    obj.enableRelatedGuiComponents();
  }
  
  private FocObject getParentRevisionFocObject() {
    FocList list = getFocList();
    Iterator it = list.getForeignObjectsMap().keySet().iterator();
    Object key = null;
    while(it.hasNext()) {
      key = it.next();
    }
    return list.getForeignObjectsMap().get(key);
  }
  
  private void unlockPropertiesForRevisionForFocList() {
    FocList list = getFocList();
    for(int j = 0; j < list.size(); j++){
      list.getFocObject(j).unlockPropertiesForRevision();
    }
  }
  //EElie
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // PANEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  
  //BElias make the details panel in a separted class
  /*public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel();
    int y = 0; 
    FilterDesc filterDesc = getThisFilterDesc();
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      GuiSpace space = cond.putInPanel(this, panel, 0, y);
      y += space.getY();
    }
    
    if(validationPanel != null){
      validationPanel.dispose();
      validationPanel = null;
    }
    validationPanel = panel.showValidationPanel(true);
    validationPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
    validationPanel.setAskForConfirmationForExit(false);
    
    validationPanel.setCancelationButtonLabel("Exit");
    validationPanel.setValidationButtonLabel("Apply");
    if(!isAllwaysActive()){
      FGButton removeButton = new FGButton("Remove");
      removeButton.setName("Remove");TEMP
      removeButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          setActive(false);
          validationPanel.cancel();
        }
      });
      validationPanel.addButton(removeButton);
      //BElie
      if (isRevisionSupportEnabled()){
        removeButton.setVisible(false);
      }
      //EElie
    }    
    
    validationPanel.setValidationListener(new FValidationListener(){
      public boolean proceedValidation(FValidationPanel panel) {
        //Globals.logString("list visible count in foc filter : " +getListVisibleElementCount());
        return true;
      }

      public boolean proceedCancelation(FValidationPanel panel) {
        return true;
      }

      public void postValidation(FValidationPanel panel) {   
        setActive(true); 
      }

      public void postCancelation(FValidationPanel panel) {
        
      }
    });
    
    return panel;
  }*/
  
  public FPanel newDetailsPanel(int viewID) {
  	return new FocListFilterGuiDetailsPanel(this, viewID);
  }
  //EElias

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST FILTERING
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public FocList getFocList(){
    return getGuiFocList();
  }

  public FocList getGuiFocList(){
    FAbstractListPanel panel = getSelectionPanel();
    //BElias
    //return (panel != null && (panel instanceof FListPanel))? ((FListPanel)panel).getFocList() : null;
    return panel != null ? panel.getFocList() : null;
    //EElias
  }
  
  public FTableView getTableView() {
    FAbstractListPanel panel = getSelectionPanel();
    return panel != null ? panel.getTableView() : null;
  }
  
  public FAbstractListPanel getSelectionPanel() {
    return selectionPanel;
  }
  
  public void setSelectionPanel(FAbstractListPanel selectionPanel) {
    this.selectionPanel = selectionPanel;
  }
  
  public void setAllwaysActive(boolean allwaysActive) {
    this.allwaysActive = allwaysActive;
    setActive(isActive());
  }

  public boolean isAllwaysActive() {
    return allwaysActive;
  }

  public int getFilterLevel() {
    return filterLevel;
  }
  
  public void setFilterLevel(int filterLevel) {
    this.filterLevel = filterLevel;
  }
}