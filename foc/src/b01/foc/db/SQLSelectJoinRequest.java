/*
 * Created on 27 fvr. 2004
 */
package b01.foc.db;

import b01.foc.desc.*;
import b01.foc.join.*;
import b01.foc.list.*;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class SQLSelectJoinRequest extends SQLSelect {
  private FocRequestDesc requestDesc = null;
  
  public SQLSelectJoinRequest(FocObject focObject, FocRequestDesc requestDesc, SQLFilter filter) {
    super(focObject, requestDesc.getFocDesc(), filter);
    this.requestDesc = requestDesc ;
  }
  
  public SQLSelectJoinRequest(FocList initialList, FocRequestDesc requestDesc, SQLFilter filter) {
    super(initialList, requestDesc.getFocDesc(), filter);
    this.requestDesc = requestDesc ;
  }

  public void addFrom() {
    request.append(" FROM ");

    boolean isFirstAlias = true;
    
    Iterator iter = requestDesc.newAliasIterator();
    while(iter != null && iter.hasNext()){
      TableAlias alias = (TableAlias) iter.next();
      if(alias != null){
        if(!isFirstAlias){
          request.append(",");
        }
        request.append(alias.getFocDesc().getStorageName()+" "+alias.getAlias());
        isFirstAlias = false;
      }
    }
  }
  
  public boolean addWhere() {
    int length = request.length();
    boolean b = super.addWhere();
    
    if(!b){
      boolean withAnd = request.length() != length;
      if(withAnd){
        append(" AND (");
      }else{
        append(" WHERE ");
      }
      
      String linkCond = requestDesc.getLinkCondition();
      append(linkCond);
      
      if(withAnd){
        append(")");
      }
    }
    return b;
  }
  
}
