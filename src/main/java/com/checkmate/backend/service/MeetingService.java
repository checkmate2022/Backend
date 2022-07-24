package com.checkmate.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.meeting.MeetingType;
import com.checkmate.backend.repo.MeetingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {

	private final MeetingRepository meetingRepository;

	//host가 회의 종료
	public void endMeeting(String meetingId) {
		Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(
			() -> new IllegalArgumentException("회의가 없습니다.")
		);
		meeting.setMeetingType(MeetingType.END);
	}

	public void startMeeting(String meetingId) {
		Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(
			() -> new IllegalArgumentException("회의가 없습니다.")
		);
		meeting.setMeetingType(MeetingType.DOING);
	}

}
