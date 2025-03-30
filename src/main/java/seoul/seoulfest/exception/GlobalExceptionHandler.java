package seoul.seoulfest.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import seoul.seoulfest.util.response.Response;
import seoul.seoulfest.util.response.error_code.GeneralErrorCode;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Response<Void>> handleCustomException(BusinessException ex) {
		Response<Void> response = Response.errorResponse(ex.getErrorCode());

		return response.toResponseEntity();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response<Void>> handleValidException(MethodArgumentNotValidException ex) {

		Response<Void> response = Response.errorResponse(GeneralErrorCode.INVALID_INPUT_VALUE);

		return response.toResponseEntity();
	}
}
