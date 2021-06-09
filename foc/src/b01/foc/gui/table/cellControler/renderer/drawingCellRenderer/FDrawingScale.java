package b01.foc.gui.table.cellControler.renderer.drawingCellRenderer;


public class FDrawingScale  {
  
  private int maxPixels   = 0;
  private double maxValue = 0;
  
  public FDrawingScale(int maxPixels ){
    super();
    this.maxPixels = maxPixels;
	}

  public int getTotalNumberOFPixelsForColumn() {
    return getMaxPixels();
  }

  public double getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(double maxValue) {
    this.maxValue = maxValue;
  }

  public int getMaxPixels() {
    return maxPixels;
  }

  public void setMaxPixels(int maxPixels) {
    this.maxPixels = maxPixels;
  }
  
}
