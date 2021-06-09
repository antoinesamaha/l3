package b01.foc.property;

import java.sql.Blob;

import b01.foc.desc.FocObject;

public class FBlobMediumProperty extends FBlobProperty{

  public FBlobMediumProperty(FocObject focObj, int fieldID, Blob defaultValue) {
    super(focObj, fieldID, defaultValue);
  }
}
