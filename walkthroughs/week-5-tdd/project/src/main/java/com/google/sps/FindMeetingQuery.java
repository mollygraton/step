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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> options = new ArrayList<TimeRange>();
    List<TimeRange> requiredEvents = new ArrayList<TimeRange>();

    // Ignore events that don't have any required attendees
    for (Event event : events) {
        for (String person : request.getAttendees()) {
            if (event.getAttendees().contains(person)) {
                requiredEvents.add(event.getWhen());
                break;
            }
        }
    }

    Collections.sort(requiredEvents, TimeRange.ORDER_BY_START);

    int earliestAvailable = TimeRange.START_OF_DAY;
    
    for(TimeRange eventTime : requiredEvents) {
        if (eventTime.start() >= earliestAvailable) {
        
            TimeRange possibleRange = new TimeRange(earliestAvailable, eventTime.start()-earliestAvailable);

            if (possibleRange.duration() >= request.getDuration()){ 
                options.add(possibleRange);
            }

            earliestAvailable = eventTime.end();

        } else if (eventTime.end() > earliestAvailable) {
            earliestAvailable = eventTime.end();
        }
    }

    // Add time after events, if it's able to fit a meeting
    if (TimeRange.END_OF_DAY - earliestAvailable >= request.getDuration()) {
        options.add(new TimeRange(earliestAvailable, TimeRange.END_OF_DAY - earliestAvailable + 1));
    }    

    return options;
  }
}
