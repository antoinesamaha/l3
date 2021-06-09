package b01.l3.drivers.coulter.dataBlockStructure;

import b01.l3.data.L3Sample;

public interface IFieldReadWrite {
  public void parse(L3Sample sample, String value);
  public void format(L3Sample sample, StringBuffer str);
  public void dispose();
}
