package b01.foc.unitobject.dimension;

import b01.foc.desc.FocObject;
import b01.foc.gui.FGTextField;
import b01.foc.gui.FPanel;
import b01.foc.list.FocList;
import b01.foc.unitobject.unit.UnitGuiBrowsePanel;

@SuppressWarnings("serial")
public class DimensionGuiDetailsPanel extends FPanel {

	private Dimension dimension = null;

  public DimensionGuiDetailsPanel(FocObject d, int view){
  	super("Dimension", FPanel.FILL_VERTICAL);  	
  	this.dimension = (Dimension) d;
  	  	
  	FGTextField textField = (FGTextField) add(dimension, DimensionDesc.FLD_NAME, 0, 0);
  	textField.setEnabled(false);
  	
  	FocList list = dimension.getUnitList(FocList.LOAD_IF_NEEDED);
  	FPanel panel = new UnitGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);
  	add(panel, 0, 1, 2, 1);
  }
  
  public void dispose(){
  	super.dispose();
  	dimension = null;
  }
}