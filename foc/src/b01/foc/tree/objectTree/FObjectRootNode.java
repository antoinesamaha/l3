package b01.foc.tree.objectTree;

import b01.foc.tree.FRootNode;
import b01.foc.tree.FTree;

public class FObjectRootNode extends FObjectNode implements FRootNode{
  private FTree tree = null;
  
  public FObjectRootNode(String title, FTree tree){
    super(title, null);
    setTree(tree);
  }

  public void setTree(FTree tree) {
    this.tree = tree;
  }
  
  public FTree getTree() {
    return tree;
  }
}
