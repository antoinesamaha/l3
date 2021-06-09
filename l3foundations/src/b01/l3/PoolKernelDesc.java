package b01.l3;

import b01.foc.admin.FocUser;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FListField;
import b01.foc.list.FocLink;
import b01.foc.list.FocLinkN2N;
import b01.foc.list.FocLinkOne2N;

/**
 * @author 01Barmaja
 */
public class PoolKernelDesc extends FocDesc {
  
  public static final int FLD_NAME = 1;
  public static final int FLD_INSTRUMENT_LIST = 2;
  public static final int FLD_SERVICE_HOST = 5;
  
  public static final int COL_NAME = 4;
  
  private static FocLink poolUsersLink = null;
  
  public FocLink getPoolUsersLink() {
    if (poolUsersLink == null) {
      poolUsersLink = new FocLinkN2N(this, FocUser.getFocDesc(), "POOL_USR");
    }
    return poolUsersLink;
  }
  
  private static FocLink poolInstrumentLink = null;
  
  public FocLink getPoolInstrumentLink(){
    if(poolInstrumentLink == null){
      poolInstrumentLink = new FocLinkOne2N(this, Instrument.getFocDesc());
    }
    return poolInstrumentLink; 
  }
  
  public PoolKernelDesc(){
    
    super(PoolKernel.class, FocDesc.DB_RESIDENT, "POOL", true);

    FField focFld = addReferenceField();
    
    //setConcurrenceLockEnabled(true);
    //concurrenceLockView_AddField(FLD_NAME);

    focFld = new FCharField("NAME", "Pool name", FLD_NAME, true, FCharField.NAME_LEN);
    focFld.setLockValueAfterCreation(true);     
    addField(focFld);
    
    focFld = new FListField("INSTR_LIST", "Instrument list", FLD_INSTRUMENT_LIST, getPoolInstrumentLink());
    addField(focFld);
    
    focFld = new FCharField("SERVICE_HOST", "Service host", FLD_SERVICE_HOST, false, 30);
    addField(focFld);
  }

}
