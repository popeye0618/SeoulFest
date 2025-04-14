package seoul.seoulfest.aws.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import seoul.seoulfest.aws.service.S3Service;
import seoul.seoulfest.board.dto.media.response.PresignedUrlResponse;
import seoul.seoulfest.util.response.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/user")
public class S3Controller {

	private final S3Service s3Service;

	@GetMapping("/post/presigned")
	public ResponseEntity<Response<PresignedUrlResponse>> generatePostPresignedUrl(
		@RequestParam String originalFileName,
		@RequestParam String contentType) {

		PresignedUrlResponse presignedUrlResponse = s3Service.generatePostMediaPresignedUrl(originalFileName, contentType);

		return Response.ok(presignedUrlResponse).toResponseEntity();
	}

	@GetMapping("/chat/presigned")
	public ResponseEntity<Response<PresignedUrlResponse>> generateChatPresignedUrl(
		@RequestParam String originalFileName,
		@RequestParam String contentType) {

		PresignedUrlResponse presignedUrlResponse = s3Service.generateChatMediaPresignedUrl(originalFileName, contentType);

		return Response.ok(presignedUrlResponse).toResponseEntity();
	}
}
