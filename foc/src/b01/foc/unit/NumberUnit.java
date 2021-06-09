/*
 * Created on 01-Feb-2005
 */
package b01.foc.unit;

/**
 * @author 01Barmaja
 */
public class NumberUnit implements Unit {

  public String getTitle() {
    return "Number";
  }

  public String getName() {
    return "Nb";
  }

  public int getID() {
    return Unit.NB;
  }
}