package com.checkmate.backend.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.comment.Comment;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.TeamDto;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class CommentRepositoryTest {
	Team savedTeam;
	User savedUser;
	Channel savedChannel;
	Board savedBoard;
	Comment savedComment;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private CommentRepository commentRepository;

	@BeforeEach
	void 게시글생성() {
		//given
		LocalDateTime now = LocalDateTime.now();
		User user = new User("repo", "repo", "repo", ProviderType.LOCAL, RoleType.USER, now, now);
		savedUser = userRepository.save(user);

		TeamDto teamDto = new TeamDto("팀이름", "팀설명");
		Team team = new Team(teamDto);
		savedTeam = teamRepository.save(team);

		Channel channel1 = new Channel("channel1", savedTeam);
		savedChannel = channelRepository.save(channel1);

		Board board = new Board("제목", "내용", savedChannel, savedTeam.getTeamSeq(), savedUser.getUserSeq());
		savedBoard = boardRepository.save(board);

		Comment comment = new Comment("내용", savedBoard, savedUser.getUserSeq(), "emoticon");
		savedComment = commentRepository.save(comment);

	}

	@Test
	void findAllByBoard() {
		List<Comment> commentList = commentRepository.findAllByBoard(savedBoard);

		assertEquals(commentList.get(0).getBoard(), savedBoard);
		assertEquals(commentList.get(0).getEmoticonUrl(), "emoticon");
	}

	@AfterEach
	void delete() {
		commentRepository.delete(savedComment);
		boardRepository.delete(savedBoard);
		channelRepository.delete(savedChannel);
		teamRepository.delete(savedTeam);
		userRepository.delete(savedUser);
	}
}