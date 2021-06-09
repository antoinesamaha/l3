/*
 * Created on Aug 31, 2005
 */
package b01.foc.cashdesk;

import b01.foc.Globals;
import b01.foc.date.*;
import b01.foc.currency.*;

import java.sql.*;

/**
 * @author 01Barmaja
 */
public class CashDateMap extends DateMap{
  private CashDesk cashDesk = null; 
  private Currency currency = null;
  private CashDateInfo currentDateInfo = null;
  
  public CashDateMap(CashDesk cashDesk, Currency currency){
    super();
    this.cashDesk = cashDesk; 
    this.currency = currency;
    fillFromTables();
  }

  public boolean isDateOpened(Date date){
    boolean b = false;
    CashDateInfo info = (CashDateInfo) get(date);
    if(info != null){
      b = info.isOpened();
    }
    return b;
  }

  public boolean isDateClosed(Date date){
    boolean b = false;
    CashDateInfo info = (CashDateInfo) get(date);
    if(info != null){
      b = info.isClosed();
    }
    return b;
  }

  public boolean dateHasCash(Date date){
    boolean b = false;
    CashDateInfo info = (CashDateInfo) get(date);
    if(info != null){
      b = info.isHasCash();
    }
    return b;
  }
  
  public Object get(Date date){
    if(currentDateInfo == null || currentDateInfo.getDate().compareTo(date) != 0){
      currentDateInfo = (CashDateInfo) super.get(date);
    }
    return (Object) currentDateInfo;
  }
  
  public void fillFromTables(){
    //Filling from the CashOpenClose table
    //------------------------------------
    /*
    FocConstructor openCloseConstr = new FocConstructor(CashOpenClose.getFocDesc(), null);
    CashOpenClose openClose = (CashOpenClose) openCloseConstr.newItem();

    openClose.setCashDesk(cashDesk);
    openClose.setCurrency(currency);

    SQLFilter filter = new SQLFilter(openClose, SQLFilter.FILTER_ON_SELECTED); 
    filter.addSelectedField(CashOpenClose.FLD_CASHDESK);
    filter.addSelectedField(CashOpenClose.FLD_CURRENCY);
    
    SQLSelectDistinct select = new SQLSelectDistinct(CashOpenClose.getFocDesc(), CashOpenClose.FLD_DATE, filter);
    select.addField(CashOpenClose.FLD_IS_CLOSED);
    select.execute();
    for(int l=0; l<select.getLineNumber(); l++){
      FDate dateProp = (FDate) select.getPropertyAt(l, 0);
      FBoolean isCloseProp = (FBoolean) select.getPropertyAt(l, 1);
      
      Date date = dateProp.getDate();
      CashDateInfo dateInfo = (CashDateInfo) get(date);
      if(dateInfo == null){
        dateInfo = new CashDateInfo();
        dateInfo.setDate(date);
        put(date, dateInfo);
      }
      
      if(isCloseProp.getBoolean()){
        dateInfo.setClosed(true);
      }else{
        dateInfo.setOpened(true);
      }
    }
    
    //Filling from the CashMovement table
    //------------------------------------
    FocConstructor cashMovementConstr = new FocConstructor(CashMovement.getFocDesc(), null);
    CashMovement cashMovement = (CashMovement) cashMovementConstr.newItem();

    cashMovement.setCashDesk(cashDesk);
    cashMovement.setCurrency(currency);

    filter = new SQLFilter(cashMovement, SQLFilter.FILTER_ON_SELECTED); 
    filter.addSelectedField(CashMovement.FLD_CASHDESK);
    filter.addSelectedField(CashMovement.FLD_CURRENCY);
    
    select = new SQLSelectDistinct(CashMovement.getFocDesc(), CashMovement.FLD_DATE, filter);
    select.execute();
    for(int l=0; l<select.getLineNumber(); l++){
      FDate dateProp = (FDate) select.getPropertyAt(l, 0);
      
      Date date = dateProp.getDate();
      CashDateInfo dateInfo = (CashDateInfo) get(date);
      if(dateInfo == null){
        dateInfo = new CashDateInfo();
        dateInfo.setDate(date);
        put(date, dateInfo);
      }
      dateInfo.setHasCash(true);
    }
    */
    //Putting the system date
    //-----------------------
    Date sysDate = Globals.getApp().getSystemDate();
    if(get(sysDate) == null){
      CashDateInfo dateInfo = new CashDateInfo();
      dateInfo.setDate(sysDate);
      put(sysDate, dateInfo);
    }
  }
}
