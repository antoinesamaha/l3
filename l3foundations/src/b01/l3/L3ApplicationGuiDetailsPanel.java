package b01.l3;

import b01.foc.gui.FGCheckBox;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class L3ApplicationGuiDetailsPanel extends FPanel{
	
  public L3ApplicationGuiDetailsPanel (int view, L3Application application){
    add(application, L3ApplicationDesc.FLD_APPLICATION_MODE, 0, 0);
    FGCheckBox box = (FGCheckBox) add(application, L3ApplicationDesc.FLD_LAUNCH_AS_SERVICES, 0, 1);
    box.setText("");
    box = (FGCheckBox) add(application, L3ApplicationDesc.FLD_AUTOMATIC_PURGE, 0, 2);
    box.setText("");
    add(application, L3ApplicationDesc.FLD_PURGE_NBR_DAYS_TO_KEEP, 0, 3);
    add(application, L3ApplicationDesc.FLD_PURGE_NBR_DAYS_TO_KEEP_FOR_COMMITED, 0, 4);
    box = (FGCheckBox) add(application, L3ApplicationDesc.FLD_KEEP_FILES_FOR_DEBUG, 0, 5);
    box.setText("");
    add(application, L3ApplicationDesc.FLD_APPLICATION_DIRECTORY, 0, 6);
    add(application, L3ApplicationDesc.FLD_REMOTE_LAUNCHER_HOST, 0, 7);
    add(application, L3ApplicationDesc.FLD_REMOTE_LAUNCHER_PORT, 0, 8);
    
    FValidationPanel validPanel = showValidationPanel(true);
    validPanel.addSubject(application);
    setFrameTitle("General Configuration");
  }
  
  public void dispose(){
  	super.dispose();
  }
}
