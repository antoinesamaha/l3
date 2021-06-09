package b01.foc.unitobject.dimension;


import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;
import b01.foc.list.FocList;
import b01.foc.menu.FMenuAction;
import b01.foc.menu.FMenuItem;
import b01.foc.menu.FMenuList;

public class Dimension extends FocObject {
	private FocList unitList = null;
	
	public Dimension(FocConstructor constr){
    super(constr);
 	  newFocProperties();
  }
	
	public void dispose(){
    super.dispose();
    unitList = null;
  }
	
  public static Dimension getDimension(int type){
    FocList dimensionList = DimensionDesc.getList(FocList.LOAD_IF_NEEDED);
    return (Dimension)dimensionList.searchByPropertyIntValue(DimensionDesc.FLD_DIMENSION_TYPE, type);
  }
  
  public FocList getUnitList(int mode){
		if(unitList == null){
			unitList = new FocList(this, DimensionDesc.getUnitLink(), null);
			unitList.setDirectlyEditable(true);
      if(mode == FocList.LOAD_IF_NEEDED){
        unitList.loadIfNotLoadedFromDB();  
      }else if(mode == FocList.FORCE_RELOAD){
        unitList.reloadFromDB();
      }
		}
		return unitList;
	}
  
  public static void declareMenus(FMenuList fatherMenu){
  	FMenuItem menuItem = new FMenuItem("Dimension", 'D', new FMenuAction(DimensionDesc.getInstance(), true)); 
    fatherMenu.addMenu(menuItem);
  	
  }
}
