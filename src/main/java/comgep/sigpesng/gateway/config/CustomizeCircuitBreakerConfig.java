package comgep.sigpesng.gateway.config;

/* Source: https://programmer.group/619afb7baa9ef.html
 * 
 */

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;

@Configuration
public class CustomizeCircuitBreakerConfig {
	
	@Value("${resilience4j.timelimiter.configs.default.timeout-duration}")
	private int timeLimit;

    @Bean
    public ReactiveResilience4JCircuitBreakerFactory defaultCustomizer() {
        ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory(cbRegistry(), tlRegistry());
        return factory;
    }
    
    private CircuitBreakerRegistry cbRegistry() {
    	// @formatter:off
    	CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom() //
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED) // The type of sliding window is time window
                .slidingWindowSize(10) // The size of the time window is 60 seconds
                .minimumNumberOfCalls(5) // At least 5 calls are required in the unit time window to start statistical calculation
                .failureRateThreshold(50) // When the call failure rate reaches 50% within the unit time window, the circuit breaker will be started
                .enableAutomaticTransitionFromOpenToHalfOpen() // The circuit breaker is allowed to automatically change from open state to half open state
                .permittedNumberOfCallsInHalfOpenState(5) // The number of normal calls allowed in the half open state
                .waitDurationInOpenState(Duration.ofSeconds(5)) // It takes 60 seconds for the circuit breaker to change from open state to half open state
                .recordExceptions(Throwable.class) // All exceptions are treated as failures
                .build();
    	return CircuitBreakerRegistry.of(circuitBreakerConfig);
    	// @formatter:on
    }
    
    private TimeLimiterRegistry tlRegistry() {

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
        		.timeoutDuration(Duration.ofSeconds(timeLimit))
        		.build();
        
        return TimeLimiterRegistry.of(timeLimiterConfig);
    }
}