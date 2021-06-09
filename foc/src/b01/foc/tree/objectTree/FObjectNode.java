package b01.foc.tree.objectTree;

import b01.foc.tree.FNode;

public class FObjectNode extends FNode{

	public FObjectNode(String title, FObjectNode fatherNode){
		setTitle(title);
		children = null;
    setFatherNode(fatherNode);
	}

	public void dispose(){
    super.dispose();
	}
  
  @Override
  public FNode createChildNode(String childTitle) {
    FObjectNode child = new FObjectNode(childTitle, this);
    return child;
  }

  @Override
  public int getLevelDepth() {
    // TODO Auto-generated method stub
    return 0;
  }

}
