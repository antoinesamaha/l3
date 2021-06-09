// EXTERNAL PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION

/*
 * Created on 20-May-2005
 */
package b01.foc.admin;

import b01.foc.Globals;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.list.*;

/**
 * @author 01Barmaja
 */
public class FocGroupDesc extends FocDesc{

  public static final int FLD_NAME = 1;
  public static final int FLD_DESCRIPTION = 2;
  public static final int FLD_APP_GROUP = 3;
  public static final int FLD_ALLOW_NAMING_MODIF = 4;
  public static final int FLD_CASH_DESKS_ACCESS = 5;
  public static final int FLD_RIGHTS_LEVEL = 6;
  public static final int FLD_ALLOW_CURRENCY_RATES_MODIF = 7;
  public static final int FLD_ALLOW_REPORT_ACCESS = 8;
  public static final int FLD_ALLOW_DATABASE_BACKUP = 9;
  public static final int FLD_ALLOW_DATABASE_RESTORE = 10;
  
  public static final int FLD_MENU_RIGHTS_LIST = 11;
  
  public static int FLD_START_APP_GROUPS = 100;
  private int numberOfAppGroupListFieldID = 0; 
  
  public static final int CASH_ACCESS_NONE = 0;
  public static final int CASH_ACCESS_VIEWER = 1; 
  public static final int CASH_ACCESS_COMPLETE = 2;
  public static final int CASH_ACCESS_OWNER = 3;  
  
  private static FocLinkOne2One link_AppGroup = null;
  
  public FocLinkOne2One getLink_AppGroup(){
    if(link_AppGroup == null){
    	if(FocGroup.getApplicationGroup() != null){
    		link_AppGroup = new FocLinkOne2One(this, FocGroup.getApplicationGroup());
    	}
    }
    return link_AppGroup;
  }
  
  public FocGroupDesc() {
    super(FocGroup.class, FocDesc.DB_RESIDENT, "FGROUP", true);
    FField focFld = null;

    addReferenceField();

    focFld = new FCharField("NAME", "Name", FLD_NAME, true, FCharField.NAME_LEN);
    focFld.setMandatory(true);
    addField(focFld);
    focFld.setLockValueAfterCreation(true);

    focFld = new FCharField("DESCRIP", "Description", FLD_DESCRIPTION, false, FCharField.DESC_LEN);
    addField(focFld);

    focFld = new FBoolField("NAME_MODF", "Allow naming modification", FLD_ALLOW_NAMING_MODIF, false);
    addField(focFld);
    
    if(getLink_AppGroup() != null){
	    focFld = new FListField("APP_GRP", "Application group", FLD_APP_GROUP, getLink_AppGroup());
	    addField(focFld);
    }
    
    if(Globals.getApp().isCurrencyModuleIncluded()){
      focFld = new FBoolField("FX_RTE_MODIF", "Allow Currency rates modification", FLD_ALLOW_CURRENCY_RATES_MODIF, false);
      addField(focFld);
    }

    if(Globals.getApp().isWithReporting()){
      focFld = new FBoolField("REPORT_ACCESS", "Allow reports access", FLD_ALLOW_REPORT_ACCESS, false);
      addField(focFld);
    }
    
    focFld = new FBoolField("DB_BACKUP", "Allow DB backup", FLD_ALLOW_DATABASE_BACKUP, false);
    addField(focFld);

    focFld = new FBoolField("DB_RESTORE", "Allow DB restore", FLD_ALLOW_DATABASE_RESTORE, false);
    addField(focFld);

    if(Globals.getApp().isCashDeskModuleIncluded()){
      FMultipleChoiceField focMultiFld = new FMultipleChoiceField("CASH_ACCESS", "Cash desk access", FLD_CASH_DESKS_ACCESS, false, 1);
      focMultiFld.addChoice(CASH_ACCESS_NONE, "None");
      focMultiFld.addChoice(CASH_ACCESS_VIEWER, "Viewer");
      focMultiFld.addChoice(CASH_ACCESS_COMPLETE, "Complete");
      addField(focMultiFld);
    }
    
    if(Globals.getApp().isWithRightsByLevel()){
      FMultipleChoiceField multiFocFld = new FMultipleChoiceField("RGHT_LEVEL", "Rights level", FLD_RIGHTS_LEVEL, false, 2);
      for(int i=1; i<=Globals.getApp().getRightsByLevel().getNbOfLevels(); i++){
        StringBuffer str = new StringBuffer();
        multiFocFld.addChoice(i, String.valueOf(i));
      }
      addField(multiFocFld);
    }
  }
  
  public int getAndIncrementNumberOfAppGroupListFieldID(){
    int number = FLD_START_APP_GROUPS + numberOfAppGroupListFieldID;
    numberOfAppGroupListFieldID++;
    return number;
  }
  
  public int getNumberOfAppGroupListFieldID(){
    return numberOfAppGroupListFieldID;
  }

  private static FocGroupDesc focDesc = null;
  
  public static FocGroupDesc getInstance(){
    if(focDesc == null){
      focDesc = new FocGroupDesc();
    }
    return focDesc;
  }
}
