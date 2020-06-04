// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

        // get TimeRange of events filtered by attendees
        List<TimeRange> eventsTimeRanges = new ArrayList<>();
        for (Event event: events) 
            if(containSameAttendees(event, request))
                eventsTimeRanges.add(event.getWhen());

        Collections.sort(eventsTimeRanges, TimeRange.ORDER_BY_START);
        Collection<TimeRange> combinedTimeRanges = combineTimeRanges(eventsTimeRanges);

        return getFreeTimeRanges(combinedTimeRanges, request);
    }

    /** Check if the event and the request contains the same attendee. */
    private boolean containSameAttendees(Event event, MeetingRequest request) {
        for (String attendee: event.getAttendees())
            if (request.getAttendees().contains(attendee))
                return true;
        return false;
    }


    /**
     * Combine overlapped TimeRanges in a sorted List.
     * @return a sorted Collection of TimeRanges without overlap.
     */
    private Collection<TimeRange> combineTimeRanges(List<TimeRange> timeRanges) {
        Collection<TimeRange> combinedTimeRanges = new ArrayList<>();

        if (timeRanges.size() == 0)
            return combinedTimeRanges;
        
        TimeRange currentEvent = timeRanges.get(0);
        for (int i = 1; i < timeRanges.size(); i++) {
            TimeRange nextEvent = timeRanges.get(i);
            if (currentEvent.overlaps(nextEvent)) {
                // update current TimeRange
                int start = currentEvent.start();
                int end = currentEvent.contains(nextEvent.end())? currentEvent.end() : nextEvent.end();
                currentEvent = TimeRange.fromStartEnd(start, end, false);
            } else {
                // store current TimeRange
                combinedTimeRanges.add(currentEvent);
                currentEvent = nextEvent;
            }
        }
        combinedTimeRanges.add(currentEvent);

        return combinedTimeRanges;
    }


    /**
     * Get a Collection of free TimeRanges given a collection of busy TimeRanges.
     * Only include TimeRanges with at least requested duration.
     */
    private Collection<TimeRange> getFreeTimeRanges(Collection<TimeRange> busyTimeRanges, MeetingRequest request) {
        Collection<TimeRange> freeTimeRanges = new ArrayList<>();
        int start = TimeRange.START_OF_DAY;
        for (TimeRange time: busyTimeRanges) {
            int newEnd = time.start();
            int newStart = time.end();
            if (newEnd - start >= request.getDuration())
                freeTimeRanges.add(TimeRange.fromStartEnd(start, newEnd, false));
            start = newStart;
        }

        if (TimeRange.END_OF_DAY - start >= request.getDuration())
            freeTimeRanges.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));

        return freeTimeRanges;
    }
}
