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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> requiredEvents = findAttendeeEvents(events, request.getAttendees());
    List<TimeRange> optionalEvents = findAttendeeEvents(events, request.getOptionalAttendees());
    
    List<TimeRange> allAttendees = findOpenTimes(requiredEvents, optionalEvents, request);
    List<TimeRange> onlyOptional = findOpenTimes(Arrays.asList(), optionalEvents, request);
    List<TimeRange> onlyMandatory = findOpenTimes(requiredEvents, Arrays.asList(), request);

    if (allAttendees.size() > 0) {
      return allAttendees;
    } else if (requiredEvents.size() > 0) {
      return onlyMandatory;
    } else {
      return onlyOptional;
    }
  }

  private List<TimeRange> findOpenTimes(List<TimeRange> mandatoryAttendeeMeetings, 
                            List<TimeRange> optionalAttendeeMeetings, MeetingRequest request) {

    ArrayList<TimeRange> options = new ArrayList<TimeRange>();
    int earliestAvailable = TimeRange.START_OF_DAY;

    List<TimeRange> allMeetings = new ArrayList<TimeRange>(mandatoryAttendeeMeetings);
    allMeetings.addAll(optionalAttendeeMeetings);

    Collections.sort(allMeetings, TimeRange.ORDER_BY_START);
    
    for (TimeRange eventTime : allMeetings) {
      if (eventTime.start() >= earliestAvailable) {
        TimeRange possibleRange = 
            TimeRange.fromStartEnd(earliestAvailable, eventTime.start(), false);

        if (possibleRange.duration() >= request.getDuration()) { 
          options.add(possibleRange);
        }

        earliestAvailable = eventTime.end();
      } else if (eventTime.end() > earliestAvailable) {
          earliestAvailable = eventTime.end();
      }
    }

    // Add time after events, if it's able to fit a meeting
    if (TimeRange.END_OF_DAY - earliestAvailable >= request.getDuration()) {
      options.add(TimeRange.fromStartEnd(earliestAvailable, TimeRange.END_OF_DAY, true));
    }    

    return options;
  }

  private List<TimeRange> findAttendeeEvents(Collection<Event> events, Collection<String> attendeeList) {
    List<TimeRange> meetingTimes = new ArrayList<TimeRange>();
    
    // Keep the event as long as at least one person in the attendeeList is in it.
    for (Event event : events) {
      for (String person : attendeeList) {
        if (event.getAttendees().contains(person)) {
          meetingTimes.add(event.getWhen());
          break;
        }
      }
    }

    return meetingTimes;
  }
}
