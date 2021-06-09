/*
 * Created on 24-May-2005
 */
package b01.foc.gui;

import b01.foc.desc.*;
import b01.foc.list.*;
import b01.foc.gui.table.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

/**
 * @author 01Barmaja
 */
public class FGMultiColumnComboListRenderer extends JLabel implements ListCellRenderer {

  private int keyFieldId = -99;  
  private FocList focList = null;
  private FTableView tableView = null;
  private JList list = null;
  private ListHighLightListener listener = null;
  
  public FGMultiColumnComboListRenderer(FocList focList, int keyFieldId, FTableView tableView) {
    super();
    setOpaque(true); //MUST do this for background to show up.
    this.focList = focList;
    this.keyFieldId = keyFieldId; 
    this.tableView = tableView;
  }

  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    if(this.list == null){
      this.list = list;
      if(this.list != null){
        listener = new ListHighLightListener(list);
        list.addMouseMotionListener(listener);
      }
    }
    
    FTable fTable = null;
    
    FocObject obj = focList.searchByProperyStringValue(keyFieldId, (String)value);
    if(obj == null){
      if(focList.size() > 0){
        obj = focList.getFocObject(0);
      }else{
        obj = focList.newEmptyItem();
      }
    }
    FocList singleItemFocList = new FocList(new FocLinkSimple(focList.getFocDesc()));
    singleItemFocList.setCollectionBehaviour(true);
    singleItemFocList.add(obj);
    FTableModel tableModel = new FTableModel(singleItemFocList, null);
    tableModel.setTableView(tableView);
    fTable = new FTable(tableModel);
    if(index == listener.getHighLightedRow()){
      fTable.setCurrentMouseRow(0);
    }else{
      fTable.setCurrentMouseRow(-1);
    }
    //fTable.setCurrentMouseRow(0);
    //fTable.addMouseListener(getMouseListener());
    //fTable.attachMouseListener();
    return fTable;
  }
  
  public class ListHighLightListener implements MouseMotionListener{
    private JList list = null;
    private int highLightedRow = -1;
    
    public ListHighLightListener(JList list){
      this.list = list;
    }
    
    public int getHighLightedRow() {
      return highLightedRow;
    }
    
    public void setHighLightedRow(int highLightedRow) {
      this.highLightedRow = highLightedRow;
    }

    private void newPoint(Point p){     
      int newRow = list.locationToIndex(p);
      if(newRow >= 0 && newRow != highLightedRow){
        highLightedRow = newRow;
        list.repaint();
      }
    }

    public void mouseDragged(MouseEvent e) {
      newPoint(e.getPoint());
    }

    public void mouseMoved(MouseEvent e) {
      newPoint(e.getPoint());
    }    
  }
}
