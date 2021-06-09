/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import java.awt.GridBagConstraints;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FGCheckBox;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FPanel;
import b01.foc.gui.FPopupMenu;
import b01.foc.gui.table.FTableColumn;
import b01.foc.gui.table.FTableView;
import b01.foc.list.FocList;
import b01.sbs.BService;
import b01.sbs.BServiceClient;
import b01.sbs.BServiceInterface;
import b01.sbs.GuiServiceSelector;
import b01.sbs.ServiceRefresher;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class InstrumentGuiBrowsePanel extends FPanel{
  
	private FListPanel selectionPanel = null;
	private FocList instrumentList = null;
	private FGCheckBox automaticRefreshCheckBox = null;
	private Thread refreshThread = null;
	private boolean exitRefreshThreadCompletely = false;
	private ServiceRefresher serviceRefresher = null;
	
	public InstrumentGuiBrowsePanel(FocList list, int viewID){
		FocAppGroup group    = (FocAppGroup) Globals.getApp().getAppGroup();
		int     realViewId   = L3Globals.view_ExtractRealViewId(viewID);
		boolean editable     = realViewId == L3Globals.VIEW_CONFIG;
		boolean canConnect   = realViewId == L3Globals.VIEW_NORMAL && group.allowConnection();

		FocDesc desc = null;
    //BAntoine - Permute - Added the condition on VIEW_SELECTION
		if(realViewId == L3Globals.VIEW_NORMAL || realViewId == L3Globals.VIEW_CONFIG || realViewId == L3Globals.VIEW_PERMUTE){
      //EAntoine - Permute      
			desc = Instrument.getFocDesc();
		}

		if (desc != null) {
      if(list == null){
      	list = Instrument.getFocDesc().getDefaultFocList(FocList.FORCE_RELOAD);
      }
      if (list != null) {
      	instrumentList = list;
      	startAutoRefresh();
        list.setDirectImpactOnDatabase(false);
        selectionPanel = new FListPanel(list);
        selectionPanel.setDirectlyEditable(false);
        FTableView tableView = selectionPanel.getTableView();
        
        if(realViewId == L3Globals.VIEW_NORMAL){
        	automaticRefreshCheckBox = new FGCheckBox();
        	add(automaticRefreshCheckBox, 0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE);
        	automaticRefreshCheckBox.setText("Auto refresh");
        	automaticRefreshCheckBox.setSelected(true);
        }
        FTableColumn col = null;

        //BAntoine - Permute
        if(realViewId == L3Globals.VIEW_PERMUTE){
          tableView.addSelectionColumn();
        }else{
          //EAntoine - Permute
          col = new FTableColumn(desc, FFieldPath.newFieldPath(InstrumentDesc.FLD_ON_HOLD), InstrumentDesc.FLD_ON_HOLD, "On hold", 10, editable && group.allowOnHold());
          tableView.addColumn(col);
          //BAntoine - Permute          
        }
        //EAntoine - Permute        
        
        col = new FTableColumn(desc, FFieldPath.newFieldPath(InstrumentDesc.FLD_CODE), InstrumentDesc.FLD_CODE, "Code",15, editable);
        tableView.addColumn(col);

        col = new FTableColumn(desc, FFieldPath.newFieldPath(InstrumentDesc.FLD_NAME), InstrumentDesc.FLD_NAME, "Name", 15, editable);
        tableView.addColumn(col);
        
        /*
        col = new FTableColumn(desc, FFieldPath.newFieldPath(InstrumentDesc.FLD_MODE), InstrumentDesc.FLD_MODE, "Mode", 7, false);
        tableView.addColumn(col);
        */
        
        if(realViewId == L3Globals.VIEW_CONFIG){
        	tableView.addColumn(desc, InstrumentDesc.FLD_SERVICE_PORT, 12, false);
        	tableView.addColumn(desc, InstrumentDesc.FLD_SERIAL_PORT_NAME, 12, false); 
        	tableView.addColumn(desc, InstrumentDesc.FLD_SERIAL_BAUDE_RATE, 12, false); 
        	tableView.addColumn(desc, InstrumentDesc.FLD_SERIAL_DATA_BITS, 8, false); 
        	tableView.addColumn(desc, InstrumentDesc.FLD_SERIAL_PARITY, 5, false); 
        	tableView.addColumn(desc, InstrumentDesc.FLD_SERIAL_STOP_BIT, 5, false);
          //BAntoine - Permute - before the else had no condition
        }else if(realViewId != L3Globals.VIEW_PERMUTE){
          //EAntoine - Permute          
	        col = new FTableColumn(desc, FFieldPath.newFieldPath(InstrumentDesc.FLD_LAUNCHED), InstrumentDesc.FLD_LAUNCHED, "Launched", 10, false);
	        tableView.addColumn(col);
	
	        col = new FTableColumn(desc, FFieldPath.newFieldPath(InstrumentDesc.FLD_CONNECTED), InstrumentDesc.FLD_CONNECTED, "On", 10, false);
	        tableView.addColumn(col);
        }        

        selectionPanel.construct();
        selectionPanel.setDirectlyEditable(true);

//        if(realViewId != L3Globals.VIEW_CONFIG){
//          FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(selectionPanel,viewID);
//          currentItemPanel.setFill(FPanel.FILL_NONE);
//          selectionPanel.getTotalsPanel().add(currentItemPanel, 0, 0, GridBagConstraints.NORTH);
//        }

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(realViewId == L3Globals.VIEW_CONFIG);
      	selectionPanel.showAddButton(viewID==L3Globals.VIEW_CONFIG);
      	selectionPanel.showRemoveButton(viewID==L3Globals.VIEW_CONFIG);

      	if(canConnect){
	      	FPopupMenu popup = selectionPanel.getTable().getPopupMenu();
	      	if(popup != null){
	      		BServiceClient.fillPopupMenu(popup, new GuiServiceSelector(){
							public BService getSelectedService() {
								Instrument instr = (Instrument) selectionPanel.getSelectedObject();
								return instr.getService();
							}
							
							public void refreshStatus() {
								Instrument instr = (Instrument) selectionPanel.getSelectedObject();
								if(instr != null){
									instr.refreshLaunched();
									instr.refreshConnected();
								}
							}
	      		});
	      	}
      	}
        selectionPanel.setFrameTitle("Instruments");
        //BAntoine - Permute
        if(realViewId == L3Globals.VIEW_PERMUTE){
          selectionPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_VERTICAL);
        }
        //EAntoine - Permute
        add(selectionPanel, 0, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL);
        if(realViewId == L3Globals.VIEW_NORMAL) refreshLaunchedAndConnected(getInstrumentList());
      }
    }
	}
	
	public void dispose(){
		refreshThread = null;
		exitRefreshThreadCompletely = true;
		
		super.dispose();
		automaticRefreshCheckBox = null;
		instrumentList = null;
		selectionPanel = null;
		if(serviceRefresher != null){
			serviceRefresher.dispose();
			serviceRefresher = null;
		}
	}
  
	public FocList getInstrumentList(){
		return instrumentList;
	}
		
	public boolean isAutoRefreshOn(){
		boolean auto = false;
		if(automaticRefreshCheckBox != null){
			auto = automaticRefreshCheckBox.isSelected();
		}
		return auto;
	}
	
	private void refreshLaunchedAndConnected(FocList list){
		
		if(serviceRefresher == null){
			serviceRefresher = new ServiceRefresher(){
				@Override
				protected BServiceInterface getServiceAt(int i) {
					return (BServiceInterface) instrumentList.getFocObject(i);
				}

				@Override
				protected int getServiceCount() {
					return instrumentList.size();
				}
			};
		}
		
		serviceRefresher.refresh();
		
//		list.iterate(new FocListIterator(){
//			@Override
//			public boolean treatElement(FocListElement element, FocObject focObj) {
//				Instrument instr = (Instrument) focObj;
//				instr.refreshLaunched();
//				instr.refreshConnected();
//				return false;
//			}
//		});
	}

	private void startAutoRefresh(){
		refreshThread = new Thread(new Runnable(){
			public void run() {
				while(!exitRefreshThreadCompletely){
					if(isAutoRefreshOn()){
						refreshLaunchedAndConnected(getInstrumentList());
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
