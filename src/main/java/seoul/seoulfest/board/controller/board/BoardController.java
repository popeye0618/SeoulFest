package seoul.seoulfest.board.controller.board;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.board.dto.board.request.CreateBoardReq;
import seoul.seoulfest.board.dto.board.request.UpdateBoardReq;
import seoul.seoulfest.board.dto.board.response.BoardListRes;
import seoul.seoulfest.board.service.board.BoardService;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {

	private final BoardService boardService;

	@PostMapping("/auth/admin/board")
	public ResponseEntity<Response<Void>> createBoard(@RequestBody CreateBoardReq request) {
		boardService.createBoard(request);

		return Response.ok().toResponseEntity();
	}

	@PatchMapping("/auth/admin/board")
	public ResponseEntity<Response<Void>> updateBoard(@RequestBody UpdateBoardReq request) {
		boardService.updateBoard(request);

		return Response.ok().toResponseEntity();
	}

	@DeleteMapping("/auth/admin/board/{boardId}")
	public ResponseEntity<Response<Void>> removeBoard(@PathVariable Long boardId) {
		boardService.removeBoard(boardId);

		return Response.ok().toResponseEntity();
	}

	@GetMapping("/auth/user/board")
	public ResponseEntity<Response<List<BoardListRes>>> getBoardList() {
		List<BoardListRes> boardList = boardService.getAllBoardName();

		return Response.ok(boardList).toResponseEntity();
	}
}
