package com.checkmate.backend.model.response;

import com.checkmate.backend.entity.meeting.MeetingParticipantType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingParticipantResponse {

	private String username;

	private String userImag;

	private MeetingParticipantType meetingParticipantType;

}
