package b01.fab.model.menu;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FField;
import b01.foc.gui.FPanel;
import b01.foc.gui.FTreeTablePanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class MenuDefinitionGuiTreePanel extends FTreeTablePanel {
	
	public MenuDefinitionGuiTreePanel(FocList list, int viewID){
		super(new MenuDefinitionTree(list, viewID));
    FocDesc desc = MenuDefinitionDesc.getInstance();
    if (desc != null) {
      if(list == null){
        list = MenuDefinitionDesc.getList(FocList.FORCE_RELOAD);
      }
      if (list != null) {
        
      	FTableView tableView = getTableView();
      	FTableColumn col = tableView.getColumnById(FField.TREE_FIELD_ID);
      	col.setEditable(true);
      	
      	col = new FTableColumn(desc, MenuDefinitionDesc.FLD_TABLE_DEFINITION, "Table", 20, true);
      	tableView.addColumn(col); 
      	
      	col = new FTableColumn(desc, MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION, "Browse view", 20, true);
      	tableView.addColumn(col);
      	
      	col = new FTableColumn(desc, MenuDefinitionDesc.FLD_USER_DETAILS_VIEW_DEFINITION, "Details view", 20, true);
      	tableView.addColumn(col);
      	
      	construct();
        
      	showEditButton(false);
        FValidationPanel validPanel = showValidationPanel(true);
        validPanel.addSubject(list);
        validPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
        
        setFrameTitle("User Menus");
        setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);   
      }
    }
  }
	
}
