/*
 * Created on September 29, 2007
 */
package b01.l3.data;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import b01.foc.Globals;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FObjectField;
import b01.foc.event.FValidationListener;
import b01.foc.gui.FPanel;
import b01.foc.gui.FPopupMenu;
import b01.foc.gui.FTreeTablePanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;
import b01.foc.property.FBoolean;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FObject;
import b01.foc.tree.FNode;
import b01.foc.tree.TreeScanner;
import b01.l3.Instrument;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class L3SampleTestJoinGuiBrowsePanel extends FPanel{	

	private FTreeTablePanel selectionPanel = null;
	private FocList listToDispose = null;
	private L3SampleTestJoinTree tree = null;
	private L3SampleTestJoinFilter filter = null;
	private boolean doDelete = false;
	
	public L3SampleTestJoinGuiBrowsePanel(FocList list, int viewID){
		L3SampleTestJoinDesc desc = (L3SampleTestJoinDesc) L3SampleTestJoinDesc.getInstance();

    if (desc != null) {
    	if(list == null){
	    	filter = L3SampleTestJoinDesc.newListWithFilter();
	      filter.makeForCurrentDateOnly();
	    	list = filter.getFocList();
	    	listToDispose = list;
    	}
      if (list != null) {
        list.setDirectImpactOnDatabase(false);
        
        tree = new L3SampleTestJoinTree(list, viewID);//rr viewID was 0
        
        selectionPanel = new FTreeTablePanel(tree);
        FTableView tableView = selectionPanel.getTableView();       
        
        if(filter != null){
        	tableView.setFilter(filter);
        }
        
        FTableColumn col = null;
        tableView.addLineNumberColumn();
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_DESCRIPTION), L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_DESCRIPTION, "Test Desc.", false);
        tableView.addColumn(col);
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_FIRST_NAME), L3SampleDesc.FLD_FIRST_NAME, "First name", 15, false);
        tableView.addColumn(col);
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_LAST_NAME), L3SampleDesc.FLD_LAST_NAME, "Last name", 15, false);
        tableView.addColumn(col);
        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_LIQUIDE_TYPE), L3SampleDesc.FLD_LIQUIDE_TYPE, "Liq.", 10, false);
        tableView.addColumn(col);
        tableView.addColumn(desc, L3SampleDesc.FLD_AGE, "Age", 5, false);
        tableView.addColumn(desc, L3SampleDesc.FLD_SEXE, "Sex", 5, false);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(L3SampleDesc.FLD_ENTRY_DATE), L3SampleDesc.FLD_ENTRY_DATE, "Date", 14, false);
        tableView.addColumn(col);
        
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_SUGGESTED_INSTRUMENT, "Suggested Inst.", 10, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_DISPATCH_INSTRUMENT, "Dispatch to Inst.", 10, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_RECEIVE_INSTRUMENT, "Reception Inst.", 10, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_STATUS, "Status", 15, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_LABEL, "Test", 10, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_VALUE, "Value", 8, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_VALUE_NOTES, "Notes", 20, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_ALARM, "Alarm", 3, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_RESULT_OK, "Ok", 5, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_BLOCKED, "Blocked", 3, false);
        tableView.addColumn(desc, L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_MESSAGE, "Message", 15, false);        
        
        selectionPanel.construct();

        selectionPanel.showFilterButton(true);
        selectionPanel.setFill(FPanel.FILL_BOTH);
      }
       
      JMenuItem deleteTests = new JMenuItem("Delete tests and samples", Globals.getApp().getFocIcons().getDeleteIcon());
      deleteTests.addActionListener(new DeleteTestsActionListener());
      
      JMenuItem resetStatus = new JMenuItem("Reset Status...");
      resetStatus.addActionListener(new ResetStatusActionListener());

      JMenuItem blockedStatus = new JMenuItem("Blocked...");
      blockedStatus.addActionListener(new BlockFlagActionListener());

      JMenuItem showDetails = new JMenuItem("Details...");
      showDetails.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
		    	FNode node = selectionPanel.getSelectedNode();
		    	if(node.isLeaf()){
						L3SampleTestJoin join = (L3SampleTestJoin) node.getObject();
						int ref = join.getPropertyInteger(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + FField.REF_FIELD_ID);

						L3Test test = L3TestDesc.newL3Test(ref);
						L3TestGuiDetailsPanel detailsPanel = new L3TestGuiDetailsPanel(FocObject.DEFAULT_VIEW_ID, test);
						Globals.getDisplayManager().popupDialog(detailsPanel, "L3Test", true);
						
		    	}else{
		    		Globals.getDisplayManager().popupMessage("Applies only to leaf nodes");
		    	}
				}
      });

      JMenuItem redispatchMenu = null;
      
      if(viewID == L3SampleTestJoinTree.VIEW_BY_DISPATCHED_INSTRUMENT){
      	redispatchMenu = new JMenuItem("Redispatch...");
	      redispatchMenu.addActionListener(new RedispatchActionListener());
      }
      
      FPopupMenu popupMenu = selectionPanel.getTable().getPopupMenu();
      popupMenu.add(deleteTests);
      popupMenu.add(resetStatus);
      popupMenu.add(blockedStatus);
      popupMenu.add(showDetails);
      if(redispatchMenu != null) popupMenu.add(redispatchMenu);
      
      if(filter != null){
      	filter.setActive(true);
      }
    }
    
    FValidationPanel validPanel = showValidationPanel(true);
    if(viewID == L3SampleTestJoinTree.VIEW_PURGE){
    	validPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
    	validPanel.setValidationButtonLabel("Delete");
    	validPanel.setValidationListener(new FValidationListener(){
				public void postCancelation(FValidationPanel panel) {
					doDelete = false;
				}

				public void postValidation(FValidationPanel panel) {
					doDelete = true;
				}

				public boolean proceedCancelation(FValidationPanel panel) {
					return true;
				}

				public boolean proceedValidation(FValidationPanel panel) {
					doDelete = true;
					return true;
				}
    	});
    	selectionPanel.showFilterButton(false);
    }else{
    	validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    }
    
    //selectionPanel.setFrameTitle("Samples");
    selectionPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    selectionPanel.showModificationButtons(false);
    selectionPanel.setFill(FPanel.FILL_BOTH);
    add(selectionPanel, 0, 0, 1, 1, 0.9, 0.1, GridBagConstraints.EAST, GridBagConstraints.BOTH);
    selectionPanel.showColomnSelectorButton(true, "SMPL_TST_JOIN");
    //setFrameTitle("Samples");
    setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    setFill(FPanel.FILL_BOTH);    
	}
	
	public void dispose(){
		if(listToDispose != null){
			listToDispose.dispose();
			listToDispose = null;
		}
		
		if(filter != null){
			filter.dispose();
			filter = null;
		}
		
		if(tree != null){
			tree.dispose();
			tree = null;
		}
		
		selectionPanel = null;
		super.dispose();
	}

	public boolean isDoDelete() {
		return doDelete;
	}
		
	//---------------------------------------------
	// DELETE
	//---------------------------------------------
	public class DeleteTestsActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
    	FNode node = selectionPanel.getSelectedNode();
    	
      int choice = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(), 
          "Do you confirm deletion of all tests under the node : '"+node.getTitle()+"' ?", 
          "Backup",
          JOptionPane.YES_NO_OPTION);
      if(choice == JOptionPane.YES_OPTION){
		    FocList listToDeleteCollection = L3SampleTestJoinDesc.newList();
		    listToDeleteCollection.setCollectionBehaviour(true);
	
		    DeleteTreeScanner scanner = new DeleteTreeScanner(listToDeleteCollection);
		    node.scan(scanner);
		    scanner.dispose();
		    scanner = null;
	
		    try{
		    	L3SampleTestJoinDesc.deleteListFromL3Test(listToDeleteCollection);
		    }catch(Exception e2){
		    	Globals.logException(e2);
		    }
		    
		    listToDeleteCollection.dispose();
		    listToDeleteCollection = null;
		    
		    filter.setActive(true);
      }
		}
		
		public class DeleteTreeScanner implements TreeScanner<FNode>{
			private FocList listToDeleteCollection = null;
			
			public DeleteTreeScanner(FocList listToDeleteCollection){
				this.listToDeleteCollection = listToDeleteCollection;
			}
			
			public void dispose(){			
				listToDeleteCollection = null;
			}
			
			public void afterChildren(FNode node) {
			}

			public boolean beforChildren(FNode node) {
				if(node.isLeaf()){
					L3SampleTestJoin join = (L3SampleTestJoin) node.getObject();
					listToDeleteCollection.add(join);
				}
				return true;
			}
		}
	}
	
	//---------------------------------------------
	// RESET STATUS
	//---------------------------------------------
	public class ResetStatusActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
    	FNode node = selectionPanel.getSelectedNode();
    	
    	L3SampleTestJoinDesc desc = L3SampleTestJoinDesc.getInstance();
    	FMultipleChoiceField multiFld = (FMultipleChoiceField) desc.getFieldByID(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START+L3TestDesc.FLD_STATUS);
    	FMultipleChoice statusProp = (FMultipleChoice) multiFld.newProperty(null);
    	statusProp.setFocField(multiFld);
    	
    	Component comp = multiFld.getGuiComponent(statusProp);
    	
    	FPanel panel = new FPanel();
    	panel.add("New Status : ", comp, 0, 0);
    	FValidationPanel validPanel = panel.showValidationPanel(true);
    	validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    	Globals.getDisplayManager().popupDialog(panel, "Select the new Status", true);
    	
    	final int selectedStatus = statusProp.getInteger();
    	final String selectedStatusStr = statusProp.getString();
    	statusProp.dispose();
    	statusProp = null;
    	
      int choice = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(), 
          "Do you confirm changing the status of all tests under the node : '"+node.getTitle()+"'\n to '"+selectedStatusStr+"'", 
          "Backup",
          JOptionPane.YES_NO_OPTION);
      if(choice == JOptionPane.YES_OPTION){
		    node.scan(new TreeScanner<FNode>(){
					public void afterChildren(FNode node) {
						if(node.isLeaf()){
							L3SampleTestJoin join = (L3SampleTestJoin) node.getObject();
							
							int ref = join.getPropertyInteger(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + FField.REF_FIELD_ID);
								
							L3TestDesc.updateStatus(ref, selectedStatus);
						}
					}
	
					public boolean beforChildren(FNode node) {
						return true;
					}
		    });
		    
		    filter.setActive(true);
      }
		}
	}
	
	public class BlockFlagActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
    	FNode node = selectionPanel.getSelectedNode();
    	
    	L3SampleTestJoinDesc desc = L3SampleTestJoinDesc.getInstance();
    	FBoolField bFld = (FBoolField) desc.getFieldByID(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START+L3TestDesc.FLD_BLOCKED);
    	FBoolean blockedProp = (FBoolean) bFld.newProperty(null);
    	blockedProp.setFocField(bFld);
    	
    	Component comp = bFld.getGuiComponent(blockedProp);
    	
    	FPanel panel = new FPanel();
    	panel.add("New Blocked flag value : ", comp, 0, 0);
    	FValidationPanel validPanel = panel.showValidationPanel(true);
    	validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    	Globals.getDisplayManager().popupDialog(panel, "Select the new Blocked value", true);
    	
    	final boolean blockedValue = blockedProp.getBoolean();
    	blockedProp.dispose();
    	blockedProp = null;
    	
    	String message = blockedValue ? "Block" : "Unblock";
    	message += " all tests under the node titled : '"+node.getTitle()+"'\n";
    	
      int choice = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(), 
      		message, 
          "Setting Block flag",
          JOptionPane.YES_NO_OPTION);
      if(choice == JOptionPane.YES_OPTION){
		    node.scan(new TreeScanner<FNode>(){
					public void afterChildren(FNode node) {
						if(node.isLeaf()){
							L3SampleTestJoin join = (L3SampleTestJoin) node.getObject();
							
							int ref = join.getPropertyInteger(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + FField.REF_FIELD_ID);
								
							L3TestDesc.updateBlockedFlag(ref, blockedValue);
						}
					}
	
					public boolean beforChildren(FNode node) {
						return true;
					}
		    });
		    
		    filter.setActive(true);
      }
		}
	}
	
	public class RedispatchActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
    	FNode node = selectionPanel.getSelectedNode();
    	
    	L3SampleTestJoinDesc desc = L3SampleTestJoinDesc.getInstance();
    	FObjectField oFld = (FObjectField) desc.getFieldByID(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START+L3TestDesc.FLD_DISPATCH_INSTRUMENT);
    	FObject dispatchProp = (FObject) oFld.newProperty(null);
    	dispatchProp.setFocField(oFld);
    	
    	Component comp = oFld.getGuiComponent(dispatchProp);
    	
    	FPanel panel = new FPanel();
    	panel.add("Dispatch to new Instrument : ", comp, 0, 0);
    	FValidationPanel validPanel = panel.showValidationPanel(true);
    	validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    	Globals.getDisplayManager().popupDialog(panel, "Select the new Instrument", true);
    	
    	Instrument newDispatchingInstrument = (Instrument) dispatchProp.getObject();
    	dispatchProp.dispose();
    	dispatchProp = null;
  	
    	if(newDispatchingInstrument != null && newDispatchingInstrument.isOnHold()){
    		Globals.getDisplayManager().popupMessage(newDispatchingInstrument.getCode()+" is on hold, redispatching is aborted.");
    	}else{
    		
      	String message = "Redispatch all tests under the node titled : '"+node.getTitle()+"'\n";
      	if(newDispatchingInstrument == null){
        	message += "without a suggested instrument.";
      	}else{
        	message += "with the suggested instrument : "+newDispatchingInstrument;
      	}
      	
        int choice = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(), 
        		message, 
            "Redispatching",
            JOptionPane.YES_NO_OPTION);
        if(choice == JOptionPane.YES_OPTION){
        	try{
    		    node.scan(new RedispatchTreeScanner(newDispatchingInstrument));
        	}catch(Exception e2){
        		Globals.logException(e2);
        	}
  		    filter.setActive(true);
        }
    	}
		}

		public class RedispatchTreeScanner implements TreeScanner<FNode>{
			private Instrument newInstr = null;
			
			public RedispatchTreeScanner(Instrument newInstr){
				this.newInstr = newInstr;
			}
			
			public void dispose(){			
				newInstr = null;
			}
			
			public void afterChildren(FNode node) {
				if(node.isLeaf()){
					L3SampleTestJoin join = (L3SampleTestJoin) node.getObject();
					int ref = join.getPropertyInteger(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + FField.REF_FIELD_ID);
						
					L3Test src = L3TestDesc.newL3Test(ref);
					/*
					L3Test tar = L3TestDesc.newL3Test();
					tar.copyPropertiesFrom(src);
					tar.setCreated(true);
					*/
					src.setPropertyObject(L3TestDesc.FLD_DISPATCH_INSTRUMENT, null);
					src.setPropertyObject(L3TestDesc.FLD_RECEIVE_INSTRUMENT, null);
					src.setPropertyBoolean(L3TestDesc.FLD_BLOCKED, false);
					src.setPropertyMultiChoice(L3TestDesc.FLD_STATUS, L3TestDesc.TEST_STATUS_AVAILABLE_IN_L3);
					src.setPropertyString(L3TestDesc.FLD_MESSAGE, "");
					
					src.setPropertyObject(L3TestDesc.FLD_SUGGESTED_INSTRUMENT, newInstr);
					
					src.validate(false);
					
					src.dispose();
					src = null;
					//tar.dispose();
					//tar = null;
				}				
			}

			public boolean beforChildren(FNode node) {
				return true;
			}
		}
	}
}
