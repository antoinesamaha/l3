package b01.l3.connector;

import java.awt.GridBagConstraints;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FGCheckBox;
import b01.foc.gui.FGCurrentItemPanel;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FPopupMenu;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;
import b01.foc.list.FocListElement;
import b01.foc.list.FocListIterator;
import b01.l3.L3Application;
import b01.l3.L3Globals;
import b01.sbs.BService;
import b01.sbs.BServiceClient;
import b01.sbs.GuiServiceSelector;

@SuppressWarnings("serial")
public class LisConnectorGuiBrowsePanel extends FPanel {
	
	private FListPanel selectionPanel = null;
	private FocList connectorList = null;	
	private FGCheckBox automaticRefreshCheckBox = null;
	private Thread refreshThread = null;
	private boolean exitRefreshThreadCompletely = false;
	
	public  LisConnectorGuiBrowsePanel(FocList list, int viewID){
		FocDesc desc = LisConnector.getFocDesc();

    selectionPanel = null;
    if (desc != null) {
      if(list == null){
      	list = LisConnector.getFocDesc().getDefaultFocList(FocList.FORCE_RELOAD);
      }
      if (list != null) {
      	connectorList = list;
      	startAutoRefresh();
        list.setDirectImpactOnDatabase(true);
        list.setDirectlyEditable(false);
        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        tableView.setDetailPanelViewID(viewID);
        
        FTableColumn col = null;

        col = new FTableColumn(desc, FFieldPath.newFieldPath(LisConnectorDesc.FLD_NAME), LisConnectorDesc.FLD_NAME, "Name", 30, false);
        tableView.addColumn(col);
        
        if(viewID == L3Globals.VIEW_NORMAL){
          tableView.addColumn(desc, LisConnectorDesc.FLD_LAUNCHED, 20, false);
          tableView.addColumn(desc, LisConnectorDesc.FLD_CONNECTED, 20, false);
          
        	automaticRefreshCheckBox = new FGCheckBox();
        	add(automaticRefreshCheckBox, 0, 0);
        	automaticRefreshCheckBox.setText("Auto refresh");
        }else{
          tableView.addColumn(desc, LisConnectorDesc.FLD_SERVICE_HOST, 30, false);
          tableView.addColumn(desc, LisConnectorDesc.FLD_SERVICE_PORT, 20, false);
        }
                
        selectionPanel.construct();
        
      	FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
        if (savePanel != null) {
          list.setFatherSubject(null);
          savePanel.addSubject(list);	
        }
        
        if (L3Application.getAppInstance().getMode() == L3Globals.APPLICATION_MODE_WITH_DB && viewID!=L3Globals.VIEW_CONFIG){
          FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(selectionPanel, viewID);
          currentItemPanel.setFill(FPanel.FILL_HORIZONTAL);
          add(currentItemPanel, 1, 0, GridBagConstraints.NORTH);
        }

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(viewID == L3Globals.VIEW_CONFIG || L3Application.getAppInstance().getMode() == L3Globals.APPLICATION_MODE_SAME_THREAD);
        selectionPanel.showDuplicateButton(false);
        selectionPanel.showAddButton(viewID==L3Globals.VIEW_CONFIG);
        selectionPanel.showRemoveButton(viewID==L3Globals.VIEW_CONFIG);
        
      	if(viewID == L3Globals.VIEW_NORMAL){
	      	FPopupMenu popup = selectionPanel.getTable().getPopupMenu();
	      	if(popup != null){
	      		BServiceClient.fillPopupMenu(popup, new GuiServiceSelector(){
							public BService getSelectedService() {
								LisConnector connector = (LisConnector) selectionPanel.getSelectedObject();
								return connector != null ? connector.getService() : null;
							}

							public void refreshStatus() {
								LisConnector connector = (LisConnector) selectionPanel.getSelectedObject();
								if(connector != null){
									connector.refreshLaunched();
									connector.refreshConnected();
								}
							}
	      		});
	      	}
      	}
      }
    }
    selectionPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    add(selectionPanel, 0, 1);
    setFrameTitle("Lis connector");
    if(viewID == L3Globals.VIEW_NORMAL) refreshLaunchedAndConnected(getConnectorList());
	}
	
	public void dispose(){
		refreshThread = null;
		exitRefreshThreadCompletely = true;
		automaticRefreshCheckBox = null;
		
		super.dispose();
		selectionPanel = null;
	}
	
	public FocList getConnectorList(){
		return connectorList;
	}
	
	public boolean isAutoRefreshOn(){
		boolean auto = false;
		if(automaticRefreshCheckBox != null){
			auto = automaticRefreshCheckBox.isSelected();
		}
		return auto;
	}
	
	private void refreshLaunchedAndConnected(FocList list){
		list.iterate(new FocListIterator(){
			@Override
			public boolean treatElement(FocListElement element, FocObject focObj) {
				LisConnector connector = (LisConnector) focObj;
				connector.refreshLaunched();
				connector.refreshConnected();
				return false;
			}
		});
	}

	private void startAutoRefresh(){
		refreshThread = new Thread(new Runnable(){
			public void run() {
				while(!exitRefreshThreadCompletely){
					if(isAutoRefreshOn()){
						refreshLaunchedAndConnected(getConnectorList());
					}
					try{
						Thread.sleep(5000);
					}catch(Exception e){
						Globals.logException(e);
					}
				}
			}
		});
		refreshThread.start();
	}
}