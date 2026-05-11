package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.entity.Meeting;
import ch.uzh.ifi.hase.soprafs26.repository.MeetingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MeetingService {

    private final MeetingRepository meetingRepository;


    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public List<Meeting> getMeetings(long id) {
        List<Meeting> meetings = meetingRepository.findByOwner(id);
        meetings.addAll(meetingRepository.findByInvitedUser(id));

        return meetings;
    }

    public Meeting getMeeting(long id) {
        Meeting meeting = meetingRepository.findByid(id);
        return meeting;
    }

    public Meeting createMeeting(Meeting meeting) {

        meetingRepository.save(meeting);
        meetingRepository.flush();
        return  meeting;
    }

    public void deleteMeeting(Meeting meeting) {
        meetingRepository.delete(meeting);
    }

}


