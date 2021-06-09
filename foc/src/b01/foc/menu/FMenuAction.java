/*
 * Created on 19-May-2005
 */
package b01.foc.menu;

import b01.foc.desc.*;
import b01.foc.gui.*;
import b01.foc.list.FocList;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FMenuAction extends FAbstractMenuAction {
	public FMenuAction(FocDesc focDesc, FocList list, boolean uniquePopup) {
		super(focDesc, list, uniquePopup);
	}

	public FMenuAction(FocDesc focDesc, FocList list, int viewID, boolean uniquePopup) {
		super(focDesc, list, viewID, uniquePopup);
	}

	public FMenuAction(FocDesc focDesc, boolean uniquePopup) {
		super(focDesc, uniquePopup);
	}

	@Override
	public FPanel generatePanel() {
		return focDesc.callNewBrowsePanel(list, viewID);
	}
}
