package seoul.seoulfest.board.service.media;

import seoul.seoulfest.board.dto.media.response.PresignedUrlResponse;

public interface S3Service {

	PresignedUrlResponse generatePresignedUrl(String objectKey, String contentType);

	void deleteObject(String s3Key);
}
