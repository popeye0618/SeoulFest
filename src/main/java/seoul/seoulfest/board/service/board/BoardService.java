package seoul.seoulfest.board.service.board;

import java.util.List;

import seoul.seoulfest.board.dto.board.request.CreateBoardReq;
import seoul.seoulfest.board.dto.board.request.UpdateBoardReq;
import seoul.seoulfest.board.dto.board.response.BoardListRes;

public interface BoardService {

	/**
	 * 게시판 생성
	 *
	 * @param request 이름
	 */
	void createBoard(CreateBoardReq request);

	/**
	 * 게시판 이름 수정
	 *
	 * @param request 이름, id
	 */
	void updateBoard(UpdateBoardReq request);

	/**
	 * 게시판 삭제
	 *
	 * @param boardId id
	 */
	void removeBoard(Long boardId);

	List<BoardListRes> getAllBoardName();
}
