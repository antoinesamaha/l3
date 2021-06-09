package b01.foc.gui.table.cellControler.renderer.gantChartResourceRenderer;

import java.awt.Color;
import java.sql.Date;
import java.util.ArrayList;

public interface IGantChartResourceDrawingInfo{
	public int getActivityCount();
	public double getActivityDurationAt(int i);
	public Date getActivityStartDateAt(int i);
	public String getActivityLabelAt(int i);
	public ArrayList<Color> getColorArrayForActivityAt(int i);
}
