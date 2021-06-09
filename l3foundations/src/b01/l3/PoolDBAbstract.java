

/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import b01.l3.data.L3Message;

/**
 * @author 01Barmaja
 */
public class PoolDBAbstract implements Pool {
	private PoolKernel poolKernel = null;
  
  
  public PoolDBAbstract(){
  	poolKernel = new PoolKernel(true);
  	//super(true);
  }
    
  /*public PoolDBAbstract(boolean loadInstrument){
  	poolKernel = new PoolKernel(getFocDesc());
  }
  */
  public void dispose(){
  	poolKernel.dispose();
  }

	public PoolKernel getPoolKernel() {
		return poolKernel;
	}

	public void setPoolKernel(PoolKernel poolKernel) {
		this.poolKernel = poolKernel;
	}

	public void postMessageToInstrument(String instrumentCode, L3Message message) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void postMessageToLIS(L3Message message) {
		// TODO Auto-generated method stub
		
	}
}
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // PANEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	/*@Override
	public FPanel newDetailsPanel(int viewID) {
		System.out.println("dans detail panel");
		FPanel panel = new FPanel();
	  Component comp = getGuiComponent(FLD_NAME);
	  panel.add(comp, 0, 0);	    
	  return panel;
	}*/
	
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

/*	public static FPanel newBrowsePanel(FocList list, int viewID) {
		FocDesc desc = getFocDesc();
		list = new FocList(new FocLinkSimple(desc));
		list.loadIfNotLoadedFromDB();
    FListPanel selectionPanel = null;
    if (desc != null) {
      
      if (list != null) {
        list.setDirectImpactOnDatabase(false);

        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        
        FField currField = null;
        FTableColumn col = null;

        col = new FTableColumn(desc, FFieldPath.newFieldPath(FLD_NAME), FLD_NAME, "Name", true);
        tableView.addColumn(col);
      
        
        selectionPanel.construct();

        selectionPanel.setDirectlyEditable(true);

        FPanel panel = selectionPanel.getTotalsPanel();
        
        
        FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
        if (savePanel != null) {
          list.setFatherSubject(null);
          savePanel.addSubject(list);
        }

        selectionPanel.requestFocusOnCurrentItem();
        selectionPanel.showEditButton(true);
        selectionPanel.showDuplicateButton(true);
      }
    }
    selectionPanel.setFrameTitle("Pools");
    selectionPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
    
    return selectionPanel;
	}*/

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

/*  private static FocDesc focDesc = null;

	public static final int FLD_NAME = 1;
	
	public static FocDesc getFocDesc() {
	  if (focDesc == null) {
	  	FocDesc fatherDesc = PoolKernel.getFocDesc();
	  	focDesc = fatherDesc;
	  	
	  	try{
	  		focDesc = (FocDesc) fatherDesc.clone();
	  	}catch(Exception e){
	  		b01.foc.Globals.logException(e);
	  	}
	  	focDesc.setDbResident(true);
	  	focDesc.setStorageName(storageName);
	  	focDesc.set(storageName);
	  }
	  return focDesc;
	}*/
