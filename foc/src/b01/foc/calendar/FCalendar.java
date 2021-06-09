package b01.foc.calendar;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocObject;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.FDate;
import b01.foc.property.FTime;

public class FCalendar extends FocObject {
	private FocList holidayList = null;
	public static final long MILLISECONDS_IN_DAY = 86400000;
	public static final long MILLISECONDS_IN_HOUR = MILLISECONDS_IN_DAY / 24;
	public static final long MILLISECONDS_IN_MINUTE = MILLISECONDS_IN_HOUR / 60;


	public FCalendar(FocConstructor constr){
	  super(constr);
		newFocProperties();
	} 
	
	public static FCalendar getDefaultCalendar(){
		FocList calendarList = FCalendarDesc.getList(FocList.LOAD_IF_NEEDED);
		FCalendar cal = (FCalendar) calendarList.searchByPropertyBooleanValue(FCalendarDesc.FLD_IS_DEFAULT, true);
		return cal;
	}

	//rr & elie Begin
  public static int getWeekNumber(Date date){
    int weekNumber = -1;
    
    /*String dateToExtract = String.valueOf(date);
    int year = Integer.valueOf(dateToExtract.substring(0,4));
    int month = Integer.valueOf(dateToExtract.substring(5,7)) - 1 ; // Start counting from 0
    int day = Integer.valueOf(dateToExtract.substring(8,10));
    
    Calendar calender = new GregorianCalendar(year, month, day);*/
    
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    
    weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
    return weekNumber;
  }
  
  public static int getMonthNumber(Date date){
    int monthNumber = -1;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    monthNumber = calendar.get(Calendar.MONTH);
    return monthNumber;
  }
  
  public static int getYear(Date date){
    int yearNumber = -1;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    yearNumber = calendar.get(Calendar.YEAR);
    return yearNumber;
  }
  
  //rr & elie End
  
  //BELIAS
  public static int getDayOfYear(Date date){
  	int dayOfYear = -1;
  	Calendar calendar = Calendar.getInstance();
  	calendar.setTime(date);
  	dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
  	return dayOfYear;
  }
  
  public static int getDayOfMonth(Date date){
  	int dayOfMonth = -1;
  	Calendar calendar = Calendar.getInstance();
  	calendar.setTime(date);
  	dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
  	return dayOfMonth;
  }
  
  public static int getHour(Date date){
  	int houre = -1;
  	Calendar calendar = Calendar.getInstance();
  	calendar.setTime(date);
  	houre = calendar.get(Calendar.HOUR_OF_DAY);
  	return houre;
  }
  
  public static int getMinute(Date date){
  	int minute = -1;
  	Calendar calendar = Calendar.getInstance();
  	calendar.setTime(date);
  	minute = calendar.get(Calendar.MINUTE);
  	return minute;
  }
  
  //EELIAS
  
	private int getFieldIDForDayOfWeek(int dayOfWeek){
		int fieldID = 0;
		if(dayOfWeek == Calendar.SUNDAY){
			fieldID = FCalendarDesc.FLD_IS_SUNDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.MONDAY){
			fieldID = FCalendarDesc.FLD_IS_MONDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.TUESDAY){
			fieldID = FCalendarDesc.FLD_IS_TUESDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.WEDNESDAY){
			fieldID = FCalendarDesc.FLD_IS_WEDNESDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.THURSDAY){
			fieldID = FCalendarDesc.FLD_IS_THURSDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.FRIDAY){
			fieldID = FCalendarDesc.FLD_IS_FRIDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.SATURDAY){
			fieldID = FCalendarDesc.FLD_IS_SATURDAY_WORKDAY;
		}
		return fieldID;
	}
  
	public boolean isWeekEnd(java.sql.Date d){
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		boolean isWorking = false;
	
		int fieldID = getFieldIDForDayOfWeek(dayOfWeek);
		isWorking = this.getPropertyBoolean(fieldID);
		return !isWorking;
	}
	
	public void setWeekEnd(int dayOfWeek){
		setPropertyBoolean(getFieldIDForDayOfWeek(dayOfWeek), false);
	}

  public static Date shiftDate(Calendar cal, Date date, int shift){
    cal.setTime((Date)date.clone());
    cal.add(Calendar.DATE, shift);
    return new Date(cal.getTimeInMillis());
  }

  public Date [] getStartingEndingDatesOfWeek (int week, int year){
   Date [] dates = new Date[2];
   Calendar baseCal = Calendar.getInstance();
   Calendar calCheckDate = Calendar.getInstance();
   
   baseCal.set(year, 0, 1);
   Date d = new Date(baseCal.getTimeInMillis());
   calCheckDate.setTime(d);
   
   if(calCheckDate.get(Calendar.DAY_OF_WEEK) > getStartingDayOfWeek() ) {
     d = FCalendar.shiftDate(baseCal, d, -(calCheckDate.get(Calendar.DAY_OF_WEEK) - getStartingDayOfWeek()) );
   }else if(calCheckDate.get(Calendar.DAY_OF_WEEK) < getStartingDayOfWeek() ) {
     d = FCalendar.shiftDate(baseCal, d, getStartingDayOfWeek() - calCheckDate.get(Calendar.DAY_OF_WEEK) - 7);
   }
   
   d = FCalendar.shiftDate(baseCal, d, 7 * week);
   dates[0] = FCalendar.shiftDate(baseCal, d, -7 );
   dates[1] = FCalendar.shiftDate(baseCal, d, -1);
  
   return dates;
  }
  
  public Date getFirstDayOfWeek(Date date){
    Date beginingOfWeek = null;
    
    Calendar calcheck = Calendar.getInstance();
    calcheck.setTime(date);
    
    if (calcheck.get(Calendar.DAY_OF_WEEK) > getStartingDayOfWeek()){
      beginingOfWeek = shiftDate(calcheck, date, -(calcheck.get(Calendar.DAY_OF_WEEK)- getStartingDayOfWeek()));
    }else{
      beginingOfWeek = new Date(date.getTime());
    }
    
    return beginingOfWeek;
  }
  
	public FocList getHolidayList(){
		if(holidayList == null){
			holidayList = getPropertyList(FCalendarDesc.FLD_HOLIDAYS_LIST);
			holidayList.reloadFromDB();
			holidayList.setDirectlyEditable(true);
			holidayList.setListOrder(new FocListOrder(HolidayDesc.FLD_START_DATE));
		}
		return holidayList;
	}
	
	public boolean isHoliday(java.sql.Date d){
    //rr Begin 
		/* FocObject found = getHolidayList().searchByPropertyDateValue(HolidayDesc.FLD_START_DATE, d);
		return found != null;*/
    
    boolean found = false;
    FocList holidayList = getHolidayList();
    for(int i=0; i< holidayList.size()&& !found; i++){
      Holiday holiday = (Holiday) holidayList.getFocObject(i);
      long startDate = FCalendar.getTimeAtMidnight(holiday.getPropertyDate(HolidayDesc.FLD_START_DATE));
      long endDate   = FCalendar.getTimeAtMidnight(holiday.getPropertyDate(HolidayDesc.FLD_END_DATE));
      long date      = FCalendar.getTimeAtMidnight(d);
      if (date <= endDate && date >= startDate){
        found = true;
      }
    }
    return found;
  }

	public void setDateAsHoliday(java.sql.Date d1, java.sql.Date d2, String reason){
		FocList holidayList = getHolidayList();
		if(holidayList != null){
			Holiday holiday = (Holiday) holidayList.newEmptyItem();
			holiday.setStartDate(d1);
			holiday.setEndDate(d2);
			holiday.setReason(reason);
		}
	}
	public boolean isWorkingDay(Date d){
		return !isWeekEnd(d) && !isHoliday(d);
	}
	
	public boolean isWorkTime(Time time){
		boolean isWork = false;
		
		FTime startTime = (FTime) getFocProperty(FCalendarDesc.FLD_START_TIME);
		FTime endTime   = (FTime) getFocProperty(FCalendarDesc.FLD_END_TIME);
		
		if(startTime.compareTo(time) <= 0 && endTime.compareTo(time) >= 0){
			isWork = true;
		}
		
		return isWork;
	}
	
	public boolean isWorkTime(long time){
		boolean isWork = false;
		
		Time startTime = getPropertyTime(FCalendarDesc.FLD_START_TIME);
		Time endTime   = getPropertyTime(FCalendarDesc.FLD_END_TIME);
		
		long startTimeLong = getTimeSinceMidnight(startTime);
		long endTimeLong = getTimeSinceMidnight(endTime);
		
		if(startTimeLong <= time && endTimeLong >= time){
			isWork = true;
		}
		return isWork;
	}

	public int getStartingDayOfWeek(){
		return getPropertyInteger(FCalendarDesc.FLD_STARTING_DAY_OF_WEEK);
	}
	
	/*public Date[] getNeerestStartEndDateForDuration(Date startDate, double durationInMinutes){
		Date[] res = null;
		if(startDate != null && durationInMinutes > 0){
			Date startDateClone = (Date)startDate.clone();
			boolean allDurationDuringWorkingTime = isAllDurationDuringWorkingTime(startDate, durationInMinutes);;
			if(!allDurationDuringWorkingTime){
				
				
				Calendar cal = Calendar.getInstance();
				FTime startTimeProp = (FTime)getFocProperty(FCalendarDesc.FLD_START_TIME);
				Time startTime = startTimeProp.getTime();
				cal.setTime(new Date(startTime.getTime()));
				int startHour = cal.get(Calendar.HOUR);
				int startMinute = cal.get(Calendar.MINUTE);
				int amPm = Calendar.AM;
				if(startHour >= 12){
					startHour = startHour % 12;
					amPm = Calendar.PM;
				}

				cal.setTime(startDateClone);
				cal.roll(Calendar.DAY_OF_MONTH, true);
				
				cal.set(Calendar.HOUR, startHour);
				cal.set(Calendar.MINUTE, startMinute);
				cal.set(Calendar.AM_PM, amPm);
				
				
				startDateClone = new Date(cal.getTime().getTime());
				allDurationDuringWorkingTime = isAllDurationDuringWorkingTime(startDateClone, durationInMinutes);
			}
			
			if(allDurationDuringWorkingTime){//we have to test if it is not a day off also
				Date endDate = new Date((long)(startDateClone.getTime() + durationInMinutes * 60 * 1000));
				res = new Date[2];
				res[0] = startDate;
				res[1] = endDate;
			}
		}
		return res;
	}*/
	
	public int getNumberOfNonWorkingDaysBetween(Date startDate, Date endDate){
		int res = 0;
		Date date = (Date)startDate.clone();
		while(!date.after(endDate)){
			if(!isWorkingDay(date)){
				res ++;
			}
			date.setTime(date.getTime() + FCalendar.MILLISECONDS_IN_DAY);
		}
		return res;
	}
	
	public double getNumberOfWorkingHoursInDay(){
		Time startTime = getPropertyTime(FCalendarDesc.FLD_START_TIME);
		Time endTime = getPropertyTime(FCalendarDesc.FLD_END_TIME);
		long workingMilliSeconds = endTime.getTime() - startTime.getTime();
		return workingMilliSeconds > 0 ? workingMilliSeconds / MILLISECONDS_IN_HOUR : 0;
	}
	
	public double getNumberOfNonWorkingHoursInDay(){
		Calendar cal = Calendar.getInstance();
		int maxHourInDay = cal.getMaximum(Calendar.HOUR_OF_DAY) + 1;
		return maxHourInDay - getNumberOfWorkingHoursInDay();
	}
	
	
	
	private double getDurationInMinutesBetweenMidNightOfFirstDateAndEndDate(Date startDate, Date endDate, double nonWorkingMinutesFactor, double nonWorkingDaysFactor){
		long totalDurationMilli = endDate.getTime() - startDate.getTime() + getTimeSinceMidnight(startDate);
		long workingDurationMilli = 0;
		
		int numberOfDays = (int)(totalDurationMilli / FCalendar.MILLISECONDS_IN_DAY);
		long numberOfMilliSecondInLastDay = (totalDurationMilli % FCalendar.MILLISECONDS_IN_DAY);
		
		int numberOfNoneWorkingDays = getNumberOfNonWorkingDaysBetween(startDate, endDate);
		
		double nonWorkingHourInDay = getNumberOfNonWorkingHoursInDay();
		long milliSecondsOutOfHoraire = (long)(((numberOfDays - numberOfNoneWorkingDays) * nonWorkingHourInDay) * FCalendar.MILLISECONDS_IN_HOUR);
		
		Time calStartTime = getPropertyTime(FCalendarDesc.FLD_START_TIME);
		long calStartTimeSinceMidnight = FCalendar.getTimeSinceMidnight(calStartTime);
		Time calEndTime = getPropertyTime(FCalendarDesc.FLD_END_TIME);
		long calEndTimeSinceMidNight = FCalendar.getTimeSinceMidnight(calEndTime);
		
		long nonWorkingMilliSecondsBeforStartInLastDay = numberOfMilliSecondInLastDay > calStartTimeSinceMidnight ? calStartTimeSinceMidnight : numberOfMilliSecondInLastDay;
		long nonWorkingMilliSecondsAfterFinsihInLastDay = numberOfMilliSecondInLastDay > calEndTimeSinceMidNight ? numberOfMilliSecondInLastDay - calEndTimeSinceMidNight : 0;
		
		milliSecondsOutOfHoraire += nonWorkingMilliSecondsBeforStartInLastDay + nonWorkingMilliSecondsAfterFinsihInLastDay;
		
		long milliSecondsInNonWorkingDays = numberOfNoneWorkingDays * FCalendar.MILLISECONDS_IN_DAY;
		
		workingDurationMilli = totalDurationMilli - milliSecondsOutOfHoraire - milliSecondsInNonWorkingDays;
		
		double duration = workingDurationMilli + (milliSecondsOutOfHoraire * nonWorkingMinutesFactor) + (milliSecondsInNonWorkingDays * nonWorkingDaysFactor);
		duration = duration / FCalendar.MILLISECONDS_IN_MINUTE;
		return duration;
	}
	
	public double getNumberOfWorkingMinutesBetween(Date startDate, Date endDate){
		double number1 = getDurationInMinutesBetweenMidNightOfFirstDateAndEndDate(startDate, startDate, 0, 0);
		double number2 = getDurationInMinutesBetweenMidNightOfFirstDateAndEndDate(startDate, endDate, 0, 0);
		return number2 - number1;
	}
	
	public static Calendar getInstanceOfJavaUtilCalandar(){
		return Calendar.getInstance();
	}
	
	private Date rolDateToStartOfNextDay(Date date){
		Calendar cal = Calendar.getInstance();
		
		Time time = getPropertyTime(FCalendarDesc.FLD_START_TIME);
		cal.setTime(time);
		int houre = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int amPm = cal.get(Calendar.AM_PM);
		
		if(houre >= 12){
			houre = houre % 12;
			amPm = Calendar.PM;
		}
		
		cal.setTime(date);
		int oldDay = cal.get(Calendar.DAY_OF_MONTH);
		int oldMonth = cal.get(Calendar.MONTH);
		cal.roll(Calendar.DAY_OF_MONTH, true);
		if(cal.get(Calendar.DAY_OF_MONTH) < oldDay){
			cal.roll(Calendar.MONTH, true);
		}
		if(cal.get(Calendar.MONTH) < oldMonth){
			cal.roll(Calendar.YEAR, true);
		}

		cal.set(Calendar.HOUR, houre);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.AM_PM, amPm);
		
		return new Date(cal.getTimeInMillis());
	}
	
	public Date getNeerestWorkingTimeForDate(Date date, boolean includeThisDate){
		Date nextWorkingDate = null;
		if(date != null){
			nextWorkingDate =(Date)date.clone();
		}
		if(nextWorkingDate != null){
			boolean searchInNextDays = true;
			if(includeThisDate){
				long timeSinceMidNight = getTimeSinceMidnight(nextWorkingDate);
				searchInNextDays = !isWorkTime(timeSinceMidNight) || !isWorkingDay(nextWorkingDate);
			}
			if(searchInNextDays){
				nextWorkingDate = rolDateToStartOfNextDay(nextWorkingDate);
				while(!isWorkingDay(nextWorkingDate)){
					nextWorkingDate = rolDateToStartOfNextDay(nextWorkingDate);
				}
			}
		}
		return nextWorkingDate;
	}
	
	private Date rolDateToEndOfPreviousDay(Date date){
		Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
		
		Time time = getPropertyTime(FCalendarDesc.FLD_END_TIME);
		cal.setTime(time);
		int houre = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		
		cal.setTime(date);
		int oldDay = cal.get(Calendar.DAY_OF_MONTH);
		int oldMonth = cal.get(Calendar.MONTH);
		cal.roll(Calendar.DAY_OF_MONTH, false);
		if(cal.get(Calendar.DAY_OF_MONTH) > oldDay){
			cal.roll(Calendar.MONTH, false);
		}
		if(cal.get(Calendar.MONTH) > oldMonth){
			cal.roll(Calendar.YEAR, false);
		}

		cal.set(Calendar.HOUR_OF_DAY, houre);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		
		return new Date(cal.getTimeInMillis());
	}
	
	public Date getNeerestPreviousWorkingTimeForDate(Date date, boolean includeThisDate){
		Date previousWorkingDate = null;
		if(date != null){
			previousWorkingDate =(Date)date.clone();
		}
		if(previousWorkingDate != null){
			boolean searchInPreviousDays = true;
			if(includeThisDate){
				long timeSinceMidNight = getTimeSinceMidnight(previousWorkingDate);
				searchInPreviousDays = !isWorkTime(timeSinceMidNight) || !isWorkingDay(previousWorkingDate);
			}
			if(searchInPreviousDays){
				previousWorkingDate = rolDateToEndOfPreviousDay(previousWorkingDate);
				while(!isWorkingDay(previousWorkingDate)){
					previousWorkingDate = rolDateToEndOfPreviousDay(previousWorkingDate);
				}
			}
		}
		return previousWorkingDate;
	}
	
	public Date rolWorkingDays_START_OF_DAY(Date date, int nbrOfWorkingDaysToRol){
		Date finalDate = null;
		if(date != null && nbrOfWorkingDaysToRol >= 0){
			finalDate = (Date)date.clone();
			for(int i = 0; i < nbrOfWorkingDaysToRol; i++){
				finalDate = getNeerestWorkingTimeForDate(finalDate, false);
			}
		}
		return finalDate;
	}
	
	public Date[] getNeerestStartEndDateForDuration(Date startDate, double durationInMinutes, boolean allowBreakingThroughtDays){
		Date[] res = null;
		if(startDate != null && durationInMinutes >= 0){
			res = new Date[2];
			Date realStartingDate = getNeerestWorkingTimeForDate(startDate, true);
			if(FCalendar.compareTimesRegardlessOfDates(realStartingDate, getPropertyTime(FCalendarDesc.FLD_END_TIME)) == 0){
				realStartingDate = getNeerestWorkingTimeForDate(realStartingDate, false);
			}
			res[0] = realStartingDate;
			double minuteNeeded = durationInMinutes;
			Time endTime = getPropertyTime(FCalendarDesc.FLD_END_TIME);
			long remainingTimeFormThisDay = compareTimesRegardlessOfDates(endTime, realStartingDate);
			double remainingMinutesFromThisDay = remainingTimeFormThisDay / (60*1000);
			
			Date endDate = new Date(realStartingDate.getTime());
			minuteNeeded -= remainingMinutesFromThisDay;
			if(minuteNeeded >= 0){
				endDate = rolWorkingDays_START_OF_DAY(realStartingDate, 1);
				double numberOfWorkingHoursInDay = getNumberOfWorkingHoursInDay();
				int neededDays = (int)(minuteNeeded / (numberOfWorkingHoursInDay*60));
				int neededMinutes = (int)(minuteNeeded % (numberOfWorkingHoursInDay*60));
				endDate = rolWorkingDays_START_OF_DAY(endDate, neededDays);
				endDate.setTime(endDate.getTime() + (neededMinutes*60*1000));
				if(FCalendar.compareTimesRegardlessOfDates(endDate, getPropertyTime(FCalendarDesc.FLD_START_TIME)) == 0){
					endDate = getNeerestPreviousWorkingTimeForDate(endDate, false);
				}
			}else{
				endDate.setTime((long)(endDate.getTime() + (durationInMinutes*60*1000)));
			}
			res[1] = endDate;			
		}
		return res;
	}
	
	public static long getTimeAtMidnight(java.util.Date date){
		long l = 0;
    if(date != null){
      long timeShift = Globals.getApp().getTimeZoneShift();
      long timeSinceAbsoluteOrigine = date.getTime()-timeShift;//AbsoluteOrigin is as 1/1/1970 at midnight
      l = timeSinceAbsoluteOrigine - (timeSinceAbsoluteOrigine % Globals.DAY_TIME);
      l = l + timeShift; 
      
      /*
      Calendar cal1 = Calendar.getInstance();
      Calendar cal2 = Calendar.getInstance();
    	
	    cal1.setTime(date);
	    cal2.setTime(new Date(0));
	    cal2.set(Calendar.DAY_OF_MONTH, cal1.get(Calendar.DAY_OF_MONTH));
	    cal2.set(Calendar.MONTH, cal1.get(Calendar.MONTH));
	    cal2.set(Calendar.YEAR, cal1.get(Calendar.YEAR));    
	    cal2.set(Calendar.HOUR, 0);    
	    cal2.set(Calendar.MINUTE, 0);
	    cal2.set(Calendar.SECOND, 0);
	    cal2.set(Calendar.AM_PM, Calendar.AM);
	    cal2.set(Calendar.MILLISECOND, 0);
	    l = cal2.getTimeInMillis();
      */
    }
    return l; 
	}
	
  public static long getTimeSinceMidnight(java.util.Date date) {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date);
    
    return cal1.getTimeInMillis() - getTimeAtMidnight(date);
  }

  public static long getDateForTimeSinceMidnight(java.util.Date timeSinceMidnight){
  	Calendar cal = Calendar.getInstance();
  	cal.setTime(timeSinceMidnight);
  	return cal.getTimeInMillis() + getTimeAtMidnight(new Date(0));
  }

  public static long compareTimesRegardlessOfDates(java.util.Date date1, java.util.Date date2) {
  	return getTimeSinceMidnight(date1) - getTimeSinceMidnight(date2);
  }
  
  public static java.util.Date getExactFullDateFromDateAndTime(java.util.Date date, Time time){
    long combinedExactTime = getTimeAtMidnight(date) + getTimeSinceMidnight(time);
    return new Date(combinedExactTime);
	}
  
  public static java.util.Date getDateAndTimeFromProperties(FDate dateProperty, FTime timeProperty){
    Date date = dateProperty.getDate();
    Time time = timeProperty.getTime();
    
    long combinedExactTime = FCalendar.getTimeAtMidnight(date) + FCalendar.getTimeSinceMidnight(time);
    return new Date(combinedExactTime);
  }
  
  
}