package utils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;

public final class DateCalculator {

  private static final int TF_PAY_HOUR = -2;

  private DateCalculator() {
  }

  /**
   * Generates the date to be selected in yyy-MM-dd format.
   *
   * @param daysFromToday The number of days from today.
   * @return The date to be selected.
   */
  public static String dateGenerator(int daysFromToday) {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, daysFromToday);
    Date d = c.getTime();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    return format.format(d);
  }

  /**
   * Generates the date of specific day of the week
   * to be selected in yyy-MM-dd format.
   *
   * @param day The day of the week.
   * @return The date to be selected.
   */
  public static String dateGeneratorWithSpecificDay(DayOfWeek day) {
    Calendar c = Calendar.getInstance();
    c.set(Calendar.DAY_OF_WEEK, day.get(WeekFields.SUNDAY_START.dayOfWeek()));
    Date d = c.getTime();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    return format.format(d);
  }

  /**
   * Generates the date to be selected in yyy-MM-dd hh:mm:ss format.
   *
   * @param daysFromToday The number of days from today.
   * @return The date to be selected.
   */
  public static String dateGeneratorWithHour(int daysFromToday) {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, daysFromToday);
    Date d = c.getTime();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    return format.format(d);
  }

  /**
   * Dynamically gets the current time minus 3 (hours) to be able to edit reservations
   * and make them payable with TF-Pay.
   *
   * @return Integer
   */
  public static int getPayableHour() {
    ZoneId z = ZoneId.of("Europe/Paris");
    ZonedDateTime zonedDateTime = Instant.now().atZone(z);
    return zonedDateTime.plusHours(TF_PAY_HOUR).getHour();
  }

  /**
   * Gets today's date with yyyy-MM-dd'T'HH:mm:ss format.
   *
   * @return String
   */
  public static String getTodayDateFixedFormat() {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, 0);
    Date d = c.getTime();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    return format.format(d);
  }
}
