package comgep.sigpesng.gateway.fallback;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

	@RequestMapping("/fallback")
    public Mono<ResponseEntity<String>> getFallback(ServerWebExchange exchange) {
        Throwable cause = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        Throwable rootCause = ExceptionUtils.getRootCause(cause);
        
        if(rootCause == null && cause instanceof java.util.concurrent.TimeoutException) {
            // Gateway Timeout
        	String strip = cause.getLocalizedMessage();
        	
    		String patternStr = "[0-9]+[a-z]+";
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(strip);
            if(matcher.find()){
            	strip = strip.substring(0, matcher.end());
            }

            return Mono
    				.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
    						.body("Request timed out..." + strip));
        } else {
            // Other error
        	if (cause.getLocalizedMessage().contains("503")) {
        		return Mono
        				.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        						.body(cause.getLocalizedMessage()));
        	}
        	if (cause.getLocalizedMessage().contains("Connection refused")) {
        		return Mono
        				.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        						.body("Service probaly down, but still registered on Gateway, OR..\\n in process of registering again, then wait a moment ... "));
        	}
        	return Mono
    				.just(ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
    						.body("Really don't know :-(  ...  " + cause.getLocalizedMessage()));
        }
    }
}