/*
 * Created on 10-Jun-2005
 */
package b01.foc.desc;

import b01.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public interface ReferenceChecker {
  public int getNumberOfReferences(FocObject obj, StringBuffer message);
  public void redirectReferencesToNewFocObject(FocObject focObjectToRedirectFrom, FocObject focObjectToRedirectTo);
  //public String getMessage();
  public FocList getLoadedFocList();
}
