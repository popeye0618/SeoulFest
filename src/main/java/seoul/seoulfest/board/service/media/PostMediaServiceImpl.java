package seoul.seoulfest.board.service.media;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.board.entity.Post;
import seoul.seoulfest.board.entity.PostMedia;
import seoul.seoulfest.board.repository.PostMediaRepository;

@Service
@RequiredArgsConstructor
public class PostMediaServiceImpl implements PostMediaService {

	private final PostMediaRepository postMediaRepository;
	private final S3Service s3Service;

	@Override
	@Transactional
	public void createPostMedia(Post post, List<String> keyList) {

		if (keyList != null && !keyList.isEmpty()) {
			List<PostMedia> mediaEntities = keyList.stream().map(key -> {
				PostMedia media = PostMedia.builder()
					.post(post)
					.s3Key(key)
					.build();
				post.addPostMedia(media);
				return media;
			}).toList();
			// postMediaRepository.saveAll(mediaEntities);
		}

	}

	@Override
	@Transactional
	public void updatePostMedia(Post post, List<String> keyList) {

		Set<String> currentKeys = getCurrentKeySet(post);

		// 요청으로 전달받은 새 S3Key 집합 (null이면 빈 집합)
		Set<String> newKeys = keyList != null ? new HashSet<>(keyList) : new HashSet<>();

		// 삭제 대상: DB에 있었으나 새 요청에 없는 S3Key들
		Set<String> keysToDelete = new HashSet<>(currentKeys);
		keysToDelete.removeAll(newKeys);

		// S3에서 삭제 후 DB 컬렉션에서도 제거
		processUpdate(post, keysToDelete);

		// 추가 대상: 새 요청에 있지만 DB에는 없는 S3Key들
		Set<String> keysToAdd = new HashSet<>(newKeys);
		keysToAdd.removeAll(currentKeys);

		List<String> addList = new ArrayList<>(keysToAdd);
		createPostMedia(post, addList);
	}

	private Set<String> getCurrentKeySet(Post post) {
		return post.getPostMedias().stream()
			.map(PostMedia::getS3Key)
			.collect(Collectors.toSet());
	}

	private void processUpdate(Post post, Set<String> keysToDelete) {
		post.getPostMedias().removeIf(media -> {
			if (keysToDelete.contains(media.getS3Key())) {
				s3Service.deleteObject(media.getS3Key());
				post.removePostMedia(media);
				return true;
			}
			return false;
		});
	}

	@Override
	@Transactional
	public void removePostMedia(Post post) {
		post.getPostMedias().forEach(media -> {
			s3Service.deleteObject(media.getS3Key());
			post.removePostMedia(media);
		});
		post.getPostMedias().clear();
	}
}
