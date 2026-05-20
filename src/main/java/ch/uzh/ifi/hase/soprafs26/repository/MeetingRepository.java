package ch.uzh.ifi.hase.soprafs26.repository;


import ch.uzh.ifi.hase.soprafs26.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("meetingRepository")
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByOwner(Long ownerId);

    @Query("SELECT m FROM Meeting m WHERE m.invitedUser = :userId AND m.owner <> :userId")
    List<Meeting> findByInvitedUser(@Param("userId") Long userId);

    Meeting findByid(long id);
}
