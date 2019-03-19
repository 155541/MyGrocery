package revolhope.splanes.com.mygrocery.helpers.reminder;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.provider.CalendarContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class AppReminder {

    private AppReminder() {
    }

    public static void setReminder(Item item, ContentResolver contentResolver) {

        ContentValues values = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(item.getDateReminder());
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        values.put(CalendarContract.Events.DTSTART, item.getDateReminder());
        values.put(CalendarContract.Events.TITLE, "Comprar: " + item.getItemName());
        values.put(CalendarContract.Events.DESCRIPTION, "Quantitat: " + item.getAmount());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL=" +
                new SimpleDateFormat("yyyyMMdd", Locale.FRANCE).format(calendar.getTime()));
        values.put(CalendarContract.Events.DURATION, "+P1H");
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
    }
}
