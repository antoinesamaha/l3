/*
 * Created on Jun 5, 2006
 */
package b01.l3.data;

import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.db.SQLSelectExistance;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocFieldEnum;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.join.FocRequestDesc;
import b01.foc.join.FocRequestField;
import b01.foc.join.JoinUsingObjectField;
import b01.foc.join.TableAlias;
import b01.foc.list.FocLinkJoinRequest;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.list.filter.FocListFilter;
import b01.foc.property.FReference;
import b01.l3.InstrumentDesc;

/**
 * @author 01Barmaja
 */
public class L3SampleTestJoinDesc extends FocDesc {

	private static FocRequestDesc requestDesc = null; 
  public static final int FLD_TEST_FIELDS_START = 100;
	
	public L3SampleTestJoinDesc(){
    super(L3SampleTestJoin.class, FocDesc.DB_RESIDENT, "SAMPLE_TEST_JOIN", false);
    setGuiBrowsePanelClass(L3SampleTestJoinGuiBrowsePanel.class);
    //setGuiDetailsPanelClass(L3SampleTestJoinGuiDetailsPanel.class);
    
    FocRequestDesc reqDesc = getFocRequestDesc();
    reqDesc.fillFocDesc(this);
	}
	
  public FocRequestDesc getFocRequestDesc(){
    if(requestDesc == null){
      requestDesc = new FocRequestDesc(L3Test.getFocDesc());

      TableAlias mainAlias = new TableAlias("T", L3Test.getFocDesc());
      requestDesc.addTableAlias(mainAlias);
      
      TableAlias sampleAlias = new TableAlias("S", L3Sample.getFocDesc());
      sampleAlias.setJoin(new JoinUsingObjectField(mainAlias, L3TestDesc.FLD_SAMPLE));
      requestDesc.addTableAlias(sampleAlias);

      FocRequestField reqFld = null;
      
      FocFieldEnum enumer = new FocFieldEnum(L3Test.getFocDesc(), FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(enumer != null && enumer.hasNext()){
      	FField field = enumer.nextField();

        reqFld = new FocRequestField(FLD_TEST_FIELDS_START + field.getID(), mainAlias, field.getID());
        requestDesc.addField(reqFld);
      }
      
      enumer = new FocFieldEnum(L3Sample.getFocDesc(), FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(enumer != null && enumer.hasNext()){
      	FField field = enumer.nextField();

        reqFld = new FocRequestField(field.getID(), sampleAlias, field.getID());
        requestDesc.addField(reqFld);
      }
      
      requestDesc.fillRequestDescWithJoinFields();
    }
    
    return requestDesc;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocList list = null;
  
  private static FocListOrder order = null;
  private static FocListOrder getOrder(){
  	if(order == null){
	    order = new FocListOrder(L3SampleDesc.FLD_ID);
	    order.addField(FFieldPath.newFieldPath(FLD_TEST_FIELDS_START + L3TestDesc.FLD_DISPATCH_INSTRUMENT, InstrumentDesc.FLD_CODE));
  	}
	  return order;
  }
  
  public static FocList newList(){
    FocList list = null;
    FocLinkJoinRequest link = new FocLinkJoinRequest(L3SampleTestJoinDesc.getInstance().getFocRequestDesc());
    
    list = new FocList(null, link, null);
    list.setDirectImpactOnDatabase(false);
    list.setDirectlyEditable(false);
    list.setListOrder(getOrder());
    
    return list;
  }
  
  public static FocList getList(int mode){
    if(list == null){
      list = newList();
    }
    if(mode == FocList.LOAD_IF_NEEDED){
      list.loadIfNotLoadedFromDB();
    }

    return list;
  }
  
  public static L3SampleTestJoinFilter newListWithFilter(){
  	FocList list = newList();
  	
    FocConstructor constr = new FocConstructor(L3SampleTestJoinFilter.getFocDesc(), null);
    L3SampleTestJoinFilter filter = (L3SampleTestJoinFilter) constr.newItem();
    constr.dispose();
    filter.setFocList(list);
    filter.setFilterLevel(FocListFilter.LEVEL_DATABASE);
  	
  	return filter;
  }
  
  public static void deleteListFromL3Test(FocList listToDelete) throws Exception {
  	if(listToDelete != null && listToDelete.size() > 0){
  		StringBuffer req = new StringBuffer("DELETE FROM L3TEST WHERE ");
  		req.append(FField.REF_FIELD_NAME+" IN (");
  		for(int i=0; i<listToDelete.size(); i++){
  			L3SampleTestJoin join = (L3SampleTestJoin) listToDelete.getFocObject(i);
  			if(join != null){
  				FReference ref = (FReference) join.getFocProperty(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + FField.REF_FIELD_ID);
  				if(ref != null){
  	  			if(i > 0) req.append(",");
  	  			req.append(ref.getInteger());
  				}
  			}
  		}
  		req.append(")");
  		
  		Statement stm = (Statement) Globals.getDBManager().lockStatement();
  		Globals.logString(req);
  		stm.execute(req.toString());
  		Globals.getDBManager().unlockStatement(stm);

  		//Check if the relevant L3Sample lines are empty from test
    	HashMap<Integer, Integer> refMap = new HashMap<Integer, Integer>();
    	for(int i=0; i<listToDelete.size(); i++){
  			L3SampleTestJoin join = (L3SampleTestJoin) listToDelete.getFocObject(i);
  			if(join != null){
  				FReference ref = (FReference) join.getFocProperty(FField.REF_FIELD_ID);
  				if(ref != null){
  					int refInt = ref.getInteger();
  					refMap.put(refInt, refInt);
  				}
  			}
    	}
    	
    	Iterator iter = refMap.keySet().iterator();
    	while(iter != null && iter.hasNext()){
    		Integer refIntObject = (Integer) iter.next();
    		if(refIntObject != null){
    			int refInt = refIntObject.intValue();
    			if(refInt > 0){
  			  	SQLSelectExistance selectExistance = new SQLSelectExistance(L3Test.getFocDesc(), new StringBuffer(L3TestDesc.FNAME_SAMPLE_PREFIX+"REF="+refInt));
  			  	selectExistance.execute();
  			  	boolean exists = selectExistance.getExist() == SQLSelectExistance.EXIST_YES;
  			  	selectExistance.dispose();
  			  	
  			  	if(!exists){
  						req = new StringBuffer("DELETE FROM L3SAMPLE WHERE "+FField.REF_FIELD_NAME+"="+refInt);
  						Statement stm2 = (Statement) Globals.getDBManager().lockStatement();
  						Globals.logString(req);
  						stm2.execute(req.toString());
  						Globals.getDBManager().unlockStatement(stm2);
  			  	}
    			}
    		}
    	}
  	}
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static L3SampleTestJoinDesc focDesc = null;
  
  public static L3SampleTestJoinDesc getInstance() {
    if (focDesc == null) {
      focDesc = new L3SampleTestJoinDesc();
    }
    return focDesc;
  }
}
