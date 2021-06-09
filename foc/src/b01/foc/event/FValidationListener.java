/*
 * Created on Aug 11, 2005
 */
package b01.foc.event;

import b01.foc.gui.FValidationPanel;

/**
 * @author 01Barmaja
 */
public interface FValidationListener {
  public boolean proceedValidation(FValidationPanel panel);
  public boolean proceedCancelation(FValidationPanel panel);
  public void postValidation(FValidationPanel panel);
  public void postCancelation(FValidationPanel panel);  
}
