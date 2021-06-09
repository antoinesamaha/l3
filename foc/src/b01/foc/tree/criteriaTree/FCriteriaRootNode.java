package b01.foc.tree.criteriaTree;

import b01.foc.tree.FRootNode;
import b01.foc.tree.FTree;

public class FCriteriaRootNode extends FCriteriaNode implements FRootNode{
  private FTree tree = null;
  
  public FCriteriaRootNode(String title, FNodeLevel info, FTree tree){
    super(title, info, null);
    setTree(tree);
  }

  public void dispose(){
  	super.dispose();
  	tree = null;
  }
  
  public void setTree(FTree tree) {
    this.tree = tree;
  }
  
  public FTree getTree() {
    return tree;
  }
  
  public void setTitle(String title){
    this.title = title;
  }
  
}
