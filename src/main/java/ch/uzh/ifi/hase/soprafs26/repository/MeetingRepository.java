package ch.uzh.ifi.hase.soprafs26.repository;


import ch.uzh.ifi.hase.soprafs26.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("meetingRepository")
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByOwner(Long ownerId);

    List<Meeting> findByInvitedUser(Long invitedUserId);

    Meeting findByid(long id);
}
