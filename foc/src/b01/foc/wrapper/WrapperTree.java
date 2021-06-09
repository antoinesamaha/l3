package b01.foc.wrapper;

import b01.foc.desc.field.FField;
import b01.foc.list.FocList;
import b01.foc.tree.FTree;
import b01.foc.tree.objectTree.FObjectTree;

public class WrapperTree extends FObjectTree{

  public WrapperTree(FocList list, int viewID){
    super(true);
    setFatherNodeId(FField.FLD_FATHER_NODE_FIELD_ID);
    setDisplayFieldId(FField.FLD_NAME);
    growTreeFromFocList(list);
    
    setColorMode(FTree.COLOR_MODE_PREDEFINED);
  }
}
