/*
 * Created on 15 fvr. 2004
 */
package b01.foc.gui;

import b01.foc.*;
import b01.foc.dragNDrop.FocDefaultDropTargetListener;
import b01.foc.dragNDrop.FocDragGestureListener;
import b01.foc.dragNDrop.FocDragable;
import b01.foc.dragNDrop.FocDropTargetListener;
import b01.foc.dragNDrop.FocDropable;
import b01.foc.dragNDrop.FocTransferable;
import b01.foc.dragNDrop.FocTransferableObjectCompleter;
import b01.foc.property.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.text.*;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


/**
 * @author 01Barmaja
 */
public class FGNumField extends FGFormattedTextField implements ActionListener, FocusListener, FPropertyListener, FocDragable, FocDropable {
  //private int decimals = 2;
  //private NumericDocument numericDocument = null;
  private FProperty objectProperty = null;
  
  private boolean dropable = true;
  private FocTransferableObjectCompleter transferableObjectCompleter = null;
  private DragGestureRecognizer dragGestureRecognizer = null;
  private FocDragGestureListener dragGasturListener = null;
  private FocDropTargetListener dropTargetListener = null;
  
  private void initDrag(){
    disposeDrag();
    DragSource dragSource = DragSource.getDefaultDragSource();
    dragGasturListener = FocDragGestureListener.newFocdragGestureListener();
    dragGestureRecognizer = dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dragGasturListener);
  }
  
  private void initDrop(DropTargetListener dropTargetListener){
    disposeDrop();
    this.dropable = true;
    this.dropTargetListener = (FocDropTargetListener)dropTargetListener;
    DropTarget dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY, this.dropTargetListener, true);
  }
  
  private void disposeDrag(){
    if(dragGasturListener != null){
      dragGasturListener = null;
    }
    if(dragGestureRecognizer != null){
      dragGestureRecognizer = null;
    }
  }
  
  private void disposeDrop(){
    if(dropTargetListener != null){
      dropTargetListener = null;
    }
  }

  public FGNumField(boolean dropable) {
    super(NumberFormat.getInstance(MultiLanguage.getCurrentLocale()));
    addFocusListener(this);
    addActionListener(this);
    initDrag();
    setDropable(dropable);
    setHorizontalAlignment(JTextField.RIGHT);
  }
  
  public FGNumField() {
    this(true);
  }

  public FGNumField(NumberFormat numFormat, int displayWidth,boolean dropable) {
    super(numFormat);
    /*
    Graphics2D g2d = (Graphics2D)getGraphics();
    getFont().getLineMetrics("1", g2d.getFontRenderContext());
    */
    
    /*
    int size = 1 + numFormat.getMaximumFractionDigits() + numFormat.getMaximumIntegerDigits();
    if(numFormat.getMaximumFractionDigits() > 0){
      size += 1;  
    }
    size += numFormat.getMaximumIntegerDigits() / 3;
    */    
    setColumns(Double.valueOf(displayWidth * Globals.CHAR_SIZE_FACTOR).intValue());
    addFocusListener(this);
    addActionListener(this);
    initDrag();
    setDropable(dropable);
  }
  
  public FGNumField(NumberFormat numFormat, int displayWidth) {
    this(numFormat,displayWidth,true);
  }
  
  public void dispose(){
    if(objectProperty != null){
      objectProperty.removeListener(this);
      objectProperty = null;
    }
    removeActionListener(this);
    removeFocusListener(this);
  }
  
  public static NumberFormat newNumberFormat(int size, int decimals){
    return newNumberFormat(size, decimals, true);
  }

  public static NumberFormat newNumberFormat(int size, int decimals, boolean groupingUsed){
    NumberFormat form = NumberFormat.getInstance(MultiLanguage.getCurrentLocale());
    
    form.setMaximumFractionDigits(decimals);
    form.setMinimumFractionDigits(decimals);
    
    form.setMaximumIntegerDigits(size);
    form.setGroupingUsed(groupingUsed);
    return form;
  }
  
  public void displayPropertyValue(){
//  setText(objectProperty.getString());
    setValue(Double.valueOf(objectProperty.getDouble()));
    Color color = objectProperty.getBackground();
    if(color != null){
      setBackground(color);
    }
  }
  
  public void setProperty(FProperty prop){
    if(prop != objectProperty){
      if(objectProperty != null){
        objectProperty.removeListener(this);
      }
      objectProperty = prop;
      //setText(objectProperty.getString());
      displayPropertyValue();
      objectProperty.addListener(this);
    }
  }
  
  public FProperty getObjectProperty(){
    return objectProperty;
  }

  public void addToPanel(FPanel fPanel) {

  }

  private void updateObjectPropertyValue() {
    try {
      if (objectProperty != null) {
        objectProperty.setString(getText());
      }
    } catch (Exception exception) {
      Globals.logString(exception.toString());
    }
  }

  // FocusListener
  // -------------
  public void focusGained(FocusEvent e) {
    SwingUtilities.invokeLater(new Runnable(){     
      public void run() {
        selectAll();
      }  
    });
  }

  public void focusLost(FocusEvent e) {
    updateObjectPropertyValue();
  }

  // -------------

  // ActionListener
  // --------------
  public void actionPerformed(ActionEvent e) {
    updateObjectPropertyValue();
  }

  // --------------

  // PropertyListener
  // ----------------
  public void propertyModified(FProperty property) {
    if (objectProperty != null) {
      displayPropertyValue();
      //setText(objectProperty.getString());
    }
  }

  // ----------------

  private double getDouble() {
    double res = 0;
    String str = getText();
    if (str != null) {
      res = (new Double(str)).doubleValue();
    }
    return res;
  }

  //This function allows the component to have the right column width
  protected int getColumnWidth(){
    return super.getColumnWidth();
    /*
    int width = super.getColumnWidth();
    Graphics2D g2d = (Graphics2D)getGraphics();
    if(g2d != null){
      Rectangle2D rect2D = getFont().getStringBounds("1", g2d.getFontRenderContext());
      width = (int)rect2D.getWidth();
      Globals.setCharDimensions(width, (int)rect2D.getHeight());
    }
    return width;
    */
  }
  //Dragable
  public void setTranferableObjectCompleter(FocTransferableObjectCompleter transferableObjectCompleter){
    this.transferableObjectCompleter = transferableObjectCompleter;
  }

  public void fillTransferableObject(FocTransferable focTransferable) {

    FGNumField numField = (FGNumField)focTransferable.getSourceComponent();
    FProperty sourceProperty = numField.getObjectProperty();
    focTransferable.setSourceProperty(sourceProperty);
    if(sourceProperty != null){
      focTransferable.setSourceFocObject(sourceProperty.getFocObject());
    }
  
    if(this.transferableObjectCompleter != null){
      this.transferableObjectCompleter.completeFillingTransferableObject(focTransferable);
    }
  }
  
  //Dropable
  public boolean isDropable(){
    return this.dropable;
  }
  
  public void setDropable(boolean dropable){
    this.dropable = dropable;
    if(dropable){
      FocDefaultDropTargetListener defaultDropTargetListener = FocDefaultDropTargetListener.getInstance();
      setDropable(defaultDropTargetListener);
    }
  }
  
  public void setDropable(FocDropTargetListener dropTargetListener){    
    initDrop(dropTargetListener);
  }
  
  public void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde){
  	
  }
  
  public boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
  	return true;
	}

  public boolean drop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
    boolean accepted = false;
    try{
      /*Transferable transferable = dtde.getTransferable();
      FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());*/
      FocDragable sourceComponent = focTransferable.getSourceComponent();
      if(sourceComponent instanceof FGNumField){
        if(sourceComponent != this){
          FGNumField sourceNumField = (FGNumField)focTransferable.getSourceComponent();
          this.setText(sourceNumField.getText());
          accepted = true;
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
    return accepted;
  }
  
  /*
  protected Document createDefaultModel() {
    numericDocument = new NumericDocument(this);
    return numericDocument;
  }  
  */
  

  
  /**
   * @return Returns the decimals.
   */
  /*
  public int getDecimals() {
    return decimals;
  }
  */
  
  /**
   * @param decimals The decimals to set.
   */
  
  /*
  public void setDecimals(int decimals) {
    this.decimals = decimals;
  }
  */
  
  /*
  static class NumericDocument extends PlainDocument {
    boolean decimalExist = false;
    int decimalIndex = 0;
    FGNumField fNumFld = null;

    public NumericDocument(FGNumField fNumFld) {
      this.fNumFld = fNumFld;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str == null) {
        return;
      }

      char[] chars = str.toCharArray();
      char[] result = new char[chars.length];
      int i;
      for (i = 0; i < chars.length; i++) {
        boolean checkCharacter = true;
        Character c = new Character(chars[i]);
        if (i + offs == decimalIndex) decimalExist = false;
        if ((c.compareTo(new Character('+')) == 0) || (c.compareTo(new Character('-')) == 0)) {
          if (i + offs != 0) break;
          checkCharacter = false;
        }
        if (c.compareTo(new Character('.')) == 0) {
          if (decimalExist) break;
          decimalExist = true;
          decimalIndex = i + offs;
          checkCharacter = false;
        }
        if (checkCharacter) {
          if (!Character.isDigit(chars[i])) break;
        }
        if ((decimalExist) && (offs + i - decimalIndex > fNumFld.decimals)) {
          break;
        }
      }
      result = new char[i];
      for (int j = 0; j < i; j++) {
        result[j] = chars[j];
      }
      String strResult = new String(result);
      super.insertString(offs, strResult, a);
    }
  }*/
}
