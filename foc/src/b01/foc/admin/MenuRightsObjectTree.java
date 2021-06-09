package b01.foc.admin;

import b01.foc.list.FocList;
import b01.foc.tree.objectTree.FObjectTree;

public class MenuRightsObjectTree extends FObjectTree{

  public MenuRightsObjectTree(FocList list, int viewID){
    super();
    setFatherNodeId(MenuRightsDesc.FLD_FATHER_MENU_RIGHT);
    setDisplayFieldId(MenuRightsDesc.FLD_MENU_CODE);
    
    growTreeFromFocList(list);
    
  }
  
}
