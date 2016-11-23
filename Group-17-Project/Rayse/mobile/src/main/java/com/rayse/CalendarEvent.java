package com.rayse;

import java.util.Date;

/*
 * Created by David Laundav and contributed by Christian Orthmann
 *
 * Copyright 2013 David Laundav
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

public class CalendarEvent implements Comparable<CalendarEvent>{

    private String title;
    private Date begin, end;
    private boolean allDay;
    private long eventID;
    private int color;

    public CalendarEvent() {

    }

    public CalendarEvent(String title, Date begin, Date end, boolean allDay, int color, long eventID) {
        setTitle(title);
        setBegin(begin);
        setEnd(end);
        setAllDay(allDay);
        setColor(color);
        setEventID(eventID);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long id) {
        this.eventID = id;
    }

    @Override
    public String toString(){
        return getTitle() + " " + getBegin() + " " + getEnd() + " " + isAllDay() + " " + getColor();
    }

    @Override
    public int compareTo(CalendarEvent other) {
        // -1 = less, 0 = equal, 1 = greater
        return getBegin().compareTo(other.begin);
    }

}
