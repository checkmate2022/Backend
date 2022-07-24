package com.checkmate.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.meeting.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, String> {

}
