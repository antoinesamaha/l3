package b01.l3.connector.dbConnector.lisConnectorTables;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FFieldPath;
import b01.foc.gui.FPanel;
import b01.foc.list.FocList;
import b01.foc.list.filter.DateCondition;
import b01.foc.list.filter.FilterCondition;
import b01.foc.list.filter.FilterDesc;
import b01.foc.list.filter.FocListFilter;
import b01.foc.list.filter.MultipleChoiceCondition;
import b01.foc.list.filter.StringCondition;

public class LisTestFilter extends FocListFilter{

  public static final String ENTRY_DATE_TITLE = "ENTRY_DATE";
  
  public LisTestFilter(FocConstructor constr) {
    super(constr);
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

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
      focDesc = new FocDesc(LisTestFilter.class, FocDesc.NOT_DB_RESIDENT, "LIS_CONNECTOR_TEST_FILTER", true);

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
      filterDesc = new FilterDesc(LisTestDesc.getInstance());

      StringCondition sampleIdCond =  new StringCondition (FFieldPath.newFieldPath(LisSampleDesc.FLD_SAMPLE_ID),"SAMPLE_ID");
      filterDesc.addCondition(sampleIdCond);
      
      MultipleChoiceCondition statusCondition = new MultipleChoiceCondition(FFieldPath.newFieldPath(LisTestDesc.FLD_STATUS), "STATUS");
      filterDesc.addCondition(statusCondition);
    }
    return filterDesc;
  }
  
  public FilterDesc getThisFilterDesc(){
    return LisTestFilter.getFilterDesc();
  }
  
}
