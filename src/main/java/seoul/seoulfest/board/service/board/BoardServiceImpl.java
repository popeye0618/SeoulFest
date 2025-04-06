package seoul.seoulfest.board.service.board;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.auth.exception.AuthErrorCode;
import seoul.seoulfest.board.dto.board.request.CreateBoardReq;
import seoul.seoulfest.board.dto.board.request.UpdateBoardReq;
import seoul.seoulfest.board.dto.board.response.BoardListRes;
import seoul.seoulfest.board.entity.Board;
import seoul.seoulfest.board.exception.BoardErrorCode;
import seoul.seoulfest.board.repository.BoardRepository;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.enums.Role;
import seoul.seoulfest.util.security.SecurityUtil;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

	private final SecurityUtil securityUtil;
	private final BoardRepository boardRepository;

	/**
	 * 게시판 생성
	 *
	 * @param request 이름
	 */
	@Override
	@Transactional
	public void createBoard(CreateBoardReq request) {
		validateAdmin();

		Board board = Board.builder()
			.name(request.getName())
			.build();

		boardRepository.save(board);
	}

	/**
	 * 게시판 이름 수정
	 *
	 * @param request 이름, id
	 */
	@Override
	@Transactional
	public void updateBoard(UpdateBoardReq request) {
		validateAdmin();

		Board board = boardRepository.findById(request.getBoardId())
			.orElseThrow(() -> new BusinessException(BoardErrorCode.NOT_EXIST_BOARD));

		board.setName(request.getName());
	}

	/**
	 * 게시판 삭제
	 *
	 * @param boardId id
	 */
	@Override
	@Transactional
	public void removeBoard(Long boardId) {
		validateAdmin();

		Board board = boardRepository.findById(boardId)
			.orElseThrow(() -> new BusinessException(BoardErrorCode.NOT_EXIST_BOARD));

		boardRepository.delete(board);
	}

	private void validateAdmin() {

		Member member = securityUtil.getCurrentMember();

		if (!member.getRole().equals(Role.ROLE_ADMIN)) {
			throw new BusinessException(AuthErrorCode.INVALID_ROLE);
		}
	}

	@Override
	public List<BoardListRes> getAllBoardName() {

		List<Board> allBoards = boardRepository.findAll();

		return allBoards.stream().map(
			board -> BoardListRes.builder()
				.boardId(board.getId())
				.name(board.getName())
				.build()
		).toList();
	}
}
