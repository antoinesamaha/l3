package b01.foc.property;

import java.sql.Blob;

import b01.foc.desc.FocObject;

public class FBlobLongProperty extends FBlobProperty{

  public FBlobLongProperty(FocObject focObj, int fieldID, Blob defaultValue) {
    super(focObj, fieldID, defaultValue);
  }
}
