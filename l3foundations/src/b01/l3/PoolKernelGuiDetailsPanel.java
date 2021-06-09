
/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import java.awt.GridBagConstraints;

import b01.foc.gui.FGButton;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class PoolKernelGuiDetailsPanel extends FPanel {
  private PoolKernel pool= null;
  private boolean isLocked = false;
  private int realView = 0;
  private FPanel headerPanel = null;

  public PoolKernelGuiDetailsPanel(int view, PoolKernel pool){
    this.pool = pool;
    this.realView = L3Globals.view_ExtractRealViewId(view);
    isLocked = L3Globals.view_IsViewLocked(view);
    newDetailsPanelAllViews();
  }
  
  public void addLisConnectorButton(FGButton button){
  	headerPanel.add(button, 2, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL);
  }
 
  public void newDetailsPanelAllViews() {
		setFill(FILL_VERTICAL);
  	setMainPanelSising(MAIN_PANEL_FILL_VERTICAL);
  	headerPanel = new FPanel();
  	{
  		headerPanel.add(pool, PoolKernelDesc.FLD_NAME, 0, 0);
	    if(L3Application.getAppInstance().isLaunchAsServices() && realView == L3Globals.VIEW_CONFIG){
	    	headerPanel.add(pool, PoolKernelDesc.FLD_SERVICE_HOST, 0, 1);
	    }
  	}

    FPanel InstrumentPanel = Instrument.newBrowsePanel(pool.getInstrumentList(), L3Globals.view_BuildViewId(realView, isLocked));
    InstrumentPanel.setFill(FPanel.FILL_VERTICAL);
    add(InstrumentPanel, 0, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL);
  	
  	add(headerPanel, 0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE);

    setFrameTitle ("Pool: "+pool.getName());
    //setMainPanelSising(FPanel.FILL_VERTICAL);
    setFill(FPanel.FILL_VERTICAL);
    FValidationPanel validPanel = showValidationPanel(true);
    if(realView == L3Globals.VIEW_CONFIG){
    	validPanel.addSubject(pool);
    }else{
    	validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    }
  }

  public void dispose(){
    super.dispose();
    pool = null;
    headerPanel = null;
  }
}
