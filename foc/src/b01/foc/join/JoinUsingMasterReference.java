/*
 * Created on Jan 9, 2006
 */
package b01.foc.join;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FField;

/**
 * @author 01Barmaja
 */
public class JoinUsingMasterReference extends Join{
  
  public JoinUsingMasterReference(TableAlias sourceAlias){
    super(sourceAlias);
  }
  
  public int fillRequestDescWithJoinFields_Internal(FocRequestDesc reqDesc, int firstJoinFieldID) {
    FocRequestField reqFld = new FocRequestField(firstJoinFieldID++, getSourceAlias(), FField.MASTER_REF_FIELD_ID);
    reqDesc.addField(reqFld);
    
    return firstJoinFieldID;
  }

  public String getLinkCondition(){
    String ret = null;
    try{
      if(getSourceAlias() != null && getTargetAlias() != null){
        FocDesc srcDesc = getSourceAlias().getFocDesc();
        FocDesc tarDesc = getTargetAlias().getFocDesc();
        
        if(tarDesc.getWithReference()){
          ret = getSourceAlias().getAlias()+"."+FField.MASTER_REF_FIELD_NAME+"="+getTargetAlias().getAlias()+"."+FField.REF_FIELD_NAME;
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
    return ret;
  }
    
  public String getUpdateCondition(){
    return "";
  }
}
