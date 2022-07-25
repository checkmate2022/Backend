package com.checkmate.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.team.Team;

public interface MeetingRepository extends JpaRepository<Meeting, String> {

	List<Meeting> findMeetingsByTeam(Team team);

}
