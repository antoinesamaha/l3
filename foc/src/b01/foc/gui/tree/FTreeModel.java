package b01.foc.gui.tree;

import javax.swing.tree.TreePath;

import b01.foc.Globals;
import b01.foc.tree.FNode;
import b01.foc.tree.FTree;

public class FTreeModel extends FAbstractTreeModel{

	private FTree tree = null;
	
	public FTreeModel(FTree tree){
		this.tree = tree;
  }
	
	public FTree getTree(){
		return tree;
	}
	
	public Object getChild(Object parent, int index) {
		FNode parentNode = (FNode) parent;		
		FNode child = parentNode.getChildAt(index);
		return child;
	}

	public int getChildCount(Object parent) {
		FNode parentNode = (FNode) parent;
		return (parentNode == null || parentNode.getNodeDepth() >= tree.getDepthVisibilityLimit()) ? 0 : parentNode.getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		FNode parentNode = (FNode) parent;
		return parentNode.findChildIndex((FNode)child);
	}

	public Object getRoot() {		
		return tree.getRoot();
	}

	public boolean isLeaf(Object node){
    FNode parentNode = (FNode) node;
    boolean isLeaf = false;
    try{
      isLeaf = parentNode.getNodeDepth() >= tree.getDepthVisibilityLimit() ? true : parentNode.isLeaf();
    }catch(Exception e){
      Globals.logException(e);
    }
		return isLeaf;
  }

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

}
