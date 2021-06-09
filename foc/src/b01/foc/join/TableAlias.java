/*
 * Created on Jan 10, 2006
 */
package b01.foc.join;

import b01.foc.desc.FocDesc;

/**
 * @author 01Barmaja
 */
public class TableAlias {
  private String alias = null; 
  private FocDesc focDesc = null;
  private Join join = null;

  public TableAlias(String alias, FocDesc desc){
    this.alias = alias;
    this.focDesc = desc;
  }

  public void setJoin(Join join){
    this.join = join;
    join.setTargetAlias(this);
  }
  
  public FocDesc getFocDesc() {
    return focDesc;
  }
  
  public String getAlias() {
    return alias;
  }
  
  public Join getJoin() {
    return join;
  }
}
