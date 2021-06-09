/*
 * Created on Jul 25, 2005
 */
package b01.foc.property.validators;

import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public interface FPropertyValidator{
  public boolean validateProperty(FProperty property);
  public void dispose();
}
