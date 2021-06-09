package b01.foc.tree.objectTree;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.gui.treeTable.FTreeTableModel;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;
import b01.foc.tree.FINode;
import b01.foc.tree.FNode;
import b01.foc.tree.FRootNode;
import b01.foc.tree.FTree;
import b01.foc.tree.TreeScanner;

public class FObjectTree extends FTree<FObjectNode> {

  private int displayFieldId = 0;
  private int fatherNodeId = 0;
  private FocList focList = null;
  private int depthVisibilityLimit = 0;
  private ArrayList<FocObject> storage = null;
  private Map<Integer, FINode> nodeMap = null;
  private boolean useRootNodeForCalculation = false;
  private boolean allowNodeNameDuplication = false;
  
  public FObjectTree(boolean useRootNodeForCalculation){
    super();
    init(useRootNodeForCalculation);
  }
  
  public FObjectTree(){
    this(false);
  }
  
  public FObjectTree(boolean useRootNodeForCalculation, int colorMode){
    super(colorMode);
    init(useRootNodeForCalculation);
  }
  
  private void init(boolean useRootNodeForCalculation ){
    root = new FObjectRootNode("root", this);
    setAllowNonLeavesDeletion(true);
    this.useRootNodeForCalculation = useRootNodeForCalculation;
    setAutomaticlyListenToListEvents(false);
  }
  
  public void dispose(){
    super.dispose();
    storage = null;
    nodeMap = null;
    focList = null;
    //descendentNodes = null;
  }
  
  @Override
  public Color getColorForLevel(int level) {
    
    return super.getColorForLevel(level);
  }

  public void setDisplayFieldId( int id ){
    displayFieldId = id;
  }
  
  public void setFatherNodeId ( int fnid ){
    fatherNodeId = fnid;
  }
  
  public int getDisplayFieldId (){
    return displayFieldId;
  }
  
  private int getMasterRef(FocObject focObj){
    FocObject obj = focObj.getPropertyObject(fatherNodeId);
    return obj != null ? obj.getReference().getInteger() : 0;
  }
  
  private ArrayList sortFocListAccordingToDependencies(FocList focList){
    ArrayList<FocObject> list = new ArrayList<FocObject>();
    for(int y = 0; y < focList.size(); y++){
      list.add(focList.getFocObject(y));
    }
    int n = list.size();
    for(int i = 1; i < n; i++){
      for(int j = 0; j < (n-i); j++){
        FocObject focObj1 = list.get(j);
        FocObject focObj2 = list.get(j+1);
        if(getMasterRef(focObj1) > getMasterRef(focObj2)){
          list.set(j, focObj2);
          list.set(j+1, focObj1);
        }else{
          list.set(j, focObj1);
          list.set(j+1, focObj2);
        }
      }
    }
    return list;
  }
  
  public void growTreeFromFocList(FocList focList){
    clearTree();
    
    this.focList = focList;
    //focList.r
    storage = new ArrayList<FocObject>();
    nodeMap = new HashMap<Integer, FINode>();
    ArrayList list = sortFocListAccordingToDependencies(focList);
    /*for(int i = 0; i < list.size(); i++){
      FocObject obj = (FocObject)list.get(i);
      System.out.println("ELIE "+ getMasterRef(obj) );  
    }*/
    
    grow(list);
    
    if( useRootNodeForCalculation ){
      FocObject focObj = focList.newEmptyDisconnectedItem();
      focObj.setPropertyString(displayFieldId, getRoot().getTitle());
      //focObj.lockAllproperties();
      getRoot().setObject(focObj);  
    }
    
  }
  
  //rr Begin
  public void addFocObject(FocObject childObject, FObjectNode fatherNode, String title){
    if(fatherNode == null){
      fatherNode = (FObjectNode) getRoot();
    }  
    FObjectNode node = new FObjectNode(title, fatherNode);
    node.setObject(childObject);
    fatherNode.addChild(node);  
  }
  //rr End
  
  public void grow(ArrayList list){
    FocObject pointedToFocObject = null;
    
    for(int j = 0; j < list.size(); j++){
      FocObject focObj = (FocObject)list.get(j);      
      FINode node = getRoot();      
      
      pointedToFocObject = focObj.getPropertyObject(fatherNodeId);
      
      if (pointedToFocObject == null){
        node = getRoot().addChild(focObj.getPropertyString(displayFieldId));
        node.setObject(focObj);
        nodeMap.put(focObj.getPropertyInteger(FField.REF_FIELD_ID), node);
      }else {
        FINode createdNode = nodeMap.get(pointedToFocObject.getPropertyInteger(FField.REF_FIELD_ID));
        if(createdNode != null){
          
          if( !isAllowNodeNameDuplication() ){
            createdNode = createdNode.addChild(focObj.getPropertyString(displayFieldId));  
          }else {
            createdNode  = new FObjectNode(focObj.getPropertyString(displayFieldId), (FObjectNode)createdNode);
            createdNode.getFatherNode().addChild((FObjectNode)createdNode);
          }
          
          createdNode.setObject(focObj);
          nodeMap.put(focObj.getPropertyInteger(FField.REF_FIELD_ID), createdNode);
          if(storage.size() > 0){
            storage.remove(focObj);  
          }
        }else{
          if (!storage.contains(focObj)){
            storage.add(focObj);  
          }
        }
      }
    }
    if(storage.size() > 0){
      grow(storage);
    }
  }
    
  @Override
  public int getDepthVisibilityLimit() {
    
    scan(new TreeScanner<FNode>() {

      public void afterChildren(FNode node) {
       if( node.isLeaf() ){
         int level = 0;
         while ( node != null && !(node instanceof FRootNode) ){
           level++;
           node = node.getFatherNode();
         }
         if( level > depthVisibilityLimit){
           depthVisibilityLimit = level;
         }
       }
      }

      public boolean beforChildren(FNode node) {
        // TODO Auto-generated method stub
        return true;
      }
      
    });
    //Globals.logString("Depth Visibility Limit: "+depthVisibilityLimit);
    return depthVisibilityLimit+1;/*1000;*/
  }

  @Override
  public FNode newEmptyNode(FTreeTableModel treeTableModel, FNode node) {
    //FTree tree = this/*treeTableModel.getTree()*/;
    FNode newNode = null;
    if( node != null ){
      int newItemNumber = 1;
      String newItemName = "New Item";
      while( node.findChild(newItemName) != null ){
        newItemName = "New Item ("+(++newItemNumber)+")";
      }
      
      FocObject newFocObj = focList.newEmptyItem();
      
      if( newFocObj != null ){
        newNode = (FNode)node.addChild(newItemName);
        newFocObj.setPropertyString(displayFieldId, newItemName);
        newNode.setObject(newFocObj);
        //newlyCreatedNode = (FObjectNode)newNode;
        FocObject targetObj = (FocObject)node.getObject();
        newFocObj.setPropertyObject(fatherNodeId, targetObj);
        treeTableModel.refreshTree(this, true);  
      }  
    }
    
    return newNode;
  }
  
/*  @Override
  public boolean deleteNode(FTreeTableModel treeTableModel, FNode node) {
    FocObject focObj = (FocObject)node.getObject();
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
              for( int i = 0; i < focList.size(); i++){
                FocObject fo = focList.getFocObject(i);
                System.out.println(fo.getPropertyString(displayFieldId));
              }
              
              System.out.println("Child Count before "+node.getChildCount());
              //traverseDescendents((FObjectNode)node);
              node.getFatherNode().removeChild(node);              
              deleteDescendents((FObjectNode)node);
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
  }*/
  
  //BElias
  /*@Override
	public FTree createSimilarTreeFromFocList(FocList list) {
  	FObjectTree newObjectTree = new FObjectTree();
  	newObjectTree.growTreeFromFocList(list);
  	return newObjectTree;
	}*/
  //EElias
  
/*  private ArrayList<FObjectNode> descendentNodes = new ArrayList<FObjectNode>();
  private void deleteDescendents(FObjectNode node ){
    for ( int i = 0; i < node.getChildCount(); i++){
      FObjectNode childNode = (FObjectNode)node.getChildAt(i);
      
      FocObject focObj = (FocObject)childNode.getObject();
      descendentNodes.add(childNode);
      System.out.println(focObj.getPropertyString(displayFieldId));
      focObj.setDeleted(true);
      
      if( childNode.getChildCount() > 0 ){
       deleteDescendents(childNode); 
      }
    }
  }*/
  
  /*private void deleteDescendents(FObjectNode node){
    node.dispose(true);
  }*/
  
//  private void removeChildNodes(){
//    for(int i = descendentNodes.size()-1; i >= 0; i--){
//      descendentNodes.get(i).getFatherNode().removeChild(descendentNodes.get(i));
//    }
//    //descendentNodes.clear();
//  }
  
//  public ArrayList<FObjectNode> getdescendentNodes(){
//    return descendentNodes;
//  }
  
  @Override
  public FProperty getTreeSpecialProperty(FTreeTableModel treeTableModel, int row) {
    FNode node = treeTableModel.getNodeForRow(row);
    FocObject focObj = (FocObject)node.getObject();
    return focObj.getFocProperty(getDisplayFieldId());
  }

  @Override
  public boolean isNodeLocked(FNode node) {
    FocObject focObject = (FocObject)node.getObject();
    return focObject != null ? focObject.getFocProperty(getDisplayFieldId()).isValueLocked() : true;
  }

  public FocList getFocList() {
    return focList;
  }

  @Override
  public Icon getIconForNode(FNode node) {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isAllowNodeNameDuplication() {
    return allowNodeNameDuplication;
  }

  public void setAllowNodeNameDuplication(boolean allowNodeNameDuplication) {
    this.allowNodeNameDuplication = allowNodeNameDuplication;
  }
  
/*  public void colorInheritedValues(final Color color){
    
    scan(new TreeScanner<FObjectNode>(){

      public void afterChildren(FObjectNode node) {
        FocObject focObject = (FocObject)node.getObject();
        
        if( focObject != null ){
          FocObject fatherObj = focObject != null ? (FocObject)focObject.getPropertyObject(fatherNodeId) : null;
          
          int nodeDepth = node.getNodeDepth();
          
          FocFieldEnum iter = new FocFieldEnum(focObject.getThisFocDesc(), focObject, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
          while(iter != null && iter.hasNext()){
            iter.next();
            FProperty prop = (FProperty) iter.getProperty();
            if(prop != null && prop.getFocField().isWithInheritance()){
              boolean equalPropVal = useRootNodeForCalculation ? focObject.getPropertyString(prop.getFocField().getID()).equals(((FocObject)getRoot().getObject()).getPropertyString(prop.getFocField().getID())) : false;
              if(nodeDepth == 1){
                if(equalPropVal && prop.isInherited()){
                  prop.setBackground(color);  
                }else{
                  prop.setBackground(getColorForLevel(nodeDepth));
                }
              }
              
              
              if(fatherObj != null ){
                
                equalPropVal = focObject.getPropertyString(prop.getFocField().getID()).equals(fatherObj.getPropertyString(prop.getFocField().getID()));
                //boolean noVal = prop.getObject() == null || prop.getObject().equals(new Double(0));
                boolean noVal = prop.isEmpty();
                
                if( (equalPropVal || noVal) && prop.isInherited()){
                  prop.setBackground(color);
                }else{
                  prop.setBackground(node.isLeaf() ? Color.WHITE : getColorForLevel(nodeDepth));
                }
              }
            }
          }
        }
        
      }

      public boolean beforChildren(FObjectNode node) {
        return true;
      }
      
    });
    
  }*/
  
}