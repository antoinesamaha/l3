package b01.foc.db;

public class SQLJoin {
  private String newAlias = null ;//New alias to point to this new table
  private String newTableName = null;//New table name
  
  private String prevAlias = null ;
  private String sourceField = "";
  private String targetField = "";
  
  public static String MAIN_TABLE_ALIAS = "M";

  public SQLJoin(String newTableName, String prevAlias, String sourceField, String targetField){
    this.newTableName = newTableName;
    this.prevAlias = prevAlias;
    this.sourceField = sourceField;
    this.targetField = targetField;
  }

  public String getKey() {
    return prevAlias+"|"+sourceField+"|"+newTableName+"|"+targetField;
  }
  
  public String getLinkCondition() {
    return prevAlias+"."+sourceField+"="+newAlias+"."+targetField;
  }

  public String getNewAlias() {
    return newAlias;
  }

  public void setNewAlias(String newAlias) {
    this.newAlias = newAlias;
  }

  public String getNewTableName() {
    return newTableName;
  }

  public String getPrevAlias() {
    return prevAlias;
  }

  public void setPrevAlias(String prevAlias) {
    this.prevAlias = prevAlias;
  }
}
