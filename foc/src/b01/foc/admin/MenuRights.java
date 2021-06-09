package b01.foc.admin;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;

public class MenuRights extends FocObject{

  public MenuRights(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void copy(MenuRights source){
    setGroup(source.getGroup());
    setFatherMenu(source.getFatherMenu());
    setCode(source.getCode());
    setRight(source.getRight());
  }
  
  public FocGroup getGroup(){
    return (FocGroup) getPropertyObject(MenuRightsDesc.FLD_GROUP);
  }
  
  public MenuRights getFatherMenu(){
    return (MenuRights) getPropertyObject(MenuRightsDesc.FLD_FATHER_MENU_RIGHT);
  }
  
  public String getCode(){
    return getPropertyString(MenuRightsDesc.FLD_MENU_CODE);
  }
  
  public int getRight(){
    return getPropertyMultiChoice(MenuRightsDesc.FLD_RIGHT);
  }
  
  public void setGroup(FocGroup group){
    setPropertyObject(MenuRightsDesc.FLD_GROUP, group);
  }
  
  public void setCode(String code){
    setPropertyString(MenuRightsDesc.FLD_MENU_CODE, code);
  }
  
  public void setFatherMenu(MenuRights menuRights){
    setPropertyObject(MenuRightsDesc.FLD_FATHER_MENU_RIGHT, menuRights);
  }
  
  public void setRight(int right){
    setPropertyMultiChoice(MenuRightsDesc.FLD_RIGHT, right);
  }
}
