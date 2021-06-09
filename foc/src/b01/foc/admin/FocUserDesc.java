package b01.foc.admin;

import b01.foc.MultiLanguage;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FObjectField;
import b01.foc.desc.field.FPasswordField;
import b01.foc.list.FocList;
import b01.foc.list.FocListElement;
import b01.foc.list.FocListIterator;
import b01.foc.util.Encriptor;

public class FocUserDesc extends FocDesc{
	
  public static final String DB_TABLE_NAME = "FUSER";
  public static final String FLDNAME_NAME = "NAME";
  
  public static final int FLD_NAME      = 1;
  public static final int FLD_PASSWORD  = 2;
  public static final int FLD_GROUP     = 3;  
  public static final int FLD_LANGUAGE  = 4;
  public static final int FLD_FONT_SIZE = 5;
  
	public FocUserDesc(){
		super(FocUser.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		setGuiBrowsePanelClass(FocUserGuiBrowsePanel.class);
    setGuiDetailsPanelClass(FocUserGuiDetailsPanel.class);
    
    addReferenceField();

    FCharField focCharFld = new FCharField(FLDNAME_NAME, "Name", FLD_NAME, true, FCharField.NAME_LEN);
    focCharFld.setCapital(true);
    focCharFld.setMandatory(true);
    focCharFld.setLockValueAfterCreation(true);      
    addField(focCharFld);      

    FPasswordField focPassFld = new FPasswordField("PASSWORD", "Password", FLD_PASSWORD, false, 22);
    focPassFld.setCapital(true);
    addField(focPassFld);
    
    FObjectField focObjFld = new FObjectField("UGROUP", "Group", FLD_GROUP, false, FocGroup.getFocDesc(), "GRP_");
    focObjFld.setSelectionList(FocGroup.getList(FocList.NONE));
    focObjFld.setComboBoxCellEditor(FocGroupDesc.FLD_NAME);      
    //focObjFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(focObjFld);
    
    if(MultiLanguage.isMultiLanguage()){
      FMultipleChoiceField multiFld = new FMultipleChoiceField("LANGUAGE", "Language", FLD_LANGUAGE, false, 2);
      MultiLanguage.fillMutipleChoices(multiFld);
      addField(multiFld);
    }
    
    FMultipleChoiceField multiFld = new FMultipleChoiceField("FONT_SIZE", "Font size", FLD_FONT_SIZE, false, 2);
    multiFld.addChoice(8, "8");
    multiFld.addChoice(9, "9");
    multiFld.addChoice(10, "10");
    multiFld.addChoice(11, "11");
    multiFld.addChoice(12, "12");
    multiFld.addChoice(13, "13");
    multiFld.addChoice(14, "14");      
    multiFld.addChoice(15, "15");
    multiFld.addChoice(16, "16");
    multiFld.addChoice(17, "17");
    multiFld.addChoice(18, "18");
    multiFld.setSortItems(false);
    addField(multiFld);
	}

  public void beforeAdaptTableModel(){
  }
  
  public void afterAdaptTableModel(){
  	FocVersion dbVersion = FocVersion.getDBVersionForModule("FOC");
  	if(dbVersion != null && dbVersion.getId() <= 1503){
    	FocList usersList = FocUser.newList();
    	usersList.loadIfNotLoadedFromDB();
    	usersList.iterate(new FocListIterator(){
				public boolean treatElement(FocListElement element, FocObject focObj) {
					FocUser user = (FocUser) focObj;
					user.setPassword(Encriptor.encrypt_MD5(user.getPassword()));
					return false;
				}
    	});
    	usersList.validate(false);
  	}
  }
}
