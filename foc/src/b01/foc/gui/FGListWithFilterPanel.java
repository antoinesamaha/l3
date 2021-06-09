/*
 * Created on 24 fvr. 2004
 */
package b01.foc.gui;

import b01.foc.list.FocList;
import b01.foc.list.FocListWithFilter;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGListWithFilterPanel extends FListPanel {
	
  public FGListWithFilterPanel() {
		super();
	}

	public FGListWithFilterPanel(FocList focList, int ddw) {
		super(focList, ddw);
	}

	public FGListWithFilterPanel(FocList focList) {
		super(focList);
	}

	public FGListWithFilterPanel(String frameTitle, int frameSizing, int panelFill) {
		super(frameTitle, frameSizing, panelFill);
	}

	public FGListWithFilterPanel(String panelTitle, int panelFill) {
		super(panelTitle, panelFill);
	}

	public void setFocList(FocList focList) throws Exception{
  	super.setFocList(focList);
  	FocListWithFilter lwf = (FocListWithFilter) focList; 
    this.fTableModel.getTableView().setFilter(lwf.getFocListFilter(), false);
  }

	@Override
	public void construct() {
		super.construct();
		showFilterButton(true);
	}
}