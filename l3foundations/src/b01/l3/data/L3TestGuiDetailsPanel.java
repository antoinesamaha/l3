// PROPERTIES
// INSTANCE
// PANEL
//    STATIC LISTENERS
//    PANEL
// LIST
// DESCRIPTION


/*
 * Created on Jun 5, 2006
 */
package b01.l3.data;

import java.awt.Component;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class L3TestGuiDetailsPanel extends FPanel{

  public L3TestGuiDetailsPanel(int view, L3Test test){
  	Component comp = add(test, L3TestDesc.FLD_LABEL, 0, 0);
  	comp.setEnabled(false);
  	comp = add(test, L3TestDesc.FLD_VALUE, 0, 1);
  	comp.setEnabled(false);
  	comp = add(test, L3TestDesc.FLD_VALUE_NOTES, 0, 2);
  	comp.setEnabled(false);  	
  	comp = add(test, L3TestDesc.FLD_ALARM, 0, 3);
  	comp.setEnabled(false);
  	comp = add(test, L3TestDesc.FLD_UNIT_LABEL, 0, 4);
  	comp.setEnabled(false);
  	comp = add(test, L3TestDesc.FLD_RESULT_OK, 0, 5);
  	comp.setEnabled(false);
  	comp = add(test, L3TestDesc.FLD_MESSAGE, 0, 6);
  	comp.setEnabled(false);
	  
	  FValidationPanel validPanel = showValidationPanel(true);
	  validPanel.addSubject(test);
  }
}
