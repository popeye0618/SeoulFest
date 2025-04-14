package seoul.seoulfest.board.service.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import seoul.seoulfest.aws.service.S3ServiceImpl;
import seoul.seoulfest.board.dto.media.response.PresignedUrlResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {

	@InjectMocks
	private S3ServiceImpl s3ServiceImpl;

	@Mock
	private S3Client s3Client;

	@Mock
	private S3Presigner s3Presigner;

	@BeforeEach
	public void setUp() {
		// @BeforeEach에서 s3ServiceImpl의 필드를 설정합니다.
		ReflectionTestUtils.setField(s3ServiceImpl, "bucketName", "test-bucket");
		ReflectionTestUtils.setField(s3ServiceImpl, "region", "us-east-1");
	}

	@Test
	public void testGeneratePresignedUrl_success() throws Exception {
		String originalFileName = "photo.png";
		String contentType = "image/png";

		// 가짜 presigned URL 반환 객체 생성
		URL dummyUrl = new URL("https://dummy-url.com");
		PresignedPutObjectRequest fakePresignedRequest = mock(PresignedPutObjectRequest.class);
		when(fakePresignedRequest.url()).thenReturn(dummyUrl);

		// s3Presigner.presignPutObject()가 가짜 객체를 반환하도록 설정
		when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
			.thenReturn(fakePresignedRequest);

		PresignedUrlResponse response = s3ServiceImpl.generatePostMediaPresignedUrl(originalFileName, contentType);

		assertThat(response).isNotNull();
		// s3Key가 "seoulfest/post/media/"로 시작하고, ".png"로 끝나는지 확인
		assertThat(response.getS3Key()).matches("seoulfest/post/media/\\d{14}_.+\\.png");
		// presignedUrl이 dummyUrl와 일치하는지 확인
		assertThat(response.getPresignedUrl()).isEqualTo(dummyUrl.toString());

		verify(s3Presigner, times(1)).presignPutObject(any(PutObjectPresignRequest.class));
	}

	@Test
	public void testDeleteObject_callsS3Client() {
		String s3Key = "seoulfest/post/media/20250402123045_testkey.jpg";

		s3ServiceImpl.deleteObject(s3Key);

		// s3Client.deleteObject()가 올바른 DeleteObjectRequest로 호출되었는지 검증
		verify(s3Client, times(1)).deleteObject(argThat((DeleteObjectRequest req) ->
			req.bucket().equals("test-bucket") && req.key().equals(s3Key)
		));
	}
}
