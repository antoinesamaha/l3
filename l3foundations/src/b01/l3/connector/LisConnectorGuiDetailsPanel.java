package b01.l3.connector;

import java.awt.Component;

import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.l3.L3Application;
import b01.l3.L3Globals;

@SuppressWarnings("serial")
public class LisConnectorGuiDetailsPanel extends FPanel {
	private LisConnector connector = null;
  private boolean isLocked = false;
  private int realView = 0;

  public LisConnectorGuiDetailsPanel (int view, LisConnector fileConnector){
  	this.connector = fileConnector;
    
    this.realView = L3Globals.view_ExtractRealViewId(view);
    isLocked = L3Globals.view_IsViewLocked(view);
  	switch (realView){
  	case L3Globals.VIEW_CONFIG:
  		newDetailsPanelConfigView();
      setValidationPanelListenerForConfigView();
  		break;
  	case L3Globals.VIEW_NORMAL :
  		break;
  	}
    setFrameTitle("Lis connector " + fileConnector.getName());
  }
  	
  private boolean isPanelWithPoolSubPanel(){
  	return L3Application.getAppInstance().getMode() == L3Globals.APPLICATION_MODE_SAME_THREAD;
  }
  
	private void newDetailsPanelConfigView(){
  	Component comp = connector.getGuiComponent(LisConnectorDesc.FLD_NAME);
    comp.setEnabled(!isLocked);
    add("Name",comp, 0, 0);
    comp = connector.getGuiComponent(LisConnectorDesc.FLD_CONNECTOR_CLASS_NAME);
    add("Class Name", comp, 0, 1);
    comp.setEnabled(!isLocked);
	  comp = connector.getGuiComponent(LisConnectorDesc.FLD_ROOT_DIR);
    comp.setEnabled(!isLocked);
    add("Root Directory",comp, 0, 2);	
	  comp = connector.getGuiComponent(LisConnectorDesc.FLD_RECEIVE_DIR);
    comp.setEnabled(!isLocked);
    add("Receive Directory",comp, 0, 3);
	  comp = connector.getGuiComponent(LisConnectorDesc.FLD_SEND_DIR);
    comp.setEnabled(!isLocked);
    add("Send Directory",comp, 0, 4);
	  comp = connector.getGuiComponent(LisConnectorDesc.FLD_ARCHIVE_DIR);
    comp.setEnabled(!isLocked);
    add("Archive directory",comp, 0, 5);
	  comp = connector.getGuiComponent(LisConnectorDesc.FLD_ERROR_DIR);
    comp.setEnabled(!isLocked);
    add("Error directory",comp, 0, 6);
    if (isPanelWithPoolSubPanel()){
      comp = connector.getGuiComponent(LisConnectorDesc.FLD_POOL);
      comp.setEnabled(!isLocked);
      add("Related pool",comp,0,7);
    }
    comp = connector.getGuiComponent(LisConnectorDesc.FLD_FILE_IO_CLASS_NAME);
    comp.setEnabled(!isLocked);
    add("File IO class", comp, 0, 8);

    comp = add(connector, LisConnectorDesc.FLD_SERVICE_HOST, 0, 9);
    comp.setEnabled(!isLocked);

    comp = add(connector, LisConnectorDesc.FLD_SERVICE_PORT, 0, 10);
    comp.setEnabled(!isLocked);
    
	  FValidationPanel validPanel = showValidationPanel(true);
	  validPanel.addSubject(connector);
	}
  
  private void setValidationPanelListenerForConfigView(){
    
  }
  
  public void dispose(){
  	super.dispose();
  	connector = null;
  }
}