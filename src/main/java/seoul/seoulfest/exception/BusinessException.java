package seoul.seoulfest.exception;

import lombok.Builder;
import lombok.Getter;
import seoul.seoulfest.util.response.error_code.ErrorCode;

public class BusinessException extends RuntimeException {

	@Getter
	private final ErrorCode errorCode;

	@Builder
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
