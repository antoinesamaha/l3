package b01.foc.tree.criteriaTree;

import b01.foc.desc.FocObject;
import b01.foc.desc.field.FFieldPath;
import b01.foc.property.FObject;
import b01.foc.property.FProperty;
import b01.foc.tree.FNode;
import b01.foc.tree.TreeScanner;

public class FCriteriaNode extends FNode{
  private FNodeLevel nodeInfo = null;

	public FCriteriaNode(String title, FNodeLevel level,FCriteriaNode fatherNode){
		setTitle(title);
		children = null;
    setNodeLevel(level);
    setFatherNode(fatherNode);
	}

	public void dispose(){
    super.dispose();
    nodeInfo = null;
	}

  public int getNodeDepth(){
    return getNodeLevel().getLevelDepth();
  }
  
  public FNodeLevel getNodeLevel() {
    return nodeInfo;
  }

  public void setNodeLevel(FNodeLevel level) {
    this.nodeInfo = level;
  }
  
  public int getLevelDepth(){
    FNodeLevel nodeLevel = getNodeLevel();
    return nodeLevel != null ? nodeInfo.getLevelDepth() : -1;
  }
  
  public FTreeDesc getTreeDesc(){
    FCriteriaTree tree = (FCriteriaTree) this.getTree();
    return tree != null ? tree.getTreeDesc() : null;
  }
  
  public boolean isSortable(){
  	boolean sortable = false;
  	FNodeLevel nodeLevel = getNodeLevel();
  	if(nodeLevel != null){
  		sortable = nodeLevel.isSortable();
  	}
  	return sortable;
  }
  
  public boolean doesAnyFatherContainFieldPath(FFieldPath fieldPath){
    boolean contains = false;
    FCriteriaNode fatherNode = (FCriteriaNode) getFatherNode();
    while(fatherNode != null && !contains){
      FNodeLevel fatherNodeLevel = fatherNode.getNodeLevel();
      FFieldPath fatherPath = fatherNodeLevel != null ? fatherNodeLevel.getPath() : null;
      contains = fatherPath != null && fieldPath.isEqualTo(fatherPath);
      fatherNode = (FCriteriaNode) fatherNode.getFatherNode();
    }
    return contains;
  }

	public FNode createChildNode(String childTitle){
		FCriteriaNode inserted = null;
    int thisNodeLevel = -1;
    FNodeLevel thisNodeInfo = getTreeDesc().getNodeInfoForLevel(getLevelDepth());
    thisNodeLevel = thisNodeInfo != null ? thisNodeInfo.getLevelDepth() : -1;
    FNodeLevel childInfo = getTreeDesc().getNodeInfoForLevel(thisNodeLevel + 1);
		inserted = new FCriteriaNode(childTitle, childInfo, this);
		
		return inserted;
	}
  
  public boolean isDisplayLeaf(){
    return isLeaf() && getNodeDepth() == getTreeDesc().getDepthVisibilityLimit();
  }
  
  public FocObject getFocObjectToShowDetailsPanelFor(){
  	LeafFinderScaner scaner = new LeafFinderScaner();
  	scan(scaner);
  	FocObject focObjectToShowDetaislPanelFor = scaner.getAnyLeafFocObject();
  	
  	if(focObjectToShowDetaislPanelFor != null){
	  	FNodeLevel nodeLevel = getNodeLevel();
	  	if(nodeLevel != null){
	  		FFieldPath fieldPath = nodeLevel.getPath();
	  		if(fieldPath != null){
	  			for(int i = 0 ; i < fieldPath.size(); i++){
	  				int at = fieldPath.get(i);
	  				FProperty prop = focObjectToShowDetaislPanelFor.getFocProperty(at);
	  				if(prop instanceof FObject){
		  				FocObject focObjFromProp = (FocObject) prop.getObject();
		  				if(focObjFromProp != null){
		  					focObjectToShowDetaislPanelFor = focObjFromProp;
		  				}
	  				}
	  			}
	  		}
	  	}
  	}
  	return focObjectToShowDetaislPanelFor;
  }
  
  public int getDetailsPanelViewId(){
  	int viewID = FNode.NO_SPECIFIC_VIEW_ID;
  	FNodeLevel nodeLevel = getNodeLevel();
  	if(nodeLevel != null){
  		viewID = nodeLevel.getDetailsPanelViewID();
  	}
  	return viewID;
  }
  
  private class LeafFinderScaner implements TreeScanner<FCriteriaNode>{
  	private FocObject anyLeafFocObject = null;
  	
		public void afterChildren(FCriteriaNode node) {
			if(node.isLeaf()){
				anyLeafFocObject = (FocObject) node.getObject();
			}
		}

		public boolean beforChildren(FCriteriaNode node) {
			return anyLeafFocObject == null;
		}
		
		private FocObject getAnyLeafFocObject(){
			return anyLeafFocObject;
		}
  	
  }
}
