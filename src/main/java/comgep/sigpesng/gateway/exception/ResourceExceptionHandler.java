package comgep.sigpesng.gateway.exception;

import java.net.ConnectException;
import java.time.Instant;

import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ResourceExceptionHandler extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// codigo 404
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<StandardError> method (NotFoundException e) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardError err = new StandardError();
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("(G) Resource not found");
		err.setMessage(e.getMessage());
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(ConnectException.class)
	public ResponseEntity<StandardError> connection (ConnectException e) {
		HttpStatus status = HttpStatus.GONE;
		StandardError err = new StandardError();
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("(G) Service Connection Exception");
		err.setMessage(e.getMessage());
		return ResponseEntity.status(status).body(err);
	}


	// codigo 500
	@ExceptionHandler(value = RuntimeException.class)
	public ResponseEntity<StandardError> interna(MethodArgumentNotValidException e) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		StandardError err = new StandardError();
		err.setTimestamp(Instant.now());
		err.setStatus(status.value());
		err.setError("(G) Internal Server Error");
		Object[] trace = e.getStackTrace();
		Object interest = trace[0];
		err.setMessage(e.getMessage() + "\nMethod: " + interest.toString());
		return ResponseEntity.status(status).body(err);
	}

}
