/*
 * Created on Jun 1, 2006
 */
package b01.l3.exceptions;

/**
 * @author 01Barmaja
 */
public class L3BadOrderAstmDataException extends L3Exception {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3256442525320491062L;

  /**
   * @param message
   * @param cause
   */
  public L3BadOrderAstmDataException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * @param cause
   */
  public L3BadOrderAstmDataException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * 
   */
  public L3BadOrderAstmDataException() {
    super();
    // TODO Auto-generated constructor stub
  }
  /**
   * @param message
   */
  public L3BadOrderAstmDataException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }
}
