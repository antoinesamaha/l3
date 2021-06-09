/*
 * Created on 24 fvr. 2004
 */
package b01.foc.gui;

import b01.foc.gui.table.*;
import b01.foc.list.FocList;
import b01.foc.list.filter.FocListFilter;
import b01.foc.property.FProperty;
import b01.foc.*;
import b01.foc.desc.*;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.GridBagConstraints;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.HashMap;

/**
 * @author 01Barmaja
 */
public abstract class FAbstractListPanel extends FPanel implements Cloneable {
  
  public abstract FGButtonAction getNewAction();
  public abstract FGButtonAction getDeleteAction();
  public abstract FGButtonAction getEditAction();
  public abstract FGButtonAction getSelectAction();
  public abstract FGButtonAction getEditCellAction();
  public abstract FGButtonAction getDuplicateAction();
  public abstract FGButtonAction getFilterAction();
  public abstract FGButtonAction getColumnSelectorAction();
  //public abstract FGButtonAction getPrintAction();
  public abstract FGButtonAction getExpandAllAction();
  public abstract FGButtonAction getCollapseAllAction();
  public abstract FGButtonAction getRedirectAction();
  
  public final static String BUTTON_INSERT          = "INSERT";
  public final static String BUTTON_DELETE          = "DELETE";
  public final static String BUTTON_EDIT            = "EDIT";
  public final static String BUTTON_DUPLICATE       = "DUPLICATE";
  public final static String BUTTON_FILTER          = "FILTER";
  public final static String BUTTON_COLUMN_SELECTOR = "COL_SEL";
  public final static String BUTTON_PRINT           = "PRINT";
  public final static String BUTTON_EXPAND_ALL      = "EXPAND_ALL";
  public final static String BUTTON_COLLAPSE_ALL    = "COLLAPSE_ALL";
  //public final static String BUTTON_REDIRECT        = "REDIRECT";
  
  private static final String POPUP_LABEL_DUPLICATE = "Duplicate";
  private static final String POPUP_LABEL_EDIT      = "Edit";
  private static final String POPUP_LABEL_DELETE    = "Delete";
  private static final String POPUP_LABEL_ADD       = "Add";
  private static final String POPUP_LABEL_REDIRECT  = "Redirect";
  
  protected FAbstractTableModel fTableModel = null;
  protected FTable              fTable      = null;
  
  private FButtonsPanel buttonsPanel = null;
  private FPanel totalsPanel = null;

  private FGButton insert = null;
  private FGButton edit = null;
  private FGButton delete = null;
  private FGButton select = null;
  private FGButton cancel = null;
  private FGButton duplicate = null;
  private FGButton filter = null;
  private FGButton columnSelector = null;
  private FGButton print = null;
  private FGButton expandAll = null;
  private FGButton collapseAll = null;
  //private FGButton redirect = null;
  
  private FGButtonAction printAction = null;
  //private FGButtonAction duplicateAction = null;
  
  private boolean UniquePoopUp = true;
  private HashMap<FocObject, InternalFrame> objectsInternalFramesMap = null;
  
  public FAbstractListPanel() {    
  }

  public FAbstractListPanel(String frameTitle, int frameSizing, int panelFill) {
  	super(frameTitle, frameSizing, panelFill);
  }
    
  public FAbstractListPanel(String panelTitle, int panelFill) {
  	super(panelTitle, panelFill);
  }
  
  public void dispose(){
    super.dispose();
    
    FTableView tableView = getTableView();
    if(tableView != null){
      FocListFilter listFilter = tableView.getFilter();
      if(listFilter != null){
        listFilter.setSelectionPanel(null);  
      }
    }
    
    if(fTableModel != null){
      fTableModel.dispose();
      fTableModel = null;
      fTable = null;
    }     
    
    if(buttonsPanel != null){
      buttonsPanel.dispose();
      buttonsPanel = null;
    }
    
    if(totalsPanel != null){
      totalsPanel.dispose();    
      totalsPanel = null;
    }
    
    insert = null;
    edit = null;
    delete = null;
    select = null;
    cancel = null;
    duplicate = null;
    filter = null;
    columnSelector = null;
    print = null;
    expandAll = null;
    collapseAll = null;
    //redirect = null;
    //duplicateAction = null;
    printAction = null;
    
  }

  public Object clone() throws CloneNotSupportedException {
    FAbstractListPanel abstractListPanel = (FAbstractListPanel) super.clone();
    FAbstractTableModel abstractTableModel = abstractListPanel.getTableModel();
    abstractTableModel = (FAbstractTableModel)abstractTableModel.clone();
    abstractListPanel.setTableModel(abstractTableModel);
    return abstractListPanel;
  }
    
  public InternalFrame popup(FocObject currElement) {
    refreshList();
    return Globals.getApp().getDisplayManager().newInternalFrame(this);//, this.getTitle(), true);
  }
  
  public void refreshList() {
    FocList focList = getFocList();
    if (focList != null) {
      //getTableModel().resetListListenerToCellPropertiesMoifications();
      //memory SHOULD BE loadIFNotLoaded otherwize we reload the clients each time
      focList.reloadFromDB();
    }
  }
  
  public void setTableMinMax(int minWidth, int minHeight, int maxWidth, int maxHeight){
    if(fTable != null) fTable.setTableMinMax(minWidth, minHeight, maxWidth, maxHeight);
  }
    
  //BElias
  public Boolean isUniquePoopUp(){
    return UniquePoopUp;
  }
  
  public void setUniquePoopUp(Boolean unique){
    UniquePoopUp = unique;
  }
  
  public HashMap<FocObject, InternalFrame> getObjectsInternalFramesMap(){
    if (objectsInternalFramesMap == null){
      objectsInternalFramesMap  = new HashMap<FocObject, InternalFrame>();
    }
    return objectsInternalFramesMap ;
  }
  //EElias
  
  public void construct() {
    FocListFilter listFilter = getTableView().getFilter();
    if(listFilter != null){
      listFilter.setSelectionPanel(this);  
    }
    
    fTable = new FTable(fTableModel);
    add(fTable.getScrollPane(), 0, 1, 2, 1, 0.99, 0.99, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
       
    // Creating buttons panel for Insert Delete Ok ...
    FButtonsPanel buttonsPanel = getButtonsPanel();

    getTable().getActionMap().put("celledit", getEditCellAction());
    getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getCellEditStroke(), "celledit");
    
    // -----------------------
    // New Button
    // -----------------------
    FGButtonAction newAction = getNewAction();
    if(newAction != null){
	    insert = new FGButton(Globals.getIcons().getInsertIcon());
	    insert.setToolTipText("Insert new item");
	    insert.setName(getButtonName(BUTTON_INSERT));
	    if(ConfigInfo.isUnitDevMode()){
	    	insert.setToolTipText(insert.getName());
	    }	    
	    newAction.setAssociatedButton(insert);
	    insert.addActionListener(newAction);    
	    buttonsPanel.addButton(insert);
	    getTable().getActionMap().put("new", newAction);
    }

    // -----------------------
    // Edit Button
    // -----------------------
    FGButtonAction editAction = getEditAction();
    if(editAction != null){
	    edit = new FGButton(Globals.getIcons().getEditIcon());
	    edit.setToolTipText("Edit current item");
	    edit.setName(getButtonName(BUTTON_EDIT));
	    if(ConfigInfo.isUnitDevMode()){
	    	edit.setToolTipText(edit.getName());
	    }	    	    
	    editAction.setAssociatedButton(edit);
	    edit.addActionListener(editAction);
	    buttonsPanel.addButton(edit);
	    getTable().getActionMap().put("edit", editAction);
    }
    
    // -----------------------
    // Delete Button
    // -----------------------
    FGButtonAction deleteAction = getDeleteAction();
    if(deleteAction != null){
	    delete = new FGButton(Globals.getIcons().getDeleteIcon());
	    delete.setDisableValidationProcess(true, getTable());
	    delete.setToolTipText("Delete current item");
	    delete.setName(getButtonName(BUTTON_DELETE));
	    if(ConfigInfo.isUnitDevMode()){
	    	delete.setToolTipText(delete.getName());
	    }	    	    
	    deleteAction.setAssociatedButton(delete);
	    delete.addActionListener(deleteAction);
	    buttonsPanel.addButton(delete);
	    getTable().getActionMap().put("delete", deleteAction);
    }
    
    // -----------------------
    // Duplicate Button
    // -----------------------
    FGButtonAction duplicateAction = getDuplicateAction();
    if(duplicateAction != null){
	    duplicate = new FGButton(Globals.getIcons().getCopyIcon());
	    duplicate.setToolTipText("Duplicate current item");
	    duplicate.setName(getButtonName(BUTTON_DUPLICATE));
	    duplicateAction.setAssociatedButton(duplicate);
	    duplicate.addActionListener(duplicateAction);    
	    buttonsPanel.addButton(duplicate);
	    getTable().getActionMap().put("duplicate", duplicateAction); 
    }
    
    //-----------------------
    // Print Button
    // -----------------------
    FGButtonAction printAction = getPrintAction();
    if(printAction != null){
      print = new FGButton(Globals.getIcons().getPrintIcon());
      print.setToolTipText("Print table");
      print.setName(getButtonName(BUTTON_PRINT));
      printAction.setAssociatedButton(print);
      print.addActionListener(printAction);    
      buttonsPanel.addButton(print);
      getTable().getActionMap().put("print", printAction); 
    }
    
    // -----------------------
    // Column selector Button
    // -----------------------
    FGButtonAction columnSelectorAction = getColumnSelectorAction();
    if(columnSelectorAction != null){
	    columnSelector = new FGButton(Globals.getIcons().getColumnSelectorIcon());
	    columnSelector.setToolTipText("Select displayed columns");
	    columnSelector.setName(getButtonName(BUTTON_COLUMN_SELECTOR));
	    columnSelectorAction.setAssociatedButton(columnSelector);
	    columnSelector.addActionListener(columnSelectorAction);    
	    buttonsPanel.addButton(columnSelector);
	    getTable().getActionMap().put("columnSelector", columnSelectorAction);
    }
    
    //-----------------------
    // Expand all Button
    // -----------------------
    FGButtonAction expandAllAction = getExpandAllAction();
    if(expandAllAction != null){
	    expandAll = new FGButton(Globals.getIcons().getExpandAllIcon());
	    expandAll.setToolTipText("Expand all nodes");
	    expandAll.setName(getButtonName(BUTTON_EXPAND_ALL));
	    expandAllAction.setAssociatedButton(expandAll);
	    expandAll.addActionListener(expandAllAction);    
	    buttonsPanel.addButton(expandAll);
	    getTable().getActionMap().put("expandAll", expandAllAction);
    }
    
    //-----------------------
    // Collapse all Button
    // -----------------------
    FGButtonAction collapseAllAction = getCollapseAllAction();
    if(collapseAllAction != null){
	    collapseAll = new FGButton(Globals.getIcons().getCollapseAllIcon());
	    collapseAll.setToolTipText("Collapse all nodes");
	    collapseAll.setName(getButtonName(BUTTON_COLLAPSE_ALL));
	    collapseAllAction.setAssociatedButton(collapseAll);
	    collapseAll.addActionListener(collapseAllAction);    
	    buttonsPanel.addButton(collapseAll);
	    getTable().getActionMap().put("collapseAll", collapseAllAction);
    }
    
    //-----------------------
    // Redirect Button
    // -----------------------
    /*FGButtonAction redirectAction = getRedirectAction();
    if(redirectAction != null){
	    redirect = new FGButton(Globals.getIcons().getRedirectIcon());
	    redirect.setToolTipText("Redirect");
	    redirect.setName(getButtonName(BUTTON_REDIRECT));
	    if(ConfigInfo.isUnitDevMode()){
	    	redirect.setToolTipText(redirect.getName());
	    }	    	    
	    redirectAction.setAssociatedButton(redirect);
	    redirect.addActionListener(redirectAction);
	    buttonsPanel.addButton(redirect);
	    getTable().getActionMap().put("redirect", redirectAction);
    }*/
    
    // -----------------------
    // Filter Button
    // -----------------------
    FGButtonAction filterAction = getFilterAction();
    if(filterAction != null){
	    filter = new FGButton(Globals.getIcons().getFilterIcon());
	    filter.setToolTipText("Filter");
	    filter.setName(getButtonName(BUTTON_FILTER));
	    filterAction.setAssociatedButton(filter);
	    filter.addActionListener(filterAction);    
	    buttonsPanel.addButton(filter);
	    getTable().getActionMap().put("filter", filterAction);
    }
    
    showPrintButton(false);
    showFilterButton(false);
    showModificationButtons(true);
    showDuplicateButton(false);
    showColomnSelectorButton(false, null);
    
    fTable.addSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e) {
        Globals.getDisplayManager().removeLockFocusForObject(getSelectedObject());    
      }      
    });
  }
  
  public String getButtonName(String suffix){
  	return getPanelName()+"."+suffix;
  }
  
  public FTable getTable() {
    return fTable;
  }

  public FAbstractTableModel getTableModel(){
    return fTableModel;
  }
  
  public void setTableModel(FAbstractTableModel fTableModel){
    this.fTableModel = fTableModel;
  }
  
  public FTableView getTableView() {
    FTableView view = null;
    if (fTableModel != null) {
      view = fTableModel.getTableView();
    }
    return view;
  }
  
  //BElias 
  public FocList getFocList(){
    return fTableModel != null ? fTableModel.getFocList() : null; 
  }
  //EElias

  public FocObject getSelectedObject() {
    FocObject ret = null;
    int row = 0;
    FTable ftable = getTable();
    if (ftable != null) {
      row = ftable.getSelectedRow();
      if(row >= 0){
        ret = ftable.getElementAt(row);
      }
    }
    return ret;
  }
  
  public void setSelectedObject(FocObject selectedObject) {
    FTable ftable = getTable();
    if(ftable != null){
      ftable.setSelectedObject(selectedObject);
    }
  }
  
  public FAbstractListPanel getThis() {
    return this;
  }

  
  public void addCellStartEditingListener(FTableCellStartEditingListener listener){
    FTable table = getTable();
    if(table != null){
      table.addCellStartEditingListener(listener);
    }
  }
  
  public void removeCellStartEditingListener(FTableCellStartEditingListener listener){
    FTable table = getTable();
    if(table != null){
      table.removeCellStartEditingListener(listener);
    }
  }
  
  /**
   * @return Returns the buttonsPanel.
   */
  public FButtonsPanel getButtonsPanel() {
    if(buttonsPanel == null){
      buttonsPanel = new FButtonsPanel();
      buttonsPanel.setFill(FPanel.FILL_NONE);
      buttonsPanel.setToolTipText("Buttons Panel");//AUTOSIZE
      add(buttonsPanel, 1, 2, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE);      
    }
    return buttonsPanel;
  }
  
  public FPanel getTotalsPanel() {
    if(totalsPanel == null){
      totalsPanel = new FPanel();
      add(totalsPanel, 0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE);      
    }
    return totalsPanel;
  }

  public void requestFocusOnTable(){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        FTable table = getTable();
        if(table != null){
        	table.requestFocusInWindow();
        }
      }
    });
  }
  
  /*
  public void repaint(){
    super.repaint();
    requestFocusOnCurrentItem();
  }
  */
  
  //-----------------------
  // Print Action
  // -----------------------
  @SuppressWarnings("serial")
	public FGButtonAction getPrintAction() {
    if (printAction == null) {
      printAction = new FGButtonAction(null) {
        public void focActionPerformed(ActionEvent e) {
          try {
            FTable table = getTable();
            //MessageFormat headerFormat = new MessageFormat(getFocDesc().getTitle()+"- Page {0}");
            MessageFormat footerFormat = new MessageFormat("- {0} -");
            
            Thread.sleep(1000);
            table.print(JTable.PrintMode.FIT_WIDTH, null, footerFormat);
          }catch (Exception pe) {
           	Globals.logString("Error printing: " + pe.getMessage());
          }
      	}
    	};
  	}
    return printAction;
  }
  
  public void showPrintButton(boolean show) {
    if(print != null) print.setVisible(show);
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getPrintStroke(), "print");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getPrintStroke());
    }
  }
  
  public void showFilterButton(boolean show) {
    if(filter != null) filter.setVisible(show);
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getFilterStroke(), "filter");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getFilterStroke());
    }
  }

  public void enableAddButton(boolean enable) {
    if(insert != null){
      if(enable) insert.setVisible(true);
      insert.setEnabled(enable);
    }
    if (enable) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getInsertStroke(), "new");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getInsertStroke());
    }
    enablePopupItem(POPUP_LABEL_ADD, enable);
  }
  
  public void enableRemoveButton(boolean enable) {
    if(delete != null){
      if(enable) delete.setVisible(true);
      delete.setEnabled(enable);
    }
    if (enable) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getDeleteStroke(), "delete");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getDeleteStroke());
    }
    enablePopupItem(POPUP_LABEL_DELETE, enable);
  }  
  
  public void showAddButton(boolean show) {
    if(insert != null){
      insert.setVisible(show);
    }

    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getInsertStroke(), "new");
      addPopupItem(Globals.getIcons().getInsertIcon(), POPUP_LABEL_ADD, getNewAction());
    } else {
    	removePopupItem(POPUP_LABEL_ADD);
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getInsertStroke());
    }
  }
  
  public void showRemoveButton(boolean show) {
    if(delete != null){
      delete.setVisible(show);
    }
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getDeleteStroke(), "delete");
      addPopupItem(Globals.getIcons().getDeleteIcon(), POPUP_LABEL_DELETE, getDeleteAction());
    } else {
    	removePopupItem(POPUP_LABEL_DELETE);
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getDeleteStroke());
    }
  }
  
  public void showExpandAllButton(boolean show) {
    if(expandAll != null) expandAll.setVisible(show);
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getExpandAllStroke(), "expandAll");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getExpandAllStroke());
    }
  }
  
  public void showCollapseAllButton(boolean show) {
    if(collapseAll != null) collapseAll.setVisible(show);
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getCollapseAllStroke(), "collapseAll");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getCollapseAllStroke());
    }
  }
  
  public void showRedirectButtonInPopupMenu(boolean show){
  	if(show){
  		if(Globals.getApp().getGroup().allowNamingModif()){
  			addPopupItem(Globals.getIcons().getRedirectIcon(), POPUP_LABEL_REDIRECT, getRedirectAction());
  		}
  	}else{
  		removePopupItem(POPUP_LABEL_REDIRECT);
  	}
  }
  
  public void showModificationButtons(boolean show) {
    showAddButton(show);
    showRemoveButton(show);
    showEditButton(show);
  }

  public void enableModificationButtons(boolean enable) {
    enableAddButton(enable);
    enableRemoveButton(enable);
    enableEditButton(enable);
  }

  public void showColomnSelectorButton(boolean show, String viewKey) {
  	if(columnSelector != null){
  		columnSelector.setVisible(show);
  	}
    if(show){
      getTableView().setViewKey(viewKey);
      getTableView().setColumnVisibilityAccordinglyToConfig();
    }
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getColumnSelectorStroke(), "columnSelector");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getColumnSelectorStroke());
    }
  }
  
  public boolean isShowEditButton(){
    return edit.isVisible();
  }

  public void enableEditButton(boolean enable) {
    if (edit != null) {
      if(enable){
        edit.setVisible(true);
      }
      edit.setEnabled(enable);
    }
    if (enable) {    	
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getRowEditStroke(), "edit");
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getRowEditStrokeCtrl(), "edit");
    } else {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStroke());
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStrokeCtrl());      
    }
    enablePopupItem(POPUP_LABEL_EDIT, enable);
  }
  
  public void showEditButton(boolean show) {
    if (edit != null) {
      edit.setVisible(show);
    }    
    if (show) {
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getRowEditStroke(), "edit");
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getRowEditStrokeCtrl(), "edit");
      
      addPopupItem(Globals.getIcons().getEditIcon(), POPUP_LABEL_EDIT, getEditAction());      
    } else {
    	removePopupItem(POPUP_LABEL_EDIT);
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStroke());
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStrokeCtrl());      
    }
  }
  
  private JMenuItem getItemIndexInPopup(FPopupMenu popupMenu, String text){
  	JMenuItem foundMenuItem = null;
    for( int i = 0; i < popupMenu.getComponents().length && foundMenuItem == null; i++){
      JMenuItem mi = (javax.swing.JMenuItem)popupMenu.getComponent(i);
      if( mi.getText().equals(text)){
      	foundMenuItem = mi ;
      }
    }
    
    return foundMenuItem;
  }
  
  private void removePopupItem(String text){
  	FPopupMenu popupMenu = getTable().getPopupMenu();
  	if(popupMenu != null){
  		JMenuItem menuItem = getItemIndexInPopup(popupMenu, text);
	  	if(menuItem != null){
	  		popupMenu.remove(menuItem);	
	  	}
  	}
  }

  private void addPopupItem(ImageIcon icon, String text, AbstractAction action){
  	FPopupMenu popupMenu = getTable().getPopupMenu();
  	if(popupMenu != null){
  		JMenuItem menuItem = getItemIndexInPopup(popupMenu, text); 
      if( menuItem == null ){
        menuItem = new JMenuItem(text, icon);
        menuItem.addActionListener(action);
        popupMenu.add(menuItem);  
      }
  	}
  }
  
  private void enablePopupItem(String text, boolean enable){
  	FPopupMenu popupMenu = getTable().getPopupMenu();
  	if(popupMenu != null){
  		JMenuItem menuItem = getItemIndexInPopup(popupMenu, text); 
      if(menuItem != null){
      	menuItem.setEnabled(enable);
	  	}
  	}
  }
  
  public void disableEditKey() {
    getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getRowEditStroke());    
    //getTable().getInputMap(WHEN_IN_FOCUSED_WINDOW).remove(FocKeys.getRowEditStrokeCtrl());
  }
  
  public void showDuplicateButton(boolean show) {
    if (duplicate != null) {
      duplicate.setVisible(show);
    }
    if (show) {
    	addPopupItem(Globals.getIcons().getCopyIcon(), POPUP_LABEL_DUPLICATE, getDuplicateAction());
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FocKeys.getDuplicateStroke(), "duplicate");
    } else {
    	removePopupItem(POPUP_LABEL_DUPLICATE);
      getTable().getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(FocKeys.getDuplicateStroke());
    }
  }  
  
  public FGButton getFilterButton() {
    return filter;
  }
  
  public void requestFocusOnCurrentItem(){
    FTable table = getTable();
    //FTableModel fTabMod = (FTableModel) fTableModel;  
    if(fTableModel != null && table != null){
      int row = 0;
      FocList list = fTableModel.getFocList();
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
  }
  
  public void setDropable(DropTargetListener dropTargetListener){
  	if(fTable != null){
  		FixedColumnScrollPane scrollPan = fTable.getScrollPane();
  		if(scrollPan != null){
  			scrollPan.setDropable(dropTargetListener);
  		}
  	}
  }
}
