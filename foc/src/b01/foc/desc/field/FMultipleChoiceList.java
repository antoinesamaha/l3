/*
 * Created on Oct 14, 2004
 */
package b01.foc.desc.field;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class FMultipleChoiceList extends HashMap {
  public void addChoice(int id, String title, Object obj) {
    FMultipleChoiceItem item = new FMultipleChoiceItem(id, title);
    super.put(Integer.valueOf(id), item);
  }
}
