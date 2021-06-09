package b01.foc.gui.table;

import java.util.ArrayList;

import b01.foc.Globals;
import b01.foc.admin.FocUser;
import b01.foc.db.SQLFilter;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FObjectField;
import b01.foc.gui.FPanel;
import b01.foc.list.FocLink;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.foc.property.FInt;
import b01.foc.property.FObject;
import b01.foc.property.FString;

//BElias save column visibility configuration in data base
public class ColumnsConfig extends FocObject{
  public ColumnsConfig(FocConstructor constr){
    super(constr);
    new FObject(this,FLD_USER,null);
    new FString(this,FLD_VIEW_KEY,"");
    new FInt(this,FLD_HIDEN_COL_ID,0);
  }

  @Override
  public FPanel newDetailsPanel(int viewID) {
    return null;
  }
  
  public FocUser getUser(){
    FObject user = (FObject)getFocProperty(FLD_USER);
    return (FocUser)user.getObject_CreateIfNeeded();
  }
  
  public void setUser(FocUser user){
    FObject objProp = (FObject) getFocProperty(FLD_USER);
    objProp.setObject(user);
  }
  
  public String getViewKey(){
    FString viewKey = (FString)getFocProperty(FLD_VIEW_KEY);
    return viewKey.getString();
  }
  
  public void setViewKey(String viewKey){
    FString viewProp = (FString)getFocProperty(FLD_VIEW_KEY);
    if(viewProp != null){
      viewProp.setString(viewKey);
    }
  }
  
  public int getColumnId(){
    FInt col = (FInt)getFocProperty(FLD_HIDEN_COL_ID);
    return col.getInteger();
  }
  
  public void setColumnID(int colID){
    FInt colProp = (FInt)getFocProperty(FLD_HIDEN_COL_ID);
    if(colProp != null){
      colProp.setInteger(colID);
    }
  }

  public static FocList newColumnsConfigFocList(String viewKey){
    FocLink link = new FocLinkSimple(getFocDesc());
    FocConstructor constr = new FocConstructor(getFocDesc(),null);
    ColumnsConfig configTemplate = (ColumnsConfig)constr.newItem();
    configTemplate.setUser(Globals.getApp().getUser());
    configTemplate.setViewKey(viewKey);
    SQLFilter filter = new SQLFilter(configTemplate,SQLFilter.FILTER_ON_SELECTED);
    filter.addSelectedField(FLD_USER);
    filter.addSelectedField(FLD_VIEW_KEY);
    FocList list = new FocList(link,filter);
    list.reloadFromDB();
    return list;
  }

  public static void disposeColumnsConfigFocList(FocList list){
    list.dispose();
    list = null;
  }

  public static ArrayList<Integer> getHidenColumnsForView(String viewKey){
    ArrayList<Integer> hidenColomns = new ArrayList<Integer>();
    FocList list = newColumnsConfigFocList(viewKey);
    for(int i = 0; i < list.size(); i++ ){
      ColumnsConfig config = (ColumnsConfig)list.getFocObject(i);
      hidenColomns.add(config.getColumnId());
    }
    disposeColumnsConfigFocList(list);
    return hidenColomns;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static final int FLD_USER = 1;
  public static final int FLD_VIEW_KEY = 2;
  public static final int FLD_HIDEN_COL_ID = 3;

  private static FocDesc focDesc = null;


  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(ColumnsConfig.class, FocDesc.DB_RESIDENT, "COLUMNS_CONFIG", true);

      focFld = focDesc.addReferenceField();

      focFld = new FObjectField("USER","User",FLD_USER,true,FocUser.getFocDesc(),"USER_");
      focDesc.addField(focFld);
      
      focFld = new FCharField("VIEW_ID","View id",FLD_VIEW_KEY,true,20);
      focDesc.addField(focFld);
      
      focFld = new FIntField("COL_ID","Column id",FLD_HIDEN_COL_ID,true,3);
      focDesc.addField(focFld);
    }
    return focDesc;
  }
}
//EElias
