package b01.foc.admin;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

public class MenuRightsDesc extends FocDesc{

  public static final int FLD_MENU_CODE = 1;
  public static final int FLD_GROUP     = 2;
  public static final int FLD_RIGHT     = 3;
  public static final int FLD_FATHER_MENU_RIGHT = 4;
  
  public static final int  ALLOW_FULL_ACCESS = 0;
  public static final int  ALLOW_HIDE        = 1;
  
  public MenuRightsDesc() {
    super(MenuRights.class, FocDesc.DB_RESIDENT, "MENU_RIGHTS", false);
    
    FField fField = addReferenceField();
    
    fField = new FCharField("MENU_CODE", "Menu Code", FLD_MENU_CODE, false, 20);
    addField(fField);
    
    FMultipleChoiceField fMultipleField = new FMultipleChoiceField("ALLOW", "Allow", FLD_RIGHT, false, 10);
    fMultipleField.addChoice(ALLOW_HIDE, "Hide");
    fMultipleField.addChoice(ALLOW_FULL_ACCESS, "Full Access");
    //fMultipleField.setWithInheritance(true);
    fMultipleField.setDisplayZeroValues(true);
    fMultipleField.addListener(new FPropertyListener(){

      public void dispose() {
      }

      public void propertyModified(FProperty property) {
        MenuRights fatherMenuRights = (MenuRights) property.getFocObject();
        FocList menuRightsList = (FocList) fatherMenuRights.getFatherSubject();
        boolean lock = false;
        if(fatherMenuRights.getRight() == ALLOW_HIDE){
          lock = true;
        }
        setChildrenMenuValue(menuRightsList, fatherMenuRights, lock);
      }
      
    });
    addField(fMultipleField);
    
    FObjectField objectField = new FObjectField("GROUP", "Group", FLD_GROUP, false, FocGroupDesc.getInstance(), "FOC_GROUP_", this, FocGroupDesc.FLD_MENU_RIGHTS_LIST);
    objectField.setDisplayField(FocGroupDesc.FLD_NAME);
    objectField.setComboBoxCellEditor(FocGroupDesc.FLD_NAME);
    objectField.setSelectionList(FocGroup.getList(FocList.NONE));
    objectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(objectField);
    
    objectField = new FObjectField("FATHER_MENU", "Father Menu", FLD_FATHER_MENU_RIGHT, false, this, "FATHER_MENU_");
    objectField.setDisplayField(FLD_MENU_CODE);
    objectField.setComboBoxCellEditor(FLD_MENU_CODE);
    objectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objectField.setWithList(false);
    objectField.setDBResident(false);
    addField(objectField);
    setFObjectTreeFatherNodeID(FLD_FATHER_MENU_RIGHT);
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
      list.setListOrder(new FocListOrder(FLD_MENU_CODE));
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
      focDesc = new /*XXX*/MenuRightsDesc();
    }
    return focDesc;
  }
  
  private void setChildrenMenuValue(FocList menuRightsList, MenuRights fatherMenuRights, boolean lock){
    for(int i=0; i < menuRightsList.size(); i++){
      MenuRights menuRights = (MenuRights) menuRightsList.getFocObject(i);
      if(menuRights.getFatherMenu() != null && menuRights.getFatherMenu().compareTo(fatherMenuRights) == 0){
        menuRights.setRight(fatherMenuRights.getRight());
        menuRights.getFocProperty(FLD_RIGHT).setValueLocked(lock);
      }
    }
  }
}
