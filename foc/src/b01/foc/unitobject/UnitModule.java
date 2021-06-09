package b01.foc.unitobject;

import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.desc.FocModuleInterface;
import b01.foc.menu.FMenuAction;
import b01.foc.menu.FMenuItem;
import b01.foc.menu.FMenuList;
import b01.foc.unitobject.dimension.DimensionDesc;
import b01.foc.unitobject.unit.UnitDesc;

public class UnitModule implements FocModuleInterface {

	public void declareFocObjects() {
		Application app = Globals.getApp();
		app.declaredObjectList_DeclareDescription(DimensionDesc.class);
    app.declaredObjectList_DeclareDescription(UnitDesc.class);
	}
	
  public static FMenuItem addDimensionMenu(FMenuList list){
		FMenuItem dimensionItem = new FMenuItem("Dimension", 'D', new FMenuAction(DimensionDesc.getInstance(), true)); 
    list.addMenu(dimensionItem);
    return dimensionItem;
	}
}
