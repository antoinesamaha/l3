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

import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class L3InstrumentGraphGuiDetailsPanel extends FPanel{
  
  public L3InstrumentGraphGuiDetailsPanel(FocObject focObj, int view){
    L3InstrumentGraph graph = (L3InstrumentGraph) focObj; 
    add(graph, L3InstrumentGraphDesc.FLD_INSTRUMENT_CODE, 0, 0);
    add(graph, L3InstrumentGraphDesc.FLD_SAMPLE_ID, 0, 1);
    add(graph, L3InstrumentGraphDesc.FLD_STATUS, 0, 2);

    add(graph, L3InstrumentGraphDesc.FLD_GRAPH, 0, 3);

	  FValidationPanel validPanel = showValidationPanel(true);
	  validPanel.addSubject(graph);
  }
}
