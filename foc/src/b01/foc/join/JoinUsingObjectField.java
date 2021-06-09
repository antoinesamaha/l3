/*
 * Created on Jan 9, 2006
 */
package b01.foc.join;

import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FObjectField;

/**
 * @author 01Barmaja
 */
public class JoinUsingObjectField extends Join{
  private int objectFieldID = FField.NO_FIELD_ID;
  
  public JoinUsingObjectField(TableAlias sourceTableAlias, int objectFieldID){
    super(sourceTableAlias);
    this.objectFieldID = objectFieldID;
  }
  
  public int fillRequestDescWithJoinFields_Internal(FocRequestDesc reqDesc, int firstJoinFieldID) {
    FocRequestField reqFld = new FocRequestField(firstJoinFieldID++, getTargetAlias(), FField.REF_FIELD_ID);
    reqDesc.addField(reqFld);
    
    return firstJoinFieldID;
  }
  
  public String getLinkCondition(){
    String ret = null;
    try{
      if(getSourceAlias() != null && getTargetAlias() != null){
        FocDesc srcDesc = getSourceAlias().getFocDesc();
        FocDesc tarDesc = getTargetAlias().getFocDesc();
        FObjectField objField = (FObjectField) srcDesc.getFieldByID(objectFieldID);

        if(tarDesc.getWithReference() && objField != null){
          ret = getSourceAlias().getAlias()+"."+objField.getKeyPrefix()+FField.REF_FIELD_NAME+"="+getTargetAlias().getAlias()+"."+FField.REF_FIELD_NAME;
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
