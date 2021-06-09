package b01.foc.unitobject.dimension;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.list.FocLink;
import b01.foc.list.FocLinkForeignKey;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.unitobject.unit.UnitDesc;

public class DimensionDesc extends FocDesc {

	public static final int FLD_NAME                           = 1;
  public static final int FLD_DIMENSION_TYPE                 = 2;
  
  public static final int DIMENSION_TIME            = 1; 
  public static final int DIMENSION_DISTANCE        = 2; 
  public static final int DIMENSION_AREA            = 3;
  public static final int DIMENSION_WEIGHT          = 4;
  public static final int DIMENSION_VOLUME          = 5;
  public static final int DIMENSION_NUMBER          = 6;
  
	public DimensionDesc(){
		super(Dimension.class, FocDesc.DB_RESIDENT, "Dim", false);
		setGuiBrowsePanelClass(DimensionGuiBrowsePanel.class);	
		setGuiDetailsPanelClass(DimensionGuiDetailsPanel.class);
		
    FField focFld = addReferenceField();
    
    focFld = new FCharField("NAME", "Dimension", FLD_NAME,  false, 30);    
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    FMultipleChoiceField multipleChoice = new FMultipleChoiceField("DIMENSIONTYPE", "Dimention Type", FLD_DIMENSION_TYPE, false, 20);
    multipleChoice.addChoice(DIMENSION_TIME, "Time");
    multipleChoice.addChoice(DIMENSION_DISTANCE, "Distance");
    multipleChoice.addChoice(DIMENSION_AREA, "Area");
    multipleChoice.addChoice(DIMENSION_WEIGHT, "Weight");
    multipleChoice.addChoice(DIMENSION_VOLUME, "Volume");
    multipleChoice.addChoice(DIMENSION_NUMBER, "Number");
    multipleChoice.setSortItems(false);
    multipleChoice.setLockValueAfterCreation(true);
    addField(multipleChoice);
    
	}
	
	private static FocLink unitLink = null;
	public static FocLink getUnitLink(){
		if(unitLink == null){
			unitLink = new FocLinkForeignKey(UnitDesc.getInstance(), UnitDesc.FLD_DIMENSION, true);
		}
		return unitLink;
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
  		focDesc = new /*XXX*/DimensionDesc();
  	}
	  return focDesc;
	}
}
