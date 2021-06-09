package b01.foc.gui.table.cellControler.renderer.gantChartActivityRenderer;

import java.sql.Date;

public interface IGanttChartObjectInfo {
	public Date getMinimumStartDate();
  public Date getMinimumEndDate();
  public Date getMaximumStartDate();
  public Date getMaximumEndDate();
  public boolean isActivity();
  public boolean isInCriticalPath();
}
