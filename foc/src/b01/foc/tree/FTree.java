package b01.foc.tree;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import b01.foc.Globals;
import b01.foc.access.AccessSubject;
import b01.foc.desc.FocObject;
import b01.foc.gui.FColorProvider;
import b01.foc.gui.treeTable.FGTreeInTable;
import b01.foc.gui.treeTable.FTreeTableModel;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;
import java.util.Comparator;

public abstract class FTree<N> {
	protected FRootNode root = null;
  private boolean allowNonLeavesDeletion = false;
  private ArrayList<Color> levelsColors = null;
  private boolean automaticlyListenToListEvents = false;
  private Comparator<FNode> comparator = null;
  private HashMap <Object, FNode> object2NodeMap = null; 
  
  private int colorMode = COLOR_MODE_PREDEFINED;
  public final static int COLOR_MODE_GRADIENT   = 0;
  public final static int COLOR_MODE_PREDEFINED = 1;
  private boolean sortable = true;
  public abstract void growTreeFromFocList(FocList focList);
  public abstract int getDepthVisibilityLimit();
  public abstract FProperty getTreeSpecialProperty(FTreeTableModel treeTableModel, int row);
  public abstract boolean isNodeLocked(FNode node);
  public abstract FNode newEmptyNode(FTreeTableModel treeTableModel, FNode node);
  //public abstract boolean deleteNode(FTreeTableModel treeTableModel, FNode node);
  public abstract FocList getFocList();
  public abstract Icon getIconForNode( FNode node );
  //public abstract FTree createSimilarTreeFromFocList(FocList list);
  
  public FTree(){
    this(COLOR_MODE_PREDEFINED);
  }

  public FTree(int colorMode){
    levelsColors = new ArrayList<Color>();
    setColorMode(colorMode);
  }
  
  public void dispose(){
    if(root != null){
      root.dispose();
      root = null;
    }
    comparator = null;
    object2NodeMap = null;
	}
  
  public boolean isAutomaticlyListenToListEvents() {
		return automaticlyListenToListEvents;
	}
  
	public void setAutomaticlyListenToListEvents(boolean automaticlyListenToListEvents) {
		this.automaticlyListenToListEvents = automaticlyListenToListEvents;
	}

	private HashMap <Object, FNode> getObject2NodeMap(boolean create){
		if(object2NodeMap == null && create){
			object2NodeMap = new HashMap <Object, FNode> ();
			scan(new TreeScanner<FNode>(){
				public void afterChildren(FNode node) {
					FTree tree = node.getTree();
					if(tree != null){
						tree.putObject2NodeMapping(node.getObject(), node);
					}
				}

				public boolean beforChildren(FNode node) {
					return true;
				}
			});
		}
		return object2NodeMap;
	}
	
	public FNode findNode(Object obj){
		HashMap <Object, FNode> map = getObject2NodeMap(true);
		FNode node = map != null ? map.get(obj) : null;
		return node;
	}

	public void putObject2NodeMapping(Object obj, FNode node){
		if(obj != null && node != null){
			HashMap <Object, FNode> map = getObject2NodeMap(false);
			if(map != null) map.put(obj, node);
		}
	}

	public void removeObject2NodeMapping(Object obj){
		if(obj != null){
			HashMap <Object, FNode> map = getObject2NodeMap(false);
			if(map != null){
				map.remove(obj);
			}
		}
	}

  public Color getColorForNode(FNode node, int row, FGTreeInTable treeInTable ){
  	Color color = null;
    if((node != null && !node.isLeaf() || node instanceof FRootNode)){
    	int level = node.getNodeDepth();
      if (level > levelsColors.size()-1){
      	color = levelsColors.get(levelsColors.size()-1);
      }else{
      	color = levelsColors.get(level);
      }
    }
    
    return color;
  }
  
  public void setColorForLevel(int level, Color color){
    if( level < levelsColors.size()){
      levelsColors.set(level, color);  
    }else if(level == levelsColors.size()){
    	levelsColors.add(color);
		}
  }
  
  public Color getColorForLevel( int level ){
    if (level > levelsColors.size()-1){
      return levelsColors.get(levelsColors.size()-1);
    }
    return levelsColors.get(level);
    //return colorMode != COLOR_MODE_GRADIENT ? levelsColors.get(level) : null ;
  }
  
  public void initLevelsColors(){
	  if (getColorMode() == FTree.COLOR_MODE_GRADIENT){
	    int r    = 141;
	    int g    = 179;
	    int b    = 255;   
	    int step = (255 - 141) / (getDepthVisibilityLimit());
	    for(int i=0; i< getDepthVisibilityLimit()+1; i++){
	      int c = i ;
	      Color color = new Color(Math.min(r + c * step, 255), Math.min(g + c * step, 255), Math.min(b + c * step, 255));
	      setColorForLevel(c, color);
	    }  
	  }else{
      //setColorForLevel(0, new Color(176, 0, 88));
      //setColorForLevel(1, new Color( 69, 196, 233));
      //setColorForLevel(2, Color.GREEN);
      //setColorForLevel(3, Color.YELLOW);
	  	
      //setColorForLevel(0, new Color(255, 79, 79));
	  	
	  	
	  	//BElias
	  	//Make the colors in FColorProvider
	  	/*setColorForLevel(0, new Color(145, 145, 255));
      setColorForLevel(1, new Color(139, 255, 23));
      setColorForLevel(2, new Color(255, 255, 66));
      setColorForLevel(3, new Color(224, 228, 50));
	  	
      setColorForLevel(4, new Color(217, 85, 197));
      setColorForLevel(5, new Color(128, 128, 0));
      setColorForLevel(6, new Color(248,44,7));
      setColorForLevel(7, new Color(241,126,65));
      setColorForLevel(8, new Color(64, 128, 128));
      setColorForLevel(9, new Color(231, 255, 159));*/
      
	  	for(int i = 0; i < 10; i++){
	  		setColorForLevel(i, FColorProvider.getColorAt(i));
	  	}
      
      //EElias
	  }
  }
	
	public FRootNode getRoot(){
		return root;
	}  
	
	public void scan(TreeScanner scanner){
		if(scanner != null){
			FINode root = getRoot();
			if(root != null){
				root.scan(scanner);
			}
		}
	}
  
  public boolean isAllowNonLeavesDeletion() {
    return allowNonLeavesDeletion;
  }
  
  public void setAllowNonLeavesDeletion(boolean allowNonLeavesDeletion) {
    this.allowNonLeavesDeletion = allowNonLeavesDeletion;
  }
  
  public int getColorMode() {
    return colorMode;
  }
  
   public void setColorMode(int colorMode) {
    this.colorMode = colorMode;
    initLevelsColors();
  }
  
  public boolean isSortable(){
  	return this.sortable;
  }
  
  public void setSortable(boolean sortable){
  	this.sortable = sortable;
  }
  
  public void setSortable(Comparator<FNode> comparator){
  	this.comparator = comparator;
  	setSortable(true);
  }
  
  public Comparator<FNode> getComparator(){
  	if(this.comparator == null){
  		this.comparator = getDefaultComparator(); 		
  	}
  	return this.comparator;
  }
  
  protected Comparator<FNode> getDefaultComparator(){
  	return new Comparator<FNode>(){
  		public int compare(FNode node1, FNode node2){
  	  	return node1.getTitle().compareTo(node2.getTitle());
  	  }
  	}; 
  }
  
  public void sort(){
  	if(isSortable()){
	  	scan(new TreeScanner<FNode>(){
				public void afterChildren(FNode node){
					if(node.isSortable()){
						node.sortChildren();
					}
				}
				public boolean beforChildren(FNode node){
					return true;
				}
	  	});
  	}
  }
  
  public void clearTree(){
  	FNode rootNode = (FNode)getRoot();
  	if(rootNode != null && rootNode.getChildCount() > 0){
  		rootNode.dispose();
  	}
  }
  
  
  public boolean deleteNode(FTreeTableModel treeTableModel, FNode node) {
    FocObject focObj = (FocObject)node.getObject();
    FocList focList = getFocList();
    
    boolean deleted = false;
    try {
      if(focObj.isDeletable()){
        Globals.getDisplayManager().removeLockFocusForObject(focObj);
        StringBuffer message = new StringBuffer();

        int refNbr = focObj.referenceCheck_GetNumber(message);
        if(refNbr > 0){
          focObj.referenceCheck_PopupDialog(refNbr, message);
        }else{
          if (focObj != null) {
            int dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                "Confirm item deletion",
                "01Barmaja - Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null);
            
            switch(dialogRet){
            case JOptionPane.YES_OPTION:
              //We want to set the object status to deleted.
              //But since we might have removed the father status for autonomy reasons
              //We put it again for the moment just to make the list react
              AccessSubject fatherSubject = focObj.getFatherSubject();
              if(fatherSubject != focList){
                focObj.setFatherSubject(focList);
              }
              //focObj.setDeleted(true);
              //focList.remove(focObj);
              System.out.println("foclist before "+focList.size());
              /*for( int i = 0; i < focList.size(); i++){
                FocObject fo = focList.getFocObject(i);
                System.out.println(fo.getPropertyString(displayFieldId));
              }*/
              
              System.out.println("Child Count before "+node.getChildCount());
              //traverseDescendents((FObjectNode)node);
              node.getFatherNode().removeChild(node);              
              
              deleteDescendents(node);
              //removeChildNodes();
              if(fatherSubject != focList){
                focObj.setFatherSubject(fatherSubject);
              }
              
              System.out.println("Child Count after "+node.getChildCount());
              deleted = true;
              treeTableModel.refreshTree(this, true);
              break;
            case JOptionPane.NO_OPTION:
              break;
            }
          }
        }
      }else{
        Globals.getApp().getDisplayManager().popupMessage("This item cannot be deleted.\nFor further assistance please call 01Barmaja.");
      }
    }catch (Exception e2) {
      Globals.logException(e2);
    }
    return deleted;
  }
  
  private void deleteDescendents(FNode node){
    node.dispose(true);
  }
  
  public ArrayList<FNode> getLeafList(FNode node){
    final ArrayList<FNode> leafList = new ArrayList<FNode>();
    node.scan(new TreeScanner<FNode>(){
      public void afterChildren(FNode node) {
        if( node.isLeaf() ){
          leafList.add(node);
        }
      }
      public boolean beforChildren(FNode node) {
        return true;
      }
    });
    return leafList;
  }
  
  /*public void clearTree(){
  	FNode rootNode = (FNode)getRoot();
  	if(rootNode != null){
	  	for(int i = 0; i < rootNode.getChildCount(); i++){
	  		FNode node = rootNode.getChildAt(i);
		  	if(node != null ){
		  		node.dispose();
		  	}
	  	}
  	}
  }*/
  
  public FNode findNodeFromFocObject(FocObject focObject){
  	FNode node = findNode(focObject);
  	if(node == null){
	    final FocObject focObj = focObject;
	    final ArrayList<FNode> nodeList = new ArrayList<FNode>(1); 
	    scan(new TreeScanner<FNode>(){
	
	      public void afterChildren(FNode node) {
	        if( node.getObject() != null && node.getObject().equals(focObj)){
	          nodeList.add(node);
	        }
	      }
	
	      public boolean beforChildren(FNode node) {
	        return true;
	      }
	      
	    });
	    node = nodeList.size() > 0 ? nodeList.get(0) : null;
  	}
    return node;
  }
}