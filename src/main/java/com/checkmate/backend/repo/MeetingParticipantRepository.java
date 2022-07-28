package com.checkmate.backend.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.meeting.MeetingParticipant;
import com.checkmate.backend.entity.user.User;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

	@Query("select c from MeetingParticipant c where c.meeting=:meeting and c.user=:user")
	Optional<MeetingParticipant> findMeetingParticipantsByMeetingandUser(@Param("meeting") Meeting meeting,
		@Param("user") User user);
}
