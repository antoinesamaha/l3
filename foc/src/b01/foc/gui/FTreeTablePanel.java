/*
 * Created on 24 fvr. 2004
 */
package b01.foc.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import b01.fab.gui.details.GuiDetails;
import b01.fab.gui.details.GuiDetailsComponent;
import b01.fab.gui.details.GuiDetailsDesc;
import b01.fab.model.table.UserDefinedObjectGuiDetailsPanel;
import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocFieldEnum;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.table.FTable;
import b01.foc.gui.treeTable.FGTreeInTable;
import b01.foc.gui.treeTable.FTreeTableModel;
import b01.foc.list.filter.FocListFilter;
import b01.foc.property.FProperty;
import b01.foc.tree.FNode;
import b01.foc.tree.FRootNode;
import b01.foc.tree.FTree;
import b01.foc.tree.TreeScanner;
import b01.foc.tree.criteriaTree.FCriteriaTree;
import b01.foc.tree.objectTree.FObjectTree;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FTreeTablePanel extends FAbstractListPanel implements MouseListener {
	private FGButtonAction expandAllAction = null;
	private FGButtonAction collapseAllAction = null;
  private FGButtonAction newAction = null;
  private FGButtonAction deleteAction = null;
  private FGButtonAction editAction = null;
  private FGButtonAction columnSelectorAction = null;
  private FGButtonAction filterAction = null;
  private FGButtonAction duplicateAction = null;
  private FNode newlyCreatedNode = null;
  
  public FTreeTablePanel(FTree fTree) {
  	super();
    this.fTableModel = new FTreeTableModel(fTree);
    getTableView().addTreeColumn(((FTreeTableModel)fTableModel).getGuiTree());
  }

  public void dispose(){
    if(fTable != null){
      fTable.removeMouseListener(this);
    }     
    
    super.dispose();
    expandAllAction = null;
    collapseAllAction = null;
    newAction = null;
    deleteAction = null;
    editAction = null;
    columnSelectorAction = null;
    filterAction = null;
    newlyCreatedNode = null;
    duplicateAction = null;
  }
  
  public void construct(){
  	super.construct();
    fTable.addMouseListener(this);
  	showCollapseAllButton(true);
  	showExpandAllButton(true);
    //rr Begin
    showPopUpMenu();
    //rr End
  }
  
  //rr Begin
  public void showPopUpMenu(){
    FPopupMenu popup =  getTable().getPopupMenu();
    
    JMenuItem collapse = new JMenuItem("Collapse up to this level", Globals.getIcons().getCollapseAllIcon() );
    collapse.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        
        int r = getTable().getSelectedRow();
        if( r != -1 ){
          FNode node = ((FTreeTableModel)getTableModel()).getNodeForRow(r);
          final int level = node.getNodeDepth();
          
          ((FTreeTableModel)getTableModel()).getTree().scan(new TreeScanner<FNode>(){

            public void afterChildren(FNode node) {
              
            }

            public boolean beforChildren(FNode node) {
              
              FGTreeInTable guiTree = ((FTreeTableModel)getTableModel()).getGuiTree();    
              if( node.getNodeDepth() == level ){
                int row = ((FTreeTableModel)getTableModel()).getRowForNode(node);
                guiTree.collapseRow(row);
              }
              
              return true;
            }
          });
        }
      }
    });
    popup.add(collapse);
    
    JMenuItem expand = new JMenuItem("Expand up to this level", Globals.getIcons().getExpandAllIcon() );
    expand.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        
        int r = getTable().getSelectedRow();
        if( r != -1 ){
          FNode node = ((FTreeTableModel)getTableModel()).getNodeForRow(r);
          final int level = node.getNodeDepth();
          final FGTreeInTable guiTree = ((FTreeTableModel)getTableModel()).getGuiTree();
          guiTree.expandAll();
          
          ((FTreeTableModel)getTableModel()).getTree().scan(new TreeScanner<FNode>(){

            public void afterChildren(FNode node) {
              
            }

            public boolean beforChildren(FNode node) {
              
              if( node.getNodeDepth() == level ){
                int row = ((FTreeTableModel)getTableModel()).getRowForNode(node);
                guiTree.collapseRow(row);
              }
              
              return true;
            }
          });
        }
      }
    });
    popup.add(expand);
  }
  //rr End

  public FGTreeInTable getGuiTree(){
  	FGTreeInTable treeInTable = null;
		FTreeTableModel tableModle = (FTreeTableModel)getTableModel();
		if(tableModle != null){
			treeInTable = tableModle.getGuiTree();
		}
		return treeInTable;
  }
  
  public FNode getSelectedNode(){
  	FNode node = null;
  	FTable table = getTable();
  	if(table != null){
		  int row = table.getSelectedRow();
		  FTreeTableModel treeTableModle = (FTreeTableModel)table.getTableModel();
		  if(treeTableModle != null){
		  	node = treeTableModle.getNodeForRow(row);
		  }
  	}
	  return node;
  }
  
	@Override
   public FGButtonAction getColumnSelectorAction() {
    if (columnSelectorAction == null) {
      columnSelectorAction = new FGButtonAction(null) {
        private static final long serialVersionUID = 1L;

        public void focActionPerformed(ActionEvent e) {
          try {
            getTableView().popupColumnConfigurationPanel(getTable());
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return columnSelectorAction;
  }

	@Override
  public FGButtonAction getDeleteAction() {
    if (deleteAction == null) {
    
    deleteAction = new FGButtonAction(null) {
        /**
         * 
         */
        private static final long serialVersionUID = -3052616839523635420L;

        public void focActionPerformed(ActionEvent e) {
          try {
            
            SwingUtilities.invokeLater(new Runnable(){
              public void run() {
                deleteItem();               
              }
            });

          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return deleteAction;
  }

	//-----------------------
  // Edit Action
  // -----------------------
  
  //BElias
  private Boolean shouldCreateNewInternalFrame(Object o){
    boolean shouldCreateNewInternalFrame = true;
    if (isUniquePoopUp()){
     InternalFrame internal = getObjectsInternalFramesMap().get(o);
      if (internal  != null){
        shouldCreateNewInternalFrame = false;
      }
    }
    return shouldCreateNewInternalFrame;
  }
  
  private void associateInternalFrameToObject(FocObject o, InternalFrame internal){
    getObjectsInternalFramesMap().put(o, internal);
  }
  
  private InternalFrame getInternalFrameAssociatedToObject(FocObject o){
    return getObjectsInternalFramesMap().get(o);
  }
  
  //EElias
  public void editCurrentItem(){
    InternalFrame internalFrame = null;
    Boolean restorSuccecfull = false;
    FPanel focObjPanel = null;
    try {
      FTable table = getTable();
      if(table != null){
        int row = table.getSelectedRow();
        FTreeTableModel treeTableModle = (FTreeTableModel)table.getTableModel();
        //BElias
        //FocObject focObj = (FocObject) table.getElementAt(row);
        FNode node = treeTableModle.getNodeForRow(row);
        FocObject focObj = node.getFocObjectToShowDetailsPanelFor();
        //EElias
        if (focObj != null) {
          //isLocking = focObj.isLocked(true);
          focObj.backup();
          //BElias moving this two lines inside if (shouldCreateNewInternalFrame(focObj))
          
          //FPanel focObjPanel = focObj.newDetailsPanel(getTableView().getDetailPanelViewID());
          //setSelectedObject(focObj);
          
          //EElias 
          //setCurrentDefaultFocusComponent(getTable());
          //BElias
          int viewId = node.getDetailsPanelViewId();
        	if(viewId == FNode.NO_SPECIFIC_VIEW_ID){
        		viewId = getTableView().getDetailPanelViewID();
        	}
          if (shouldCreateNewInternalFrame(focObj)){
            focObjPanel = focObj.newDetailsPanel(viewId);
            setSelectedObject(focObj);
            internalFrame = Globals.getDisplayManager().newInternalFrame(focObjPanel);
            associateInternalFrameToObject(focObj,internalFrame);
            restorSuccecfull = true;
          }else{
            restorSuccecfull = Globals.getDisplayManager().restoreInternalFrame(getInternalFrameAssociatedToObject(focObj));
          }
          if (!restorSuccecfull){
            //discardAssociatedInternalFrame(focObj);
            focObjPanel = focObj.newDetailsPanel(viewId);
            internalFrame = Globals.getDisplayManager().newInternalFrame(focObjPanel);
            associateInternalFrameToObject(focObj,internalFrame);
          }
          //EElias
        }
      }
    } catch (Exception e2) {
      Globals.logException(e2);
    } 
  }
  
  @Override
  public FGButtonAction getEditAction() {
    if (editAction == null) {
      editAction = new FGButtonAction(null) {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void focActionPerformed(ActionEvent e) {
          //if(!getTable().isEditing()){
          FTable table = getTable();
          setCurrentDefaultFocusComponent(table);
          editCurrentItem();
          /*
            try {
              FTable ftable = getTable();
              int row = ftable.getSelectedRow();
              FocObject focObj = (FocObject) ftable.getElementAt(row);
              if (focObj != null) {
                focObj.backup();
                FPanel focObjPanel = focObj.newDetailsPanel(FocObject.DEFAULT_VIEW_ID);
                setSelectedObject(focObj);
                
                Globals.getDisplayManager().changePanel(focObjPanel);
              }              
            } catch (Exception e2) {
              Globals.logException(e2);
            }
            */
          }
        //}
      };
    }
    return editAction;
  }

	@Override
	public FGButtonAction getEditCellAction() {
		return null;
	}

  @Override
  public FGButtonAction getFilterAction() {
    if (filterAction == null) {
      filterAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            FTable ftable = getTable();
            FTreeTableModel model = (FTreeTableModel) fTable.getModel();
            FocListFilter filter = model.getFilter();
            if(filter != null){
              FPanel panel = filter.newDetailsPanel(0);
              Globals.getDisplayManager().popupDialog(panel, "Filter", true);
            }
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return filterAction;
  }
	/*public FGButtonAction getFilterAction() {
		return null;
	}*/
  //BElie
	@Override
	public FGButtonAction getNewAction() {
    if (newAction == null) {
      newAction = new FGButtonAction(null) {
        /**
         * 
         */
        private static final long serialVersionUID = -3052616839523635420L;

        public void focActionPerformed(ActionEvent e) {

          try {
            SwingUtilities.invokeLater(new Runnable(){
              public void run() {
              	try{
              		newEmptyItem();
              	}catch(Exception e){
              		Globals.logException(e);
              	}
              }
            });

          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return newAction;
	}
  
	public void popupSelectANodeDialog(){
		Globals.getApp().getDisplayManager().popupMessage("Select a node first.");
	}
	
	public FTree getFTree(){
		return ((FTreeTableModel)fTableModel).getTree();
	}
  
  public FNode getNewlyCreatedNode(){
    return newlyCreatedNode;
  }
	
  public void insertNode(FNode fatherNode){
		FTree tree = getFTree();
    newlyCreatedNode = tree.newEmptyNode(((FTreeTableModel)fTableModel), fatherNode);
	}
	
  private void addGuiFieldsForCriteriaTree(FocObject newFocObj, GuiDetails detailsViewDefinition){
    
    FCriteriaTree tree =  (FCriteriaTree)getFTree();
    int nodeLevelsCount = tree.getTreeDesc().getNodeLevelsCount();
    int counterY = 0;
    ArrayList<FField> guiFieldList = new ArrayList<FField>();
    
    for( int i = 0; i < nodeLevelsCount; i++){
      FFieldPath path = tree.getTreeDesc().getNodeLevelAt(i).getPath();
      
      if( path != null ){
        FField fField = newFocObj.getFocProperty(path.get(0)).getFocField();
        GuiDetailsComponent guiDetailsComponent = detailsViewDefinition.addGuiDetailsFieldForFField(fField);
        guiFieldList.add(fField);
        alignGuiDetailComponents(guiDetailsComponent, 0, counterY++);
      }
    }
    
    FocFieldEnum iter = new FocFieldEnum(newFocObj.getThisFocDesc(), newFocObj, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      if(prop != null){
        FField field = prop.getFocField();
        if ( field != null && field.isMandatory() && !guiFieldList.contains(field)){
          GuiDetailsComponent guiDetailsComponent = detailsViewDefinition.addGuiDetailsFieldForFField(field);
          alignGuiDetailComponents(guiDetailsComponent, 0, counterY++);
        }
      }
    }
  }
  
  private void alignGuiDetailComponents(GuiDetailsComponent guiDetailsComponent, int x, int y){
    if(guiDetailsComponent != null){
      guiDetailsComponent.setX(0);
      guiDetailsComponent.setY(y);
    }
  }
  
  
  public FocObject newEmptyItem() {
  	
    FocObject newFocObj = null;
    int row = getTable().getSelectedRow();
    FNode node = ((FTreeTableModel)fTableModel).getNodeForRow(row);
    
    
    //FNode backUpNode = newlyCreatedNode;
    if( node != null && getFTree() instanceof FCriteriaTree /*&& (!node.isLeaf() || node instanceof FRootNode)*/){
      
      if( node.isLeaf() && !(node instanceof FRootNode)){
        insertNode(node.getFatherNode());
      }else{
        insertNode(node);  
      }
      
      
      if( newlyCreatedNode != null ){
        newFocObj = (FocObject) newlyCreatedNode.getObject();
        FocConstructor constr = new FocConstructor(GuiDetailsDesc.getInstance(), null);
        GuiDetails detailsViewDefinition = (GuiDetails)constr.newItem();
        
        detailsViewDefinition.setShowValidationPanel(true);
        detailsViewDefinition.setAddSubjectToValidationPanel(true);
        addGuiFieldsForCriteriaTree(newFocObj, detailsViewDefinition);
        
        
        detailsViewDefinition.setViewMode(GuiDetails.VIEW_MODE_ID_NORMAL);
        UserDefinedObjectGuiDetailsPanel panel = new UserDefinedObjectGuiDetailsPanel(newFocObj, detailsViewDefinition);
        panel.setMainPanelSising(FPanel.FILL_NONE);
        getTable().removeFocusListener(getTable());
        Globals.getDisplayManager().popupDialog(panel, "New Item", true, 200, 200);
        getTable().addFocusListener(getTable());
        
        ((FTreeTableModel)fTableModel).refreshTreeFromList();  
      }
      
    }else if (getFTree() instanceof FObjectTree){
      insertNode(node);
    }
    
    
    
    
    final FTreeTableModel tableModle = (FTreeTableModel)getTableModel();
    FGTreeInTable treeInTable = tableModle.getGuiTree();
    treeInTable.expandRow(row);
    
    if( newlyCreatedNode != null ){
      getTable().changeSelection(tableModle.getRowForNode(newlyCreatedNode), 0, false, false);    
      newFocObj = (FocObject) newlyCreatedNode.getObject();
      
      SwingUtilities.invokeLater(new Runnable(){
        public void run() {
          getTable().editCellAt(tableModle.getRowForNode(newlyCreatedNode), 0);
          TableCellEditor tableCellEditor = getTable().getCellEditor(tableModle.getRowForNode(newlyCreatedNode), 0);
          JTextField textField = (JTextField)tableCellEditor.getTableCellEditorComponent(getTable(), newlyCreatedNode, true, tableModle.getRowForNode(newlyCreatedNode), 0);
          textField.setSelectionStart(0);
          textField.requestFocusInWindow();
        }
        
      });
    }
    
    
    return newFocObj;
  }
  
  public boolean deleteItem() {
    FTree tree = ((FTreeTableModel)fTableModel).getTree();
    int row = getTable().getSelectedRow();
    FNode node = ((FTreeTableModel)fTableModel).getNodeForRow(row);
    return tree.deleteNode(((FTreeTableModel)fTableModel), node);
  }
  
  //EElie
	@Override
	public FGButtonAction getSelectAction() {
		return null;
	}

	@Override
	public FGButtonAction getExpandAllAction() {
    if (expandAllAction == null) {
    	expandAllAction = new FGButtonAction(null) {
        private static final long serialVersionUID = 1L;

        public void focActionPerformed(ActionEvent e) {
          try {
          	FTreeTableModel tableModle = (FTreeTableModel)getTableModel();
          	if(tableModle != null){
          		FGTreeInTable treeInTable = tableModle.getGuiTree();
          		if(treeInTable != null){
          			treeInTable.expandAll();
          		}
          	}
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return expandAllAction;
  }

	//-----------------------
  // Duplicate Action
  // -----------------------
  
  public FGButtonAction getDuplicateAction() {
    if (duplicateAction == null) {
      duplicateAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            //BGuiLock
            //if(getFocList().isContentValid())
            //if(!Globals.getDisplayManager().shouldLockFocus(true)){
            //EGuiLock
              FTable ftable = getTable();
              int row = ftable.getSelectedRow();
              if(row >= 0){
                FocObject sourceObj = (FocObject) ftable.getElementAt(row);
                
                if(sourceObj != null){
                  FocObject targetObj = sourceObj.duplicate(newEmptyItem(), sourceObj.getMasterObject(), true);
                  //getFocList().add(targetObj);
                }
              }
              
              /*
              boolean backupControler = getFocList().isControler();
              getFocList().forceControler(true);
              getFocList().validate();
              getFocList().forceControler(backupControler);
              getFocList().reloadFromDB();
              */
            //}
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return duplicateAction;
  }
  
  
	@Override
	public FGButtonAction getCollapseAllAction() {
    if (collapseAllAction == null) {
    	collapseAllAction = new FGButtonAction(null) {
        private static final long serialVersionUID = 1L;

        public void focActionPerformed(ActionEvent e) {
          try {
          	FTreeTableModel tableModle = (FTreeTableModel)getTableModel();
          	if(tableModle != null){
          		FGTreeInTable treeInTable = tableModle.getGuiTree();
          		if(treeInTable != null){
          			treeInTable.collapseAll();
          		}
          	}
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return collapseAllAction;
  }

	@Override
	public FGButtonAction getRedirectAction() {
		return null;
	}
  
//------------------------------------------------------------- 
  //-------------------------------------------------------------
  //Mouse listener
  //-------------------------------------------------------------
  //-------------------------------------------------------------

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  public void mouseClicked(MouseEvent e) {
    if(e.getClickCount()>=2){
      FTreeTableModel model = (FTreeTableModel) getTableModel();
      
      if(!model.getFocList().isDirectlyEditable()){
        if(isShowEditButton()){
          editCurrentItem();
        }
      }
    }else{
      getTable().requestFocusInWindow();
    }
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }
}
