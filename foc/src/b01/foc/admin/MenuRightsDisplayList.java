package b01.foc.admin;

import b01.foc.Globals;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FFieldPath;
import b01.foc.list.DisplayList;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.menu.FMenu;
import b01.foc.menu.FMenuList;

public class MenuRightsDisplayList extends DisplayList{

  private FocList realList      = null;
  private FocList displayList   = null;
  private FocGroup group = null;
  
  public MenuRightsDisplayList(FocList realList, FocGroup group) {
    super(realList);
    this.group = group;
    setDoNotRemoveRealItems(false);
    construct();
  }
  
  public void dispose(){
    realList = null;
    displayList = null;
    group = null;
  }

  @Override
  public void completeTheDisplayList(FocList realList, FocList displayList) {
    this.realList = realList;
    this.displayList = displayList;
    displayList.setDbResident(false);
    //displayList.setLoaded(true);
    FocListOrder focListOrder = new FocListOrder();
    focListOrder.addField(FFieldPath.newFieldPath(MenuRightsDesc.FLD_MENU_CODE));
    FMenuList mainAppMenuList = (FMenuList) Globals.getApp().getMainAppMenu();
    searchAndAddFMenuItem(mainAppMenuList, null);
  }

  private void searchAndAddFMenuItem(FMenuList menuList, MenuRights fatherMenu){
    for(int i =0; i < menuList.menuCount(); i++){
      FMenu menu = menuList.getMenu(i);
      String menuCode = menu.getCode();
      
      if(menuCode != null){
        MenuRights existedMenuRights = (MenuRights) realList.searchByProperyStringValue(MenuRightsDesc.FLD_MENU_CODE, menuCode);
        if(existedMenuRights == null ){
          existedMenuRights = (MenuRights) displayList.newEmptyItem();
          
          existedMenuRights.setCode(menuCode);
          existedMenuRights.setFatherMenu(fatherMenu);
          existedMenuRights.setGroup(group);
          if(fatherMenu != null && fatherMenu.getRight() == MenuRightsDesc.ALLOW_HIDE){
            existedMenuRights.setRight(MenuRightsDesc.ALLOW_HIDE);
            existedMenuRights.getFocProperty(MenuRightsDesc.FLD_RIGHT).setValueLocked(true);
          }
          displayList.add(existedMenuRights);
        }else{
          MenuRights menuRightsInDisplay = (MenuRights) displayList.searchByProperyStringValue(MenuRightsDesc.FLD_MENU_CODE, existedMenuRights.getCode()); 
          if(menuRightsInDisplay != null){
           menuRightsInDisplay.setFatherMenu(fatherMenu);
          }
        }
        if (menu.isList()){
          searchAndAddFMenuItem((FMenuList)menu, existedMenuRights);
        }
      }
    }
    
  }
  
  @Override
  public void copyFromObjectToObject(FocObject target, FocObject source) {
    MenuRights tar = (MenuRights) target;
    MenuRights src = (MenuRights) source;
    
    tar.copy(src);
  }

  @Override
  public FocObject findObjectInList(FocList focList, FocObject object) {
    String menuCode = object.getPropertyString(MenuRightsDesc.FLD_MENU_CODE);
    return menuCode != null ? focList.searchByProperyStringValue(MenuRightsDesc.FLD_MENU_CODE, menuCode) : null;
  }

  @Override
  public boolean isDisplayItemToBeSaved(FocObject object) {
    MenuRights menuRights = (MenuRights)object;
    boolean yes = false;
    if(menuRights.getRight() == MenuRightsDesc.ALLOW_HIDE && !menuRights.getFocProperty(MenuRightsDesc.FLD_RIGHT).isValueLocked()){
      yes = true;
      menuRights.setFatherMenu(null);
    }
    return yes;
  }

}
