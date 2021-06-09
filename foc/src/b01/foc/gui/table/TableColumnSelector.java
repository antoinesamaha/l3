package b01.foc.gui.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.table.JTableHeader;

import b01.foc.Globals;
import b01.foc.admin.FocGroup;
import b01.foc.desc.field.FField;
import b01.foc.event.FValidationListener;
import b01.foc.gui.FDesktop;
import b01.foc.gui.FGCheckBox;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.list.FocList;

public class TableColumnSelector extends FPanel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private FTableView tableView = null;
  private HashMap<Integer, FGCheckBox> checkBoxMap = null;
    
  public TableColumnSelector(FTable table){
    this.tableView = table.getTableView();
    checkBoxMap = new HashMap<Integer, FGCheckBox>();
    int rowCont = 0;
    
  	FColumnGroupTableHeader tableHeader = null;
  	if(table != null){
  		JTableHeader jTableHeader = table.getTableHeader();
  		if(jTableHeader != null && jTableHeader instanceof FColumnGroupTableHeader){
  			tableHeader = (FColumnGroupTableHeader) jTableHeader;
  		}
  	}
    	
    for(int i = 0; i < tableView.getColumnCount(); i++){
      FTableColumn col = tableView.getColumnAt(i);
      if(col.isShowConfigurable()){
	      FGCheckBox check = new FGCheckBox();
	      String title = "Line number";
	      if(col.getID() != FField.LINE_NUMBER_FIELD_ID){
		      if(tableHeader == null){
		      	title = col.getTitle();
		      }else if(tableHeader != null){
		      	title = "";
		      	ArrayList<FColumnGroup> groupsList  = tableHeader.getGroupsForColumn(col);
		      	for(int g=0; g<groupsList.size(); g++){
		      		FColumnGroup group = groupsList.get(g);
		      		title += group.getHeaderValue().toString();
		      		title += " - ";
		      	}
		      	title += col.getTitle();
		      }
	      }
	      check.setText(title);
	      check.setSelected(col.isShow());
	      checkBoxMap.put(col.getID(), check);
	      add(check,0,rowCont++);
      }
    }
    
    FValidationPanel validPanel = showValidationPanel(true);
    validPanel.getValidationButton().setText("Apply");
    validPanel.setValidationListener(new FValidationListener(){
      public void postCancelation(FValidationPanel panel) {
      }

      public void postValidation(FValidationPanel panel) {
      }

      public boolean proceedCancelation(FValidationPanel panel) {
        return true;
      }

      public boolean proceedValidation(FValidationPanel panel) {
        FocList columnConfigList = ColumnsConfig.newColumnsConfigFocList(getTableView().getViewKey());
        updateConfigList(columnConfigList,checkBoxMap);
        columnConfigList.validate(false);
        columnConfigList.dispose();
        columnConfigList = null;
        Iterator iter = checkBoxMap.keySet().iterator();
        while (iter.hasNext()){
          int key = (Integer)iter.next();
          FGCheckBox checkBox = checkBoxMap.get(key);
          getTableView().getColumnById(key).setShow(checkBox.isSelected());
        }
        getTableView().adjustColumnVisibility();
        ((FDesktop)Globals.getDisplayManager().getNavigator()).getSelectedFrame().pack();
        return true;
      }
    });
  }
  
  //BElias save column visibility configuration in data base
  public void updateConfigList(FocList list, HashMap<Integer, FGCheckBox> visibilityMap){
    Iterator iter = visibilityMap.keySet().iterator();
    while (iter.hasNext()){
      int key = (Integer)iter.next();
      FGCheckBox chexkBox = visibilityMap.get(key);
      ColumnsConfig config = (ColumnsConfig)list.searchByPropertyIntValue(ColumnsConfig.FLD_HIDEN_COL_ID, key);
      if(chexkBox.isSelected()){// if the column have to  be visible
        if(config != null){//if this column is hiden in preivious config
          config.setDeleted(true);
        }
      }else{//if the column have to be hidden 
        if(config == null){
          ColumnsConfig newConfig = (ColumnsConfig)list.newEmptyItem();
          newConfig.setUser(Globals.getApp().getUser());
          newConfig.setViewKey(getTableView().getViewKey());
          newConfig.setColumnID(key);
          newConfig.setCreated(true);
          list.add(newConfig);
        }
      }
    }
  }
  //EElias
  
  public void dispose(){
    tableView = null;
    if(checkBoxMap != null){
      checkBoxMap.clear();
      checkBoxMap = null;
    }    
    super.dispose();
  }
  
  public FTableView getTableView(){
    return tableView;
  }
}
