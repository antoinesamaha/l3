package b01.fab.parameterSheet;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;

@SuppressWarnings("serial")
public class ParameterSheetSelectorGuiBrowsePanel extends FListPanel {
	
	public ParameterSheetSelectorGuiBrowsePanel(FocList list, int viewID){
    super("Paramete set selector",FPanel.FILL_BOTH);
    FocDesc desc = ParameterSheetSelectorDesc.getInstance();
    if(desc != null){
    	if(list == null){
    		list = ParameterSheetSelectorDesc.getList(FocList.LOAD_IF_NEEDED);
    	}
      if (list != null) {
        try{
          setFocList(list);
        }catch(Exception e){
          Globals.logException(e);
        }
        list.setDirectlyEditable(true);
        FTableView tableView = getTableView();  
        
        tableView.addColumn(desc, ParameterSheetSelectorDesc.FLD_PARAM_SET_ID, 10, true);
        tableView.addColumn(desc, ParameterSheetSelectorDesc.FLD_PARAM_SET_NAME, 30, true);
        tableView.addColumn(desc, ParameterSheetSelectorDesc.FLD_TABLE_NAME, 30, true);

        construct();
        
        requestFocusOnCurrentItem();
        showEditButton(false);
        showDuplicateButton(false);
        
        FValidationPanel validPanel = showValidationPanel(true);
        validPanel.addSubject(list);
      }
    }
	}
}
