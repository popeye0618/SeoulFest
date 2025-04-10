package seoul.seoulfest.parking.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import seoul.seoulfest.util.response.error_code.ErrorCode;

@Getter
public enum ParkingErrorCode implements ErrorCode {

	API_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "주차장 정보 API 호출 실패"),
	PLACE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "요청한 주차장 정보를 찾을 수 없습니다."),

	;

	private final HttpStatus httpStatus;
	private final String message;

	ParkingErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
