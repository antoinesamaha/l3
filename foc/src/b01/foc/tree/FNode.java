package b01.foc.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import b01.foc.desc.FocObject;
import b01.foc.tree.criteriaTree.FCriteriaRootNode;

public abstract class FNode implements FINode{
	protected String           title               = "." ;
  protected ArrayList<FNode> children            = null;
  private   Object           object              = null;
  protected FNode            fatherNode          = null;
  
  public static final int    NO_SPECIFIC_VIEW_ID = -1;
  
  public abstract int getLevelDepth();
  public abstract FNode createChildNode(String childTitle);
  
	public void dispose(){
    dispose(false);
	}

  public void dispose(final boolean withSetDeleted){
    scan(new TreeScanner<FNode>(){
      public void afterChildren(FNode node) {
        if(node != null){
          if(withSetDeleted){
            FocObject focObj = (FocObject)(node).getObject();
            focObj.setDeleted(true);
          }
          
          if(node.children != null){
            node.children.clear();
          }
          node.children = null;
          node.setObject(null);
          node.fatherNode = null;
          node.title = null;          
        }
      }

      public boolean beforChildren(FNode node) {
        return true;
      }
    });
  }
  
  public String toString(){
    return title != null ? title : "";
  }
	
	public int compareTo(FNode o) {
		return getTitle().compareTo(o.getTitle());
	}

  @Override
  public boolean equals(Object obj) {
    /*if(toString() == null){
      Globals.logString("toString isNull");
    }
    if(obj == null){
      Globals.logString("obj isNull");
    }
    if(obj.toString() == null){
      Globals.logString("obj.toString isNull");
    }*/
    //return toString().equals(((FNode)obj).toString());
  	return toString().equals(obj.toString());
  }
  
  public FNode getSimilarNode(FNode node){
    /*FNode similarNode = null;
    if(node != null){
      if(this.equals(node)){
        similarNode = this; 
      }
      for(int i = 0; i < getChildCount() && similarNode == null; i ++){
        FNode childNode = getChildAt(i);
        similarNode = childNode.getSimilarNode(node);
      }
    }
    return similarNode;*/
  	
  	FNode similarNode = null;
  	if(node != null){
  		similarNode = getNodeByTitle(node.getTitle());
  	}
  	return similarNode;
  }
  
  public FNode getNodeByTitle(String title){
    FNode similarNode = null;
    if(getTitle() != null &&  title != null){
      if(getTitle().equals(title)){
        similarNode = this; 
      }
      for(int i = 0; i < getChildCount() && similarNode == null; i ++){
        FNode childNode = getChildAt(i);
        similarNode = childNode.getNodeByTitle(title);
      }
    }
    return similarNode;
  }

  public boolean isLeaf(){
		return children == null || children.isEmpty();
	}
	
  public int getNodeDepth(){
    int depth = 0;
    FINode node = this.getFatherNode();
    while(node != null){
      depth++;
      node = node.getFatherNode();
    }
    return depth;
  }
  
	public String getTitle(){
    return title;
	}
  
  public void setTitle(String title){
    this.title = title;
  }
  
  public FNode getFatherNode() {
    return fatherNode;
  }

  public void setFatherNode(FNode fatherNode) {
    this.fatherNode = fatherNode;
  }
  
  public boolean isRoot(){
    return getFatherNode() == null;
  }
  
  public FRootNode getRootNode(){
    FNode rootNode = this;
    while (!rootNode.isRoot()){
      rootNode = rootNode.getFatherNode();
    }
    return (FRootNode)rootNode;
  }
  
  static int debugCounter = 0;
  
  public FTree getTree(){
    FRootNode rootNode = getRootNode();
    debugCounter++;
    if(debugCounter == 1000){
    	int debug = 5;
    }
    return rootNode != null ? rootNode.getTree() : null;
  }
  
	public int findChildIndex(FNode child){
		int index = -1;
    if(children != null){
  		for(int i=0; i<children.size() && index < 0; i++){
  			if(((FNode)children.get(i)) == child){
  				index = i;
  			}
  		}
    }
		return index;
	}	
	
	public FNode findChild(String childTitle){
		FNode node = null;
		//BElie
    if(children != null){
      for(int i=0; i<children.size() && node == null; i++){
        int comp = ((FNode)children.get(i)).getTitle().compareTo(childTitle);  
        if(comp == 0){
          node = (FNode)children.get(i);
        }
      }  
    }
    //EElie
		return node;
	}
	
	public FINode addChild(String childTitle){
		FNode inserted = null;
		if(children == null){
			children = new ArrayList<FNode>();
		}else{
			inserted = findChild(childTitle);
		}
		
		if(inserted == null){
      inserted = createChildNode(childTitle);
			children.add(inserted);
		}
		
		return inserted;
	}
	//BElie
  public void addChild(FNode node){
    if(children == null){
      children = new ArrayList<FNode>();
    }
    children.add(node);
  }
  
  public void removeChild(FNode node){
    children.remove(node);
  }
  //EElie
  
  public FNode getChildAt(int at){
		FNode node = null;
		if(children != null){
			node = children.get(at);
		}
		
		return node ;
	}
  
	public int getChildCount(){
		return (children != null) ? children.size() : 0;
	}
	
	public boolean isSortable(){
		return true;
	}
	
	/*public void sortChildren(Comparator<FNode> comparator){
		if(children != null){
			if(isSortable()){
				Comparator<FNode> comparator = getComparator();
				if(comparator == null){
					Collections.sort(children);
				}else{
					Collections.sort(children, comparator);
				}
			}
		}
		
	}*/
	
	public void sortChildren(){
		Comparator<FNode> comparator = getTree().getComparator();
		if(children != null && isSortable()){
			Collections.sort(children, comparator);
		}
	}
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		FTree tree = getTree();
		if(object == null && tree != null){
			tree.removeObject2NodeMapping(this.object);
		}
		this.object = object;
		if(this.object != null){
			tree.putObject2NodeMapping(this.object, this);
		}
	}
	
	public void scan(TreeScanner scanner){
		if(scanner != null){
			boolean goInside = scanner.beforChildren(this);
			if(goInside && children != null){
				Iterator iter = children.iterator();
				while(iter != null && iter.hasNext()){
					FNode child = (FNode) iter.next();
					child.scan(scanner);
				}
			}
			scanner.afterChildren(this);			
		}
	}
  
  public void moveTo( FNode targetNode ){
    getFatherNode().removeChild(this);
    setFatherNode(targetNode);
    targetNode.addChild(this);
  }
  
  /*public void copyTo( FNode targetNode ){
    
  }*/
  
  public boolean isAncestorOf(FNode node){
    while ( node != getTree().getRoot()){
      node = node.getFatherNode();  
      if( node == this){
        return true;
      }
    }
    return false;
  }
  
  public FocObject getFocObjectToShowDetailsPanelFor(){
  	return (FocObject) getObject();
  }
  
  public int getDetailsPanelViewId(){
  	return FNode.NO_SPECIFIC_VIEW_ID;
  }
}
