package com.patrickohalloran.calendardemo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;

/*
 * Created by David Laundav and contributed by Christian Orthmann
 *
 * Copyright 2013 Daivd Laundav
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * References:
 * http://stackoverflow.com/questions/5883938/getting-events-from-calendar
 *
 * Please do not delete the references as they gave inspiration for the implementation
 */


public class CalendarService {

    // Default constructor
    public static void readCalendar(Context context) {
        readCalendar(context, 1, 0);
    }

    // Use to specify specific the time span
    public static HashMap<String, List<CalendarEvent>> readCalendar(Context context, int year, int month) {

        ContentResolver contentResolver = context.getContentResolver();

//        // Create a cursor and read from the calendar (for Android API below 4.0)
//        final Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
//                (new String[] { "_id", "displayName", "selected" }), null, null, null);

		/*
        * Use the cursor below for Android API 4.0+ (Thanks to SLEEPLisNight)
		*
		* Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/events"),
		* new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" },
		* null, null, null);
		*Uri.parse("content://com.android.calendar/events" "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"
		*/

        String[] EVENT_PROJECTION = new String[]{CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT};

        Uri uri = CalendarContract.Calendars.CONTENT_URI;

//        String selection = "(" + CalendarContract.Calendars._ID + " = ?)";
//        String[] selectionArgs = new String[]{id};

//        Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/events"),
//                new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" },
//                null, null, null);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Cursor cursor = contentResolver.query(uri, EVENT_PROJECTION, null, null, null);

        // Create a set containing all of the calendar IDs available on the phone
        HashSet<String> calendarIds = getCalenderIds(cursor);


        // Create a hash map of calendar ids and the events of each id
        HashMap<String, List<CalendarEvent>> eventMap = new HashMap<String, List<CalendarEvent>>();

        // Loop over all of the calendars
        for (String id : calendarIds) {

//            // Create a builder to define the time span
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
//            long now = new Date().getTime();
//
//            // create the time span based on the inputs
//            ContentUris.appendId(builder, now - (DateUtils.DAY_IN_MILLIS * days) - (DateUtils.HOUR_IN_MILLIS * hours));
//            ContentUris.appendId(builder, now + (DateUtils.DAY_IN_MILLIS * days) + (DateUtils.HOUR_IN_MILLIS * hours));

            Calendar startTime = Calendar.getInstance();
            startTime.set(year,month,01,00,00);

            Calendar endTime= Calendar.getInstance();
            endTime.set(year,month,startTime.getActualMaximum(Calendar.DAY_OF_MONTH),59,59);

            ContentUris.appendId(builder, startTime.getTimeInMillis());
            ContentUris.appendId(builder, endTime.getTimeInMillis());

            String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis()
                    + " ) AND ( "
                    + CalendarContract.Events.CALENDAR_ID + " = ?"
                    + " ) AND ( "
                    + CalendarContract.Events.DTEND + " <= " + endTime.getTimeInMillis() + " ))";

            // Create an event cursor to find all events in the calendar
            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[] { "title", "begin", "end", "allDay", "displayColor", "event_id"}, "((" + CalendarContract.Events.CALENDAR_ID + " = ?))",
                    new String[] {id}, "startDay ASC, startMinute ASC");

            System.out.println("eventCursor count="+eventCursor.getCount());

            // If there are actual events in the current calendar, the count will exceed zero
            if(eventCursor.getCount()>0)
            {

                // Create a list of calendar events for the specific calendar
                List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();

                // Move to the first object
                eventCursor.moveToFirst();

                // Create an object of CalendarEvent which contains the title, when the event begins and ends,
                // and if it is a full day event or not and the color
                CalendarEvent ce = loadEvent(eventCursor);

                // Adds the first object to the list of events
                eventList.add(ce);

                System.out.println(ce.toString());

                // While there are more events in the current calendar, move to the next instance
                while (eventCursor.moveToNext())
                {

                    // Adds the object to the list of events
                    ce = loadEvent(eventCursor);
                    eventList.add(ce);

                    System.out.println(ce.toString());

                }

                Collections.sort(eventList);
                eventMap.put(id, eventList);

                System.out.println(eventMap.keySet().size() + " " + eventMap.values());

            }

            eventCursor.close();
        }

        return eventMap;
    }

    // Returns a new instance of the calendar object
    private static CalendarEvent loadEvent(Cursor csr) {
        return new CalendarEvent(csr.getString(0),
                new Date(csr.getLong(1)),
                new Date(csr.getLong(2)),
                !csr.getString(3).equals("0"),
                Integer.valueOf(csr.getString(4)),
                Long.parseLong(csr.getString(5))); //getHexCode(csr.getString(4)),
    }

    private static String getHexCode(String num) {
        Integer numRep = Integer.valueOf(num);
        return String.format("#%06X", (0xFFFFFF & numRep));
    }

    // Creates the list of calendar ids and returns it in a set
    private static HashSet<String> getCalenderIds(Cursor cursor) {

        HashSet<String> calendarIds = new HashSet<String>();

        try
        {

            // If there are more than 0 calendars, continue
            if(cursor.getCount() > 0)
            {

                // Loop to set the id for all of the calendars
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    Boolean selected = !cursor.getString(2).equals("0");

                    System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
                    calendarIds.add(_id);

                }
            }
        }

        catch(AssertionError ex)
        {
            ex.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return calendarIds;

    }
}
