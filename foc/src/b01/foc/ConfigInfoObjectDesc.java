package b01.foc;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;

public class ConfigInfoObjectDesc extends FocDesc {
  public static final int FLD_PROPERTY            = 1;
  public static final int FLD_VALUE               = 2;
  
  public ConfigInfoObjectDesc(){
    super(ConfigInfoObject.class, FocDesc.NOT_DB_RESIDENT, "ConfigInfoObject", false);
    setGuiBrowsePanelClass(ConfigInfoObjectGuiBrowsePanel.class);  
    
    FField focFld = addReferenceField();
    focFld = new FCharField("PROPERTY", "Property", FLD_PROPERTY,  true, 30);    
    addField(focFld);
    
    focFld = new FCharField("VALUE", "Value", FLD_VALUE,  true, 30);    
    addField(focFld);
    
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
      FocListOrder order = new FocListOrder(FField.REF_FIELD_ID);
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
      focDesc = new /*XXX*/ConfigInfoObjectDesc();
    }
    return focDesc;
  }
}
