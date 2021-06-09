/*
 * Created on 24 fvr. 2004
 */
package b01.foc.gui;

import b01.foc.gui.lock.ListFocusLock;
import b01.foc.gui.table.*;
import b01.foc.gui.table.cellControler.*;
import b01.foc.list.FocList;
import b01.foc.list.FocListElement;
import b01.foc.list.filter.FocListFilter;
import b01.foc.*;
import b01.foc.access.AccessSubject;
import b01.foc.desc.*;
import b01.foc.property.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FListPanel extends FAbstractListPanel implements MouseListener{
	
  private FGButtonAction newAction = null;
  private FGButtonAction deleteAction = null;
  private FGButtonAction editAction = null;
  private FGButtonAction selectAction = null;
  private FGButtonAction duplicateAction = null;
  private FGButtonAction filterAction = null;
  private FGButtonAction columnSelectorAction = null;
  private FGButtonAction redirectAction = null;
  
  private FPanel selectAllUnselectAllPanel = null;
  private FGButton selectAllButton = null;
  private FGButton unselectAllButton = null;
  private String selectAllLabel = "Select all";
  private String unselectAllLabel = "Unselect all";
  
  
  public FListPanel() {
  	super();
  }
  
  public FListPanel(FocList focList) {
  	try{
  		setFocList(focList);
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  }
  
  public FListPanel(String frameTitle, int frameSizing, int panelFill) {
  	super(frameTitle, frameSizing, panelFill);
  }
    
  public FListPanel(String panelTitle, int panelFill) {
  	super(panelTitle, panelFill);
  }

  public FListPanel(FocList focList, int ddw) {
  	super();
  	try{
  		setFocList(focList);
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  }
  
  public void dispose(){
    if(fTable != null){
      fTable.removeMouseListener(this);
    }
    
    if(getFocList() != null){
      getFocList().removeAttachedListPanel(this);
    }

    super.dispose();
    
    newAction = null;
    deleteAction = null;
    editAction = null;
    selectAction = null;
    duplicateAction = null;
    filterAction = null;
    columnSelectorAction = null;
    redirectAction = null;
  }

  public FListPanel getThis() {
    return this;
  }

  public void setFocList(FocList focList) throws Exception{
  	if(fTableModel != null){
  		throw new Exception("In ListPanel FocList should be set once!");
  	}
    this.fTableModel = new FTableModel(focList, this);
    focList.setAttachedListPanel(this);
  }
  
  
  //BElias moving this function to FAbstractListPanel
  /*public FocList getFocList(){
    return fTableModel != null ? ((FTableModel)fTableModel).getFocList() : null; 
  }*/
  //EElias
  
  public String getPanelName(){
    FocList focList = getFocList();
    FocDesc focDesc = focList.getFocDesc();
  	return focDesc.getStorageName();
  }
  
  public boolean isSelectionEnabled() {
    boolean b = false;
    FocList list = getFocList();
    b = list != null ? list.getSelectionProperty() != null : false;
    return b;
  }
  
  public FocDesc getFocDesc() {
    FocDesc desc = null;
    FocList focList = getFocList();
    if (focList != null) {
      desc = focList.getFocDesc();
    }
    return desc;
  }
  
  public void constructFromDescription(boolean withCheckBox) {
    getFocDesc().fillTableModelWithKeyFields(fTableModel, withCheckBox);
    construct();
  }

  public void constructFromDescription() {
    constructFromDescription(false);
  }

  public void construct(){
  	super.construct();
    fTable.addMouseListener(this);
  }
  
  public FocObject newEmptyItem() {
    FocObject focObj = null;
    FocList focList = getFocList();
    
    if (focList != null ) {
      focList.sort();
      boolean backup = focList.isDisableReSortAfterAdd();      
      focList.setDisableReSortAfterAdd(true);
      focObj = focList.newEmptyItem();
      focList.setDisableReSortAfterAdd(backup);
      if( focObj != null ){
        if(getTableView().getDetailPanelViewIDForNewItem() != -1){
          FPanel focObjPanel = focObj.newDetailsPanel(getTableView().getDetailPanelViewIDForNewItem());
          Globals.getDisplayManager().popupDialog(focObjPanel, focObjPanel.getFrameTitle(), true, 200, 200);
          
          setSelectedObject(focObj);
          if(getTableView().isEditAfterInsertion()){
            editCurrentItem();
          }
          
        }
        if( focObj.isCreated()){
          if (!getThis().isDirectlyEditable()) {
            setCurrentDefaultFocusComponent(getTable());
            FPanel focObjPanel = focObj.newDetailsPanel(getTableView().getDetailPanelViewID());
            Globals.getDisplayManager().changePanel(focObjPanel);
          }else{
            setSelectedObject(focObj);
            if(getTable() != null){
              getTable().setColumnSelectionInterval(0, 0);
            }
            requestFocusOnTable();
            //BGuiLock
            ListFocusLock listFocusLock = new ListFocusLock(getTable(), getFocList(), focObj);
            Globals.getDisplayManager().setFocusLock(listFocusLock);        
            //EGuiLock        
          }
        }
      }
    }
    
    return focObj;
  }
  
  public String getSelectAllLabel() {
		return selectAllLabel;
	}

	public void setSelectAllLabel(String selectAllLabel) {
		this.selectAllLabel = selectAllLabel;
		if(selectAllButton != null){
			selectAllButton.setText(this.selectAllLabel); 
		}
	}

	public String getUnselectAllLabel() {
		return unselectAllLabel;
	}

	public void setUnselectAllLabel(String unselectAllLabel) {
		this.unselectAllLabel = unselectAllLabel;
		if(unselectAllButton != null){
			unselectAllButton.setText(this.unselectAllLabel);
		}
	}
  
  public FPanel getSelecAllUnselectAllPanel(){
  	if(selectAllUnselectAllPanel == null){
  		selectAllUnselectAllPanel = new FPanel();
  		FGButton selectAll = createSelectAllButton();
  		FGButton unselectAll = createUnselectAllButton();
  		selectAllUnselectAllPanel.add(selectAll,0,0);
  		selectAllUnselectAllPanel.add(unselectAll,1,0);
  		selectAllUnselectAllPanel.setFill(FPanel.FILL_NONE);
  	}
  	return selectAllUnselectAllPanel;
  }
  
  private FGButton createSelectAllButton(){
  	if(selectAllButton == null){
  		selectAllButton = new FGButton(getSelectAllLabel());
  		selectAllButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
          FTable table = getTable();
          FTableModel model = (FTableModel)table.getTableModel();
          for (int i = 0; i < model.getRowCount(); i++) {
            FocListElement listElmt = model.getRowListElement(i);
            if (listElmt != null) {
            	FProperty selectedProperty = listElmt.getSelectedProperty();
            	if(selectedProperty != null && !selectedProperty.isValueLocked()){
            		listElmt.setSelected(true);
            	}
            }
          }
          model.fireTableDataChanged();
				}
  		});
  	}
  	return selectAllButton;
  }
  
  private FGButton createUnselectAllButton(){
  	if(unselectAllButton == null){
  		unselectAllButton = new FGButton(getUnselectAllLabel());
  		unselectAllButton.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
          FTable table = getTable();
          FTableModel model = (FTableModel)table.getTableModel();
          for (int i = 0; i < model.getRowCount(); i++) {
            FocListElement listElmt = model.getRowListElement(i);
            if (listElmt != null) {
            	FProperty selectedProperty = listElmt.getSelectedProperty();
            	if(selectedProperty != null && !selectedProperty.isValueLocked()){
            		listElmt.setSelected(false);
            	}
            }
          }
          model.fireTableDataChanged();
				}
  		});
  	}
  	return unselectAllButton;
  }
  
  // -----------------------
  // New Action
  // -----------------------
  public FGButtonAction getNewAction() {
    if (newAction == null) {
      newAction = new FGButtonAction(null) {
        /**
         * 
         */
        private static final long serialVersionUID = -3052616839523635420L;

        public void focActionPerformed(ActionEvent e) {
          try {
            //BGuiLock
            //if(!getTable().isEditing() && getFocList().isContentValid())
            //getTable().getEditorComponent().getE
            
            /*if(getTable().isEditing()){
              getTable().
            }*/
            //if(requestFocusInWindow()){
          	SwingUtilities.invokeLater(new Runnable(){
							public void run() {
	              newEmptyItem();								
							}
          	});

            //}
            
            //if(/*!getTable().isEditing() && */!Globals.getDisplayManager().shouldLockFocus(true)){
            //EGuiLock
            //  newEmptyItem();
            //}
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
    }
    return newAction;
  }

  // -----------------------
  // Delete Action
  // -----------------------
  public FGButtonAction getDeleteAction() {
    if (deleteAction == null) {
      deleteAction = new FGButtonAction(null) {
        /**
         * 
         */
        private static final long serialVersionUID = -429900189381760891L;

        public void focActionPerformed(ActionEvent e) {// ici
          FocObject highLightedObject = getFocList().getSelectedObject();
          //if(!getTable().isEditing()){
            try {
              FTable ftable = getTable();
              int row = ftable.getSelectedRow();
              if(row >= 0){
                FocObject focObj = (FocObject) ftable.getElementAt(row);
                if(focObj.isDeletable()){
                  Globals.getDisplayManager().removeLockFocusForObject(focObj);
                  
                  StringBuffer message = new StringBuffer();
                  int refNbr = focObj.referenceCheck_GetNumber(message);
                  if(refNbr > 0){
                    focObj.referenceCheck_PopupDialog(refNbr, message);
                  }else{
                    highLightedObject = focObj; 
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
                        if(fatherSubject != getFocList()){
                          focObj.setFatherSubject(getFocList());
                        }
                        focObj.setDeleted(true);
                        if(fatherSubject != getFocList()){
                          focObj.setFatherSubject(fatherSubject);
                        }
                        highLightedObject = (FocObject) ftable.getElementAt(row);
                        if(highLightedObject == null && row > 0){
                          highLightedObject = (FocObject) ftable.getElementAt(row - 1);                      
                        }
                        break;
                      case JOptionPane.NO_OPTION:
                        break;
                      }
                    }
                  }
                }else{
                  Globals.getApp().getDisplayManager().popupMessage("This item cannot be deleted.\nFor further assistance please call 01Barmaja.");
                }
              }
            } catch (Exception e2) {
              Globals.logException(e2);
            }
          //}
          
          setSelectedObject(highLightedObject);
          requestFocusOnTable();
        }
      };
    }
    return deleteAction;
  }

  // -----------------------
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
        FocObject focObj = (FocObject) table.getElementAt(row);
        if (focObj != null) {
          //isLocking = focObj.isLocked(true);
          focObj.backup();
          //BElias moving this two lines inside if (shouldCreateNewInternalFrame(focObj))
          
          //FPanel focObjPanel = focObj.newDetailsPanel(getTableView().getDetailPanelViewID());
          //setSelectedObject(focObj);
          
          //EElias 
          //setCurrentDefaultFocusComponent(getTable());
          //BElias
          if (shouldCreateNewInternalFrame(focObj)){
            focObjPanel = focObj.newDetailsPanel(getTableView().getDetailPanelViewID());
            setSelectedObject(focObj);
            internalFrame = Globals.getDisplayManager().newInternalFrame(focObjPanel);
            associateInternalFrameToObject(focObj,internalFrame);
            restorSuccecfull = true;
          }else{
            restorSuccecfull = Globals.getDisplayManager().restoreInternalFrame(getInternalFrameAssociatedToObject(focObj));
          }
          if (!restorSuccecfull){
            //discardAssociatedInternalFrame(focObj);
            focObjPanel = focObj.newDetailsPanel(getTableView().getDetailPanelViewID());
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

  // -----------------------
  // Select Action
  // -----------------------
  public FGButtonAction getSelectAction() {
    if (selectAction == null) {
      selectAction = new FGButtonAction (null) {      

        public void focActionPerformed(ActionEvent e) {
          //BGuiLock
          //if(!getTable().isEditing() && getFocList().isContentValid())
          //if(!getTable().isEditing() && !Globals.getDisplayManager().shouldLockFocus(true)){
          //EGuiLock
            try {
              /*
               * FTable ftable = getTable(); int row = ftable.getSelectedRow();
               * FocObject focObj = (FocObject) ftable.getElementAt(row); if
               * (focObj != null) {
               */
              Globals.getDisplayManager().goBack();
              FocList focList = getFocList();
              if (focList != null) {
                focList.validateSelectedObject();
              }
              /*
               * if (focList != null) {
               * focList.fireEvent(FocEvent.ID_ITEM_SELECT); }
               */
              // }
            } catch (Exception e2) {
              Globals.logException(e2);
            }
          }
        //}
      };
    }
    return selectAction;
  }

  // -----------------------
  // Edit Cell Action
  // -----------------------
  public FGButtonAction getEditCellAction() {
    if (selectAction == null) {
      selectAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          FTable ftable = getTable(); 
          int selRow = ftable.getSelectedRow();
          int selCol = ftable.getSelectedColumn();
          FocObject focObj = (FocObject) ftable.getElementAt(selRow);
          if (focObj != null) {
            FTableModel tableModel = (FTableModel) ftable.getModel();
            FTableView tableView = tableModel.getTableView();
            FTableColumn fColumn = tableView.getColumnAt(selCol);
            if(fColumn != null){
              AbstractCellControler cellEditor = fColumn.getCellEditor();
              if(cellEditor != null){
                cellEditor.editRequested(ftable, selRow, selCol);
              }
            }
          }
        }
      };
    }
    return selectAction;
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
              duplicate();
              
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
  
  public void duplicate(){
    FTable ftable = getTable();
    int row = ftable.getSelectedRow();
    if(row >= 0){
      FocObject sourceObj = (FocObject) ftable.getElementAt(row);
      
      if(sourceObj != null){
        sourceObj.duplicate(newEmptyItem(), sourceObj.getMasterObject(), true);
      }
    }
  }

  // -----------------------
  // Filter Action
  // -----------------------
  public FGButtonAction getFilterAction() {
    if (filterAction == null) {
      filterAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            FTable ftable = getTable();
            FTableModel model = (FTableModel) ftable.getModel();
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
  
  // -----------------------
  // Column selector Action
  // -----------------------
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
  
  //-----------------------
  // Redirect Action
  // -----------------------
  
  @Override
	public FGButtonAction getRedirectAction() {
		if(redirectAction == null && Globals.getApp().getGroup().allowNamingModif()){
			redirectAction = new FGButtonAction(null){
				@Override
				public void focActionPerformed(ActionEvent e) {
					FocObject focObjectToRedirectFrom = getSelectedObject();
					if(focObjectToRedirectFrom != null){
						FocDesc focDesc = getFocList().getFocDesc();
						FocList listToChooseFrom = focDesc.getList(null, FocList.LOAD_IF_NEEDED);
						FObject selectionProperty = new FObject(null, 1, null);
						listToChooseFrom.setSelectionProperty(selectionProperty);
						FAbstractListPanel listPanel = listToChooseFrom.getSelectionPanel(false);
						if(listPanel != null){
							listPanel.showFilterButton(listPanel.getTableView().getFilter() != null);
							listPanel.showAddButton(false);
							listPanel.showRemoveButton(false);
							listPanel.showEditButton(false);
							listPanel.showDuplicateButton(false);
							listPanel.showRedirectButtonInPopupMenu(false);
							
							FValidationPanel validPanel = (FValidationPanel) listPanel.getValidationPanel();
							validPanel.setSelectionType(FValidationPanel.SELECTION_ENABLED);
							Globals.getDisplayManager().popupDialog(listPanel, "Choose new object", true);
							selectionProperty = listToChooseFrom.getSelectionProperty();
							FocObject focObjectToRedirectTo = (FocObject) selectionProperty.getObject();
							if(focObjectToRedirectTo != null){
								focObjectToRedirectFrom.referenceCheck_RedirectToNewFocObject(focObjectToRedirectTo);
							}
						}
					}
				}
			};
		}
  	return redirectAction;
	}
  
  //-----------------------
  // Expand All Action
  // -----------------------
	@Override
	public FGButtonAction getExpandAllAction() {
		return null;
	}
	
  //-----------------------
  // Collapse All Action
  // -----------------------
	@Override
	public FGButtonAction getCollapseAllAction() {
		return null;
	}
  
  //-----------------------
  // Print Action
  // -----------------------

  /**
   * @return
   */
  public boolean isDirectlyEditable() {
    return getFocList().isDirectlyEditable();
  }

  /**
   * @param b
   */
  public void setDirectlyEditable(boolean b) {
    getFocList().setDirectlyEditable(b);
    /*
    if (b && getFocList().isDirectImpactOnDatabase()) {
      Exception e = new Exception("List Panel cannot be DIRECTLY_EDITABLE when list is DIRECT_IMPACT_ON_DATABASE");
      Globals.logException(e);
    } else {
      getFocList().setWaitForValidationToAddObject(false);
      directlyEditable = b;
      if (directlyEditable) {
        FTableView tableView = this.getTableView();
        if (tableView != null) {
          for (int i = 0; i < tableView.getColumnCount(); i++) {
            FTableColumn col = tableView.getColumnAt(i);
            if (col != null) {
              col.setEditable(true);
            }
          }
        }
      }
    }
    */
  }
  
/*  public void requestFocusOnCurrentItem(){
    FTable table = getTable();
    FTableModel fTabMod = (FTableModel) fTableModel;  
    if(fTabMod != null && table != null){
      int row = 0;
      FocList list = fTabMod.getFocList();
      if(list != null){
        FProperty selProp = list.getSelectionProperty();
        if(selProp != null){
          FocObject selectedObj = (FocObject) selProp.getObject();
          if(selectedObj != null){
            row = list.getRowForObject(selectedObj);
          }
        }
      }
 
      if(row < 0) row = 0;
      if(row < table.getRowCount()){
        table.setRowSelectionInterval(row, row);
  
        SwingUtilities.invokeLater(new Runnable(){
          public void run(){
            FTable table = getTable();
            if(table != null){
            	table.requestFocusInWindow();
            }
          }
        });
      }
    }
  }*/
  
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
      FTableModel model = (FTableModel) getTableModel();
      
      if(!model.getFocList().isDirectlyEditable()){
        if(model.getListPanel() != null && model.getListPanel().isShowEditButton()){
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