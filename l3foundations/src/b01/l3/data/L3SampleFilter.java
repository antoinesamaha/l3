// INSTANCE
//    MAIN
// PANEL
// LIST
// DESCRIPTION
// FILTER DESCRIPTION

/*
 * Created on Jul 4, 2005
 */
package b01.l3.data;

import b01.foc.Globals;
import b01.foc.desc.*;
import b01.foc.list.*;
import b01.foc.list.filter.*;
import b01.foc.gui.*;
import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class L3SampleFilter extends FocListFilter{
  
  public static final String ENTRY_DATE_TITLE = "ENTRY_DATE";

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public L3SampleFilter(FocConstructor constr) {
    super(constr);    
  }

  public void makeForCurrentDateOnly(){
    FilterDesc filterDesc = getFilterDesc();
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      if(cond != null){ 
        if(cond.getFieldPrefix().compareTo(ENTRY_DATE_TITLE) == 0){
          DateCondition dateCond = (DateCondition)cond;
          //dateCond.setValue(this, getCurrentdate(.));
          dateCond.setOperator(this, DateCondition.OPERATOR_EQUALS);
          dateCond.setFirstDate(this, Globals.getApp().getSystemDate());
        }
      }          
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      focDesc = new FocDesc(L3SampleFilter.class, FocDesc.NOT_DB_RESIDENT, "SAMPLE_FILTER", true);

      focDesc.addReferenceField();
      getFilterDesc().fillDesc(focDesc, 1);
    }
    return focDesc;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // FILTER DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FilterDesc filterDesc = null;
  
  public static FilterDesc getFilterDesc(){
    if(filterDesc == null){
      filterDesc = new FilterDesc(L3Sample.getFocDesc());

      DateCondition dateCond =  new DateCondition (FFieldPath.newFieldPath(L3SampleDesc.FLD_ENTRY_DATE),"ENTRY_DATE");
      filterDesc.addCondition(dateCond);
      
      StringCondition cond = new StringCondition(FFieldPath.newFieldPath(L3SampleDesc.FLD_FIRST_NAME), "FIRST_NAME");
      filterDesc.addCondition(cond);

      cond = new StringCondition(FFieldPath.newFieldPath(L3SampleDesc.FLD_LAST_NAME), "LAST_NAME");
      filterDesc.addCondition(cond);
    }
    return filterDesc;
  }
  
  public FilterDesc getThisFilterDesc(){
    return L3SampleFilter.getFilterDesc();
  }
}



