package seoul.seoulfest.board.service.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import seoul.seoulfest.board.dto.post.request.CreatePostReq;
import seoul.seoulfest.board.dto.post.request.UpdatePostReq;
import seoul.seoulfest.board.dto.post.response.PostListRes;
import seoul.seoulfest.board.dto.post.response.PostRes;
import seoul.seoulfest.board.entity.Board;
import seoul.seoulfest.board.entity.Post;
import seoul.seoulfest.board.exception.BoardErrorCode;
import seoul.seoulfest.board.repository.BoardRepository;
import seoul.seoulfest.board.repository.PostRepository;
import seoul.seoulfest.board.service.media.PostMediaService;
import seoul.seoulfest.exception.BusinessException;
import seoul.seoulfest.member.entity.Member;
import seoul.seoulfest.member.enums.Role;
import seoul.seoulfest.util.security.SecurityUtil;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private PostMediaService postMediaService;

	@Mock
	private BoardRepository boardRepository;

	@Mock
	private PostRepository postRepository;

	@InjectMocks
	private PostServiceImpl postService;

	@Test
	public void createPost_success() {
		// Arrange
		Long boardId = 1L;
		CreatePostReq request = CreatePostReq.builder()
			.boardId(boardId)
			.title("Test Title")
			.content("Test Content")
			.keyList(List.of("key1", "key2"))
			.build();

		Member member = Member.builder()
			.username("admin")
			.role(Role.ROLE_ADMIN)
			.build();
		ReflectionTestUtils.setField(member, "id", 1L);

		Board board = Board.builder().name("Test Board").build();
		ReflectionTestUtils.setField(board, "id", boardId);

		when(securityUtil.getCurrentMember()).thenReturn(member);
		when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
		when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
			Post p = invocation.getArgument(0);
			ReflectionTestUtils.setField(p, "id", 100L);
			ReflectionTestUtils.setField(p, "updatedAt", LocalDateTime.now());
			return p;
		});

		// Act
		postService.createPost(request);

		// Assert
		verify(postRepository, times(1)).save(any(Post.class));
		verify(postMediaService, times(1)).createPostMedia(any(Post.class), eq(request.getKeyList()));
	}

	@Test
	public void createPost_fail_boardNotFound() {
		// Arrange
		Long boardId = 1L;
		CreatePostReq request = CreatePostReq.builder()
			.boardId(boardId)
			.title("Test Title")
			.content("Test Content")
			.keyList(Collections.emptyList())
			.build();
		Member member = Member.builder()
			.username("admin")
			.role(Role.ROLE_ADMIN)
			.build();
		ReflectionTestUtils.setField(member, "id", 1L);
		when(securityUtil.getCurrentMember()).thenReturn(member);
		when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> postService.createPost(request))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(BoardErrorCode.NOT_EXIST_BOARD.getMessage());
	}

	@Test
	public void getPosts_success() {
		// Arrange
		Long boardId = 1L;
		Member member = Member.builder().username("admin").role(Role.ROLE_ADMIN).build();
		ReflectionTestUtils.setField(member, "id", 1L);
		Board board = Board.builder().name("Test Board").build();
		ReflectionTestUtils.setField(board, "id", boardId);
		Post post = Post.builder()
			.board(board)
			.member(member)
			.title("Test Title")
			.content("Test Content")
			.build();
		ReflectionTestUtils.setField(post, "id", 100L);
		ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.now());
		Page<Post> postPage = new PageImpl<>(List.of(post));
		when(postRepository.findByBoardId(eq(boardId), any(Pageable.class))).thenReturn(postPage);

		Pageable pageable = PageRequest.of(0, 10, Sort.by("updatedAt").descending());

		// Act
		Page<PostListRes> result = postService.getPosts(boardId, pageable);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		PostListRes res = result.getContent().get(0);
		assertThat(res.getPostId()).isEqualTo(100L);
		assertThat(res.getTitle()).isEqualTo(post.getTitle());
		assertThat(res.getWriter()).isEqualTo(member.getUsername());
	}

	@Test
	public void getPost_success() {
		// Arrange
		Long postId = 100L;
		Member member = Member.builder().username("admin").role(Role.ROLE_ADMIN).build();
		ReflectionTestUtils.setField(member, "id", 1L);
		Board board = Board.builder().name("Test Board").build();
		ReflectionTestUtils.setField(board, "id", 1L);
		Post post = Post.builder()
			.board(board)
			.member(member)
			.title("Test Title")
			.content("Test Content")
			.build();
		// set id and updatedAt via Reflection
		ReflectionTestUtils.setField(post, "id", postId);
		ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.now());
		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		// Act
		PostRes res = postService.getPost(postId);

		// Assert
		assertThat(res).isNotNull();
		assertThat(res.getPostId()).isEqualTo(postId);
		assertThat(res.getTitle()).isEqualTo(post.getTitle());
		assertThat(res.getViewCount()).isEqualTo(post.getViewCount());
	}

	@Test
	public void getPost_fail_postNotFound() {
		// Arrange
		Long postId = 100L;
		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> postService.getPost(postId))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(BoardErrorCode.NOT_EXIST_POST.getMessage());
	}

	@Test
	public void updatePost_success() {
		// Arrange
		Long postId = 100L;
		Member member = Member.builder().username("admin").role(Role.ROLE_ADMIN).build();
		ReflectionTestUtils.setField(member, "id", 1L);
		Board board = Board.builder().name("Test Board").build();
		ReflectionTestUtils.setField(board, "id", 1L);
		Post post = Post.builder()
			.board(board)
			.member(member)
			.title("Old Title")
			.content("Old Content")
			.build();
		ReflectionTestUtils.setField(post, "id", postId);
		ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.now());
		when(postRepository.findById(postId)).thenReturn(Optional.of(post));
		when(securityUtil.getCurrentMember()).thenReturn(member);

		UpdatePostReq req = UpdatePostReq.builder()
			.postId(postId)
			.title("New Title")
			.content("New Content")
			.keyList(List.of("key1", "key2"))
			.build();

		// Act
		postService.updatePost(postId, req);

		// Assert
		assertThat(post.getTitle()).isEqualTo("New Title");
		assertThat(post.getContent()).isEqualTo("New Content");
		verify(postMediaService, times(1)).updatePostMedia(post, req.getKeyList());
	}

	@Test
	public void updatePost_fail_notWriter() {
		// Arrange
		Long postId = 100L;
		Member writer = Member.builder().username("writer").role(Role.ROLE_USER).build();
		ReflectionTestUtils.setField(writer, "id", 2L);
		Member currentMember = Member.builder().username("admin").role(Role.ROLE_ADMIN).build();
		ReflectionTestUtils.setField(currentMember, "id", 1L);
		Board board = Board.builder().name("Test Board").build();
		ReflectionTestUtils.setField(board, "id", 1L);
		Post post = Post.builder()
			.board(board)
			.member(writer)
			.title("Old Title")
			.content("Old Content")
			.build();
		ReflectionTestUtils.setField(post, "id", postId);
		when(postRepository.findById(postId)).thenReturn(Optional.of(post));
		when(securityUtil.getCurrentMember()).thenReturn(currentMember);

		UpdatePostReq req = UpdatePostReq.builder()
			.postId(postId)
			.title("New Title")
			.content("New Content")
			.keyList(List.of("key1"))
			.build();

		// Act & Assert
		assertThatThrownBy(() -> postService.updatePost(postId, req))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(BoardErrorCode.NOT_WRITER.getMessage());
	}

	@Test
	public void deletePost_success() {
		// Arrange
		Long postId = 100L;
		Member member = Member.builder().username("admin").role(Role.ROLE_ADMIN).build();
		ReflectionTestUtils.setField(member, "id", 1L);
		Board board = Board.builder().name("Test Board").build();
		ReflectionTestUtils.setField(board, "id", 1L);
		Post post = Post.builder()
			.board(board)
			.member(member)
			.title("Title")
			.content("Content")
			.build();
		ReflectionTestUtils.setField(post, "id", postId);
		ReflectionTestUtils.setField(post, "updatedAt", LocalDateTime.now());
		when(postRepository.findById(postId)).thenReturn(Optional.of(post));
		when(securityUtil.getCurrentMember()).thenReturn(member);

		// Act
		postService.deletePost(postId);

		// Assert
		verify(postMediaService, times(1)).removePostMedia(post);
		verify(postRepository, times(1)).delete(post);
	}

	@Test
	public void deletePost_fail_notWriter() {
		// Arrange
		Long postId = 100L;
		Member writer = Member.builder().username("writer").role(Role.ROLE_USER).build();
		ReflectionTestUtils.setField(writer, "id", 2L);
		Member currentMember = Member.builder().username("admin").role(Role.ROLE_ADMIN).build();
		ReflectionTestUtils.setField(currentMember, "id", 1L);
		Board board = Board.builder().name("Test Board").build();
		ReflectionTestUtils.setField(board, "id", 1L);
		Post post = Post.builder()
			.board(board)
			.member(writer)
			.title("Title")
			.content("Content")
			.build();
		ReflectionTestUtils.setField(post, "id", postId);
		when(postRepository.findById(postId)).thenReturn(Optional.of(post));
		when(securityUtil.getCurrentMember()).thenReturn(currentMember);

		// Act & Assert
		assertThatThrownBy(() -> postService.deletePost(postId))
			.isInstanceOf(BusinessException.class)
			.hasMessageContaining(BoardErrorCode.NOT_WRITER.getMessage());
	}
}
