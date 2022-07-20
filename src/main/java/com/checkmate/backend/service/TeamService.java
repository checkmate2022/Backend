package com.checkmate.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.advice.exception.UserNotFoundException;
import com.checkmate.backend.entity.avatar.Avatar;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.team.TeamParticipant;
import com.checkmate.backend.entity.team.TeamRoleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.TeamDto;
import com.checkmate.backend.model.request.TeamRequest;
import com.checkmate.backend.model.response.ParticipantResponse;
import com.checkmate.backend.model.response.TeamResponse;
import com.checkmate.backend.repo.TeamParticipantRepository;
import com.checkmate.backend.repo.TeamRepository;
import com.checkmate.backend.repo.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

	private final TeamRepository teamRepository;
	private final UserRepository userRepository;
	private final TeamParticipantRepository participantRepository;

	// 전체 팀 조회
	@Transactional(readOnly = true)
	public List<Team> findTeams() {
		List<Team> teams = teamRepository.findAll();

		return teams;
	}

	// 단건 팀 조회
	@Transactional(readOnly = true)
	public Team findOne(Long teamId) {
		Team team = teamRepository.findById(teamId).orElseThrow(
			() -> new IllegalArgumentException("팀이 존재하지 않습니다.")
		);
		return team;
	}

	// 사용자별 팀 조회
	public List<TeamResponse> findTeamByUser(User user) {
		//user에 따라 participant 찾음
		List<TeamParticipant> participants = participantRepository.findAllByUser(user);

		List<TeamResponse> teams = new ArrayList<>();
		//반복문
		for (TeamParticipant p : participants) {
			List<String> users = new ArrayList<>();
			//response 객체 생성
			Optional<Team> team = teamRepository.findById(p.getTeam().getTeamSeq());
			TeamResponse response = TeamResponse.builder().
				teamSeq(team.get().getTeamSeq())
				.teamName(team.get().getTeamName())
				.teamDescription(team.get().getTeamDescription())
				.user(team.get().getUser())
				.build();
			//참여자 정보 담아줌
			for (TeamParticipant teamP : team.get().getParticipants()) {
				users.add(teamP.getUser().getUsername());
			}
			response.setParticipants(users);
			//List 담기
			teams.add(response);
		}

		return teams;
	}

	// 팀별 사용자 조회
	public List<ParticipantResponse> findUserByTeam(long teamId) {
		Team team = teamRepository.findById(teamId).orElseThrow(
			() -> new IllegalArgumentException("해당 team은 존재하지 않습니다.")
		);
		//user에 따라 participant 찾음
		List<TeamParticipant> participants = participantRepository.findAllByTeam(team);

		List<ParticipantResponse> participantResponses = new ArrayList<>();
		//반복문
		for (TeamParticipant p : participants) {
			//response 객체 생성
			Optional<User> user = userRepository.findById(p.getUser().getUserSeq());
			ParticipantResponse response = ParticipantResponse.builder().
				userSeq(user.get().getUserSeq())
				.userId(user.get().getUserId())
				.username(user.get().getUsername())
				.userImg(user.get().getUserImage())
				.teamRoleType(p.getTeamRoleType())
				.build();

			for (Avatar a : user.get().getAvatar()) {
				if (a.getIsBasic()) {
					response.setAvatar(a);
				}
			}
			participantResponses.add(response);
		}
		return participantResponses;
	}

	// team 등록
	public Team make(TeamRequest teamReq, User user) {

		TeamDto teamDto = new TeamDto(teamReq.getTeamName(), teamReq.getTeamDescription());

		Team team = new Team(teamDto);
		List<String> participants = teamReq.getParticipantName();
		Team save = teamRepository.save(team);
		//작성자 설정
		save.setUser(user);
		//participant 닉네임으로 담음
		for (String p : participants) {
			//닉네임으로 User 찾음
			User findUser = userRepository.findByUsername(p);
			//participant 설정
			TeamParticipant participant = new TeamParticipant(findUser, save, TeamRoleType.MEMBER);
			participant = participantRepository.save(participant);
			save.addParticipant(participant);
		}
		//작성자도 참여자로 넣음
		TeamParticipant participant = new TeamParticipant(user, save, TeamRoleType.LEADER);
		participant = participantRepository.save(participant);

		save.addParticipant(participant);
		return save;
	}

	// team 수정
	public Team update(Long teamId, TeamRequest teamReq, User user) {

		TeamDto teamDto = new TeamDto(teamReq.getTeamName(), teamReq.getTeamDescription());
		Team team = teamRepository.findById(teamId).orElseThrow(
			() -> new IllegalArgumentException("해당 team은 존재하지 않습니다.")
		);
		if (!team.getUser().equals(user)) {
			throw new UserNotFoundException("팀 리더가 아니면 팀 수정이 불가능합니다.");
		}
		//기존 participants 삭제
		participantRepository.deleteAllByTeam(team);
		team.deleteAllParticipants();

		//새롭게 participants 추가
		List<String> participants = teamReq.getParticipantName();
		for (var p : participants) {
			User findUser = userRepository.findByUsername(p);
			TeamParticipant participant = new TeamParticipant(findUser, team, TeamRoleType.MEMBER);
			participant = participantRepository.save(participant);
			team.addParticipant(participant);
		}

		TeamParticipant participant = new TeamParticipant(team.getUser(), team, TeamRoleType.LEADER);
		participant = participantRepository.save(participant);
		team.addParticipant(participant);

		team.update(teamDto);

		team = teamRepository.save(team);

		return team;
	}

	//team 삭제
	public void delete(Long teamId, User user) {
		Team team = teamRepository.findById(teamId).orElseThrow(
			() -> new IllegalArgumentException("해당 team은 존재하지 않습니다.")
		);
		if (!team.getUser().equals(user)) {
			throw new UserNotFoundException("팀 리더가 아닙니다.");
		}
		teamRepository.deleteById(teamId);
	}

}
