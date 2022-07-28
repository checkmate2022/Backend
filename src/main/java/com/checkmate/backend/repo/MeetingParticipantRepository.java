package com.checkmate.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.meeting.MeetingParticipant;
import com.checkmate.backend.entity.user.User;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

	MeetingParticipant findMeetingParticipantByUser(User user);
}
