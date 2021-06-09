package b01.foc.unitobject.unit;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FNumField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.unitobject.dimension.DimensionDesc;

public class UnitDesc extends FocDesc{
	
  public static final int FLD_SYMBOL              = 1;
  public static final int FLD_NAME                = 2;
  public static final int FLD_FACTOR              = 3;
  public static final int FLD_DIMENSION           = 4;
  
	public UnitDesc(){
		super(Unit.class, FocDesc.DB_RESIDENT, "Unit", true);
		setGuiBrowsePanelClass(UnitGuiBrowsePanel.class);	
		
    FField focFld = addReferenceField();
    
    focFld = new FCharField("NAME", "Name", FLD_NAME,  true, 30);    
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("SYMBOL", "Symbol", FLD_SYMBOL,  false, 5);    
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FNumField("FACTOR", "Factor", FLD_FACTOR,  false, 5, 5);    
    addField(focFld);
    
    FObjectField objFld = new FObjectField("DIMENSION", "Dimension", FLD_DIMENSION, false, DimensionDesc.getInstance(), "DIMENSION_");
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setSelectionList(DimensionDesc.getList(FocList.NONE));
    objFld.setMandatory(true);
    addField(objFld);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	private static FocList list = null;
	public static FocList getList(int mode){
		list = getInstance().getList(list, mode);
		list.setDirectlyEditable(true);
		list.setDirectImpactOnDatabase(false);
		if(list.getListOrder() == null){
			FocListOrder order = new FocListOrder(FLD_NAME);
			list.setListOrder(order);
		}
		return list;		
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
  	if (focDesc==null){
  		focDesc = new /*XXX*/UnitDesc();
  	}
	  return focDesc;
	}
}
