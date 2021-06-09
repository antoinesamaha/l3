package b01.l3.data;

import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.db.SQLFilter;
import b01.foc.db.SQLSelectExistance;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocFieldEnum;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.join.FocRequestDesc;
import b01.foc.join.FocRequestField;
import b01.foc.join.JoinUsingObjectField;
import b01.foc.join.TableAlias;
import b01.foc.list.FocLinkJoinRequest;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.FReference;
import b01.l3.InstrumentDesc;

public class L3SampleMessageJoinDesc extends FocDesc{
  
  private static FocRequestDesc requestDesc = null;
  public static final int FLD_MESSAGE_FIELDS_START = 100;
  
  public L3SampleMessageJoinDesc() {
    super(L3SampleMessageJoin.class, FocDesc.DB_RESIDENT, "L3SAMPLE_INSTR_MESSAGE_JOIN", null);
    FocRequestDesc reqDesc = getFocRequestDesc();
    reqDesc.fillFocDesc(this);
  }

  public FocRequestDesc getFocRequestDesc(){
    if(requestDesc == null){
      requestDesc = new FocRequestDesc(L3InstrumentMessageDesc.getInstance());

      TableAlias mainAlias = new TableAlias("M", L3InstrumentMessageDesc.getInstance());
      requestDesc.addTableAlias(mainAlias);
      
      TableAlias sampleAlias = new TableAlias("S", L3Sample.getFocDesc());
      sampleAlias.setJoin(new JoinUsingObjectField(mainAlias, L3InstrumentMessageDesc.FLD_L3_SAMPLE));
      requestDesc.addTableAlias(sampleAlias);

      FocRequestField reqFld = null;
      
      FocFieldEnum enumer = new FocFieldEnum(L3InstrumentMessageDesc.getInstance(), FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(enumer != null && enumer.hasNext()){
        FField field = enumer.nextField();

        reqFld = new FocRequestField(FLD_MESSAGE_FIELDS_START + field.getID(), mainAlias, field.getID());
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
  
  private static FocListOrder joinOrder = null;
  private static FocListOrder getJoinOrder(){
  	if(joinOrder == null){
  		joinOrder = new FocListOrder(L3SampleDesc.FLD_ID);
  		joinOrder.addField(FFieldPath.newFieldPath(FLD_MESSAGE_FIELDS_START + L3InstrumentMessageDesc.FLD_INSTRUMENT, InstrumentDesc.FLD_CODE));
  	}
  	return joinOrder;
  }
  
  public static FocList newList(){
    FocList list = null;
    FocLinkJoinRequest link = new FocLinkJoinRequest(getInstance().getFocRequestDesc());
    
    list = new FocList(null, link, null);
    list.setDirectImpactOnDatabase(false);
    list.setDirectlyEditable(false);
    
    //ORDER
    FocListOrder order = getJoinOrder();
    list.setListOrder(order);
    
    return list;
  }
  
  public static FocList newListOfNonCommited(){
    FocList list = newList();
    
    //FILTER
    SQLFilter listFiler = list.getFilter();
    FocConstructor constr = new FocConstructor(getInstance(), null);
    FocObject templateObject = constr.newItem();
    listFiler.setObjectTemplate(templateObject);
    templateObject.setPropertyMultiChoice(L3SampleMessageJoinDesc.FLD_MESSAGE_FIELDS_START + L3InstrumentMessageDesc.FLD_STATUS, L3TestDesc.TEST_STATUS_RESULT_AVAILABLE);
    listFiler.addSelectedField(L3SampleMessageJoinDesc.FLD_MESSAGE_FIELDS_START + L3InstrumentMessageDesc.FLD_STATUS);
    listFiler.setFilterFields(SQLFilter.FILTER_ON_SELECTED);
    
    return list;
  }
    
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static L3SampleMessageJoinDesc focDesc = null;
  
  public static L3SampleMessageJoinDesc getInstance() {
    if (focDesc == null) {
      focDesc = new L3SampleMessageJoinDesc();
    }
    return focDesc;
  }
  
  //A REVOIR
  public static void deleteListFromL3InstrumentMessage(FocList listToDelete) throws Exception {
    if(listToDelete != null && listToDelete.size() > 0){
      StringBuffer req = new StringBuffer("DELETE FROM L3_INSTR_MESSAGE WHERE ");
      req.append(FField.REF_FIELD_NAME+" IN (");
      for(int i=0; i<listToDelete.size(); i++){
        L3SampleMessageJoin join = (L3SampleMessageJoin) listToDelete.getFocObject(i);
        if(join != null){
          FReference ref = (FReference) join.getFocProperty(L3SampleMessageJoinDesc.FLD_MESSAGE_FIELDS_START + FField.REF_FIELD_ID);
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

      //Check if the relevant L3Sample lines are empty from L3InstrumentMessage
      HashMap<Integer, Integer> refMap = new HashMap<Integer, Integer>();
      for(int i=0; i<listToDelete.size(); i++){
        L3SampleMessageJoin join = (L3SampleMessageJoin) listToDelete.getFocObject(i);
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
            SQLSelectExistance selectExistance = new SQLSelectExistance(L3InstrumentMessageDesc.getInstance(), new StringBuffer(L3InstrumentMessageDesc.FNAME_SAMPLE_PREFIX+"REF="+refInt));
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
  
}
