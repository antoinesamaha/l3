package b01.foc.gui.table;

public class CopyPasteContent {
  private Object tableDisplayObject = null;
  private String formulaExpression = null;
  
  public void dispose(){
    tableDisplayObject = null;
    formulaExpression = null;
  }
  
  public Object getTableDisplayObject() {
    return tableDisplayObject;
  }

  public void setTableDisplayObject(Object tableDisplayObject) {
    this.tableDisplayObject = tableDisplayObject;
  }

  public String getFormulaExpression() {
    return formulaExpression;
  }

  public void setFormulaExpression(String formulaExpression) {
    this.formulaExpression = formulaExpression;
  }
  
}
