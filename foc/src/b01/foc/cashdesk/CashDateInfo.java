/*
 * Created on Sep 1, 2005
 */
package b01.foc.cashdesk;

import java.sql.Date;

/**
 * @author 01Barmaja
 */
public class CashDateInfo {
  private Date date = null;
  private boolean opened = false;
  private boolean closed = false;
  private boolean hasCash = false;

  public CashDateInfo(){
    
  }
  
  public boolean isClosed() {
    return closed;
  }
  public void setClosed(boolean closed) {
    this.closed = closed;
  }
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }
  public boolean isHasCash() {
    return hasCash;
  }
  public void setHasCash(boolean hasCash) {
    this.hasCash = hasCash;
  }
  public boolean isOpened() {
    return opened;
  }
  public void setOpened(boolean opened) {
    this.opened = opened;
  }
}
