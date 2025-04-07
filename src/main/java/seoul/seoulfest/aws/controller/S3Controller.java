package seoul.seoulfest.aws.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping("/presigned")
	public ResponseEntity<Response<PresignedUrlResponse>> generatePresignedUrl(
		@RequestParam String originalFileName,
		@RequestParam String contentType) {

		PresignedUrlResponse presignedUrlResponse = s3Service.generatePresignedUrl(originalFileName, contentType);

		return Response.ok(presignedUrlResponse).toResponseEntity();
	}
}
