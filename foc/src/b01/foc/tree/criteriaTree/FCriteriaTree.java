package b01.foc.tree.criteriaTree;

import java.awt.Color;
import java.util.Comparator;

import javax.swing.Icon;

import b01.foc.desc.FocObject;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.treeTable.FGTreeInTable;
import b01.foc.gui.treeTable.FTreeTableModel;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;
import b01.foc.tree.FINode;
import b01.foc.tree.FNode;
import b01.foc.tree.FRootNode;
import b01.foc.tree.FTree;
import b01.foc.tree.TreeScanner;

public class FCriteriaTree extends FTree<FCriteriaNode>{
	private FTreeDesc treeDesc  = null;
	private FocList focList = null; 

  public FCriteriaTree(){
    this(FTree.COLOR_MODE_PREDEFINED);
  }

  public FCriteriaTree(int colorMode){
    super(colorMode);
    setAutomaticlyListenToListEvents(true);
  }
  
	public FCriteriaTree(FTreeDesc treeDesc){
    this();
    setTreeDesc(treeDesc);
	}

  public FCriteriaTree(FTreeDesc treeDesc, String rootTitle){
    this();
    setTreeDesc(treeDesc);
    FINode root = getRoot();
    if(root != null){
      root.setTitle(rootTitle);
    }
  }

	public void dispose(){
    scanTreeAndDisposeDisconnectedNonLeafItems();    
    super.dispose();

    if(treeDesc != null){
      treeDesc.dispose();
      treeDesc = null;
    }
    
   	focList = null; 
	}
  
  public void clearTree(){
    scanTreeAndDisposeDisconnectedNonLeafItems();
    super.clearTree();
  }
  
  private void scanTreeAndDisposeDisconnectedNonLeafItems(){
    scan(new TreeScanner<FCriteriaNode>(){
      public void afterChildren(FCriteriaNode node) {
        if(!node.isLeaf()){
          FocObject obj = (FocObject) node.getObject();
          if(obj != null){
            obj.dispose();  
          }
        }
      }

      public boolean beforChildren(FCriteriaNode node) {
        return true;
      }
    });
  }
  
  public void setTreeDesc(FTreeDesc treeDesc){
    this.treeDesc = treeDesc;
    /*if(treeDesc != null){
      root = new FCriteriaRootNode("Root", treeDesc.getNodeInfoForLevel(0), this);
    }*/
  }
  
  public FTreeDesc getTreeDesc(){
    return treeDesc;
  }
	
  public void growTreeFromFocList(FocList focList){
  	clearTree();
  	if(treeDesc != null){
      root = new FCriteriaRootNode("Root", treeDesc.getNodeInfoForLevel(0), this);
    }
  	this.focList = focList;
    for(int j = 0; j < focList.size(); j++){
      FocObject focObj = focList.getFocObject(j);

      //rr Begin
      /*FCriteriaTree   tree  = this;
      FocList fList = focList;
      FINode node = tree.getRoot();
      for(int i = 1; i<treeDesc.getNodeLevelsCount(); i++){
        FNodeLevel nodeLevel = treeDesc.getNodeLevelAt(i);
        FFieldPath path = nodeLevel.getPath();
        FProperty prop = path.getPropertyFromObject(focObj);
        String childName = prop == null ? " < None > " : prop.getString();
        node = node.addChild(childName);
        if(node.getObject() == null){
          if(i == treeDesc.getNodeLevelsCount()-1){
            //In this case we are in a leaf node
            node.setObject(focObj);
          }else{
            FocObject objDisconnected = fList.newEmptyDisconnectedItem();
            
            FProperty nodeObjectProp = path.getPropertyFromObject(objDisconnected, 0);
            FProperty firstLevelProp = path.getPropertyFromObject(focObj, 0);
            nodeObjectProp.setObject(firstLevelProp.getObject());
            
            node.setObject(objDisconnected);
          }
        }
      }
      sort();*/
      addFocObject(focObj);
      //rr End
    }
    
    if(!root.isLeaf()){
      FocObject objDisconnected = focList.newEmptyDisconnectedItem();
      root.setObject(objDisconnected);
    }
  }
  
  public void addFocObject(FocObject focObj){
    FCriteriaTree   tree  = this;
    FocList fList = focList;
    FINode node = tree.getRoot();
    for(int i = 1; i<treeDesc.getNodeLevelsCount(); i++){
      FNodeLevel nodeLevel = treeDesc.getNodeLevelAt(i);
      FFieldPath path = nodeLevel.getPath();
      FProperty prop = path.getPropertyFromObject(focObj);
      String childName = prop == null ? " < None > " : prop.getString();
      node = node.addChild(childName);
      if(node.getObject() == null){
        if(i == treeDesc.getNodeLevelsCount()-1){
          //In this case we are in a leaf node
          node.setObject(focObj);
        }else{
          FocObject objDisconnected = fList.newEmptyDisconnectedItem();
          
          FProperty nodeObjectProp = path.getPropertyFromObject(objDisconnected, 0);
          FProperty firstLevelProp = path.getPropertyFromObject(focObj, 0);
          nodeObjectProp.setObject(firstLevelProp.getObject());
          
          node.setObject(objDisconnected);
        }
      }
    }
    sort();
  }
  
  public boolean containsFieldPath(FFieldPath fieldPath){
    return (treeDesc != null)? treeDesc.containsFieldPath(fieldPath) : false;
  }
  
  //BElias
  /*@Override
	public FTree createSimilarTreeFromFocList(FocList list) {
		FTreeDesc treeDesc = getTreeDesc();
		FCriteriaTree newCriteriaTree = null;
		if(treeDesc != null){
			newCriteriaTree = new FCriteriaTree(getTreeDesc());
			newCriteriaTree.growTreeFromFocList(list);
		}
		return newCriteriaTree;
	}*/
  //EElias


  @Override
  public int getDepthVisibilityLimit() {
    return (treeDesc != null)? treeDesc.getDepthVisibilityLimit() : 1;
  }

  @Override
  public FNode newEmptyNode(FTreeTableModel treeTableModel, FNode node) {
  
    int newItemNumber = 1;
    String newItemName = "New Item";
    while( node.findChild(newItemName) != null ){
      newItemName = "New Item ("+(++newItemNumber)+")";
    }
    FocObject newFocObj = focList.newEmptyItem();
    FNode newChildNode = null;
    if( newFocObj != null ){
      newChildNode = ((FCriteriaNode)node).createChildNode(newItemName);
      newChildNode.setObject(newFocObj);
     
      
      FFieldPath path = null;
      for(int i = 0; i < getTreeDesc().getNodeLevelsCount(); i++){
        if( getTreeDesc().getNodeLevelAt(i).getPath() != null ){
          path = getTreeDesc().getNodeLevelAt(i).getPath();
        }
      }
      
      //path = ((FCriteriaNode)newChildNode).getNodeLevel().getPath();
      path.getPropertyFromObject(newFocObj, 0).setString(newChildNode.getTitle());
      node.addChild(newChildNode);
      
      FCriteriaNode criteriaNode = (FCriteriaNode)newChildNode.getFatherNode();
      while( !(criteriaNode instanceof FRootNode) ){
        FFieldPath fieldPath = criteriaNode.getNodeLevel().getPath();
        FProperty property = fieldPath.getPropertyFromObject((FocObject)criteriaNode.getObject(), 0);
        newFocObj.getFocProperty(property.getFocField().getID()).setObject(property.getObject());
        criteriaNode = (FCriteriaNode)criteriaNode.getFatherNode();
      }
    }
    //treeTableModel.refreshTree(this, true);
    return newChildNode;
  }
  
  
  @Override
  public boolean deleteNode(FTreeTableModel treeTableModel, FNode node) {
    
    FCriteriaNode criteriaNode = (FCriteriaNode)node;
    
    if(criteriaNode.isDisplayLeaf()){
      return super.deleteNode(treeTableModel, node);  
    }
    
    return false;
  }

  @Override
  public FProperty getTreeSpecialProperty(FTreeTableModel treeTableModel, int row) {
    
    FCriteriaNode node = (FCriteriaNode)treeTableModel.getNodeForRow(row);
    FFieldPath path = node.getNodeLevel().getPath();
    return path != null ? path.getPropertyFromObject((FocObject)node.getObject()) : null;
    
  }

  @Override
  public boolean isNodeLocked(FNode node) {
    return !node.isLeaf() && node.getNodeDepth() < treeDesc.getDepthVisibilityLimit() ? true : false;
  }

  @Override
  public FocList getFocList() {
    return focList;
  }

  @Override
  public Icon getIconForNode(FNode node) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public Color getColorForNode(FNode node, int row, FGTreeInTable treeInTable ){
  	Color col = null;
  	if((node != null && !node.isLeaf() && node.getNodeDepth() < treeDesc.getDepthVisibilityLimit()) || node instanceof FRootNode){
  		col = super.getColorForNode(node, row, treeInTable );
  	}
    
    return col;
  }
  
  protected Comparator<FNode> getDefaultComparator(){
		 return new Comparator<FNode>(){

			public int compare(FNode o1, FNode o2) {
				int res = 0;
				if(o1 != null && o2!= null){
					FCriteriaNode c1 = (FCriteriaNode) o1;
					FocObject focObject1 = (FocObject) o1.getObject();
					FocObject focObject2 = (FocObject) o2.getObject();
					FFieldPath fieldPath = c1.getNodeLevel().getPath();
					if(fieldPath != null){
						FProperty property1 = fieldPath.getPropertyFromObject(focObject1);
						FProperty property2 = fieldPath.getPropertyFromObject(focObject2);
						if(property1 != null && property2 != null){
							res = property1.compareTo(property2);
						}
					}									
				}
				return res;
			}
		 };
	 }
}