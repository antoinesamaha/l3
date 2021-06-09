package b01.foc.access;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AccessControl {
  private boolean read = true;
  private boolean write = true;

  public AccessControl(boolean read, boolean write) {
    super();
    this.read = read;
    this.write = write;
  }

  /**
   * @return Returns the read.
   */
  public boolean isRead() {
    return read || write;
  }

  /**
   * @param read
   *          The read to set.
   */
  public void setRead(boolean read) {
    this.read = read;
  }

  /**
   * @return Returns the write.
   */
  public boolean isWrite() {
    return write;
  }

  /**
   * @param write
   *          The write to set.
   */
  public void setWrite(boolean write) {
    this.write = write;
  }
}
