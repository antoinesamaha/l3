package b01.foc.desc.field;

import java.awt.Component;
import java.sql.Types;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.desc.FocObject;
import b01.foc.gui.FGScrollPane;
import b01.foc.gui.FGTextArea;
import b01.foc.gui.table.cellControler.AbstractCellControler;
import b01.foc.gui.table.cellControler.TextCellControler;
import b01.foc.property.FProperty;
import b01.foc.property.FBlobStringProperty;

public class FBlobStringField extends FCharField {

  private int rows    = 0;
  private int columns = 0;
  
  public FBlobStringField(String name, String title, int id, boolean key, int rows, int columns ) {
    super(name, title, id, key, 0);
    this.rows = rows;
    this.columns = columns;
    setIncludeInDBRequests(false);
  }
  
  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FBlobStringProperty(masterObj, getID(), (String)defaultValue);
  }
  
  public static int SqlType() {
    return Types.BLOB;
  }

  public int getSqlType() {
    return SqlType();
  }

  public String getCreationString(String name) {
    //BAntoineS-HSG-ORACLE-BLOB
    if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
      return " " + name + " CLOB";
    }else{
      //EAntoineS-HSG-ORACLE-BLOB  
      return " " + name + " BLOB";
      //BAntoineS-HSG-ORACLE-BLOB
    }
    //EAntoineS-HSG-ORACLE-BLOB
  }
  
  public Component getGuiComponent(FProperty prop){
    FGTextArea textArea = new FGTextArea();
    textArea.setColumns(columns);
    textArea.setRows(rows);
    textArea.setColumnsLimit(this.getSize());
    textArea.setCapital(isCapital());
    if(prop != null) textArea.setProperty(prop);
    return new FGScrollPane(textArea);
  }
  
  public AbstractCellControler getTableCellEditor(FProperty prop){
    JTextArea textArea = (JTextArea)getGuiComponent(prop);
    String textAreaText = textArea.getText();
    textAreaText = textAreaText.replaceAll("\n", " ");
    return new TextCellControler(new JTextField(textAreaText));
  }
}
