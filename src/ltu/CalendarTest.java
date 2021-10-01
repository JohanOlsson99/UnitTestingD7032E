package ltu;

import java.util.Calendar;

import java.util.Date;

public class CalendarTest implements ICalendar {
	private Calendar date;
	
	public CalendarTest(Calendar date) {
		this.date = date;
	}

	public CalendarTest() {

	}

    @Override
	public Date getDate() {
		if (this.date != null) {
			return this.date.getTime();
		}
		Calendar cal = Calendar.getInstance();
        cal.set(2016, Calendar.JANUARY, 1);
		return cal.getTime();
	}
}
