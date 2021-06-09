// INSTANCE
//    MAIN
// PANEL
// LIST
// DESCRIPTION
// FILTER DESCRIPTION

/*
 * Created on Jul 4, 2005
 */
package b01.l3;

import b01.foc.desc.*;
import b01.foc.list.*;
import b01.foc.list.filter.*;
import b01.foc.gui.*;
import b01.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class TestLabelMapFilter extends FocListFilter{

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public TestLabelMapFilter(FocConstructor constr) {
    super(constr);
    setFilterLevel(FocListFilter.LEVEL_MEMORY);
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
      focDesc = new FocDesc(TestLabelMapFilter.class, FocDesc.NOT_DB_RESIDENT, "TST_MAP_FLTR", true);

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
      filterDesc = new FilterDesc(TestLabelMap.getFocDesc());

      StringCondition cond = new StringCondition(FFieldPath.newFieldPath(TestLabelMapDesc.FLD_INSTRUMENT, InstrumentDesc.FLD_CODE), "INSTRUMENT");
      cond.setFieldLabel("Instrument Code");
      filterDesc.addCondition(cond);

      cond = new StringCondition(FFieldPath.newFieldPath(TestLabelMapDesc.FLD_LIS_TEST_LABEL), "LIS_LABEL");
      filterDesc.addCondition(cond);

      cond = new StringCondition(FFieldPath.newFieldPath(TestLabelMapDesc.FLD_INSTRUMENT_TEST_CODE), "INST_LABEL");
      filterDesc.addCondition(cond);

      cond = new StringCondition(FFieldPath.newFieldPath(TestLabelMapDesc.FLD_TEST_DESCRIPTION), "TST_DESC");
      filterDesc.addCondition(cond);

      cond = new StringCondition(FFieldPath.newFieldPath(TestLabelMapDesc.FLD_TEST_GROUP, TestGroupDesc.FLD_NAME), "TST_GROUP");
      cond.setFieldLabel("Test group");      
      filterDesc.addCondition(cond);
    }
    return filterDesc;
  }
  
  public FilterDesc getThisFilterDesc(){
    return TestLabelMapFilter.getFilterDesc();
  }
}



