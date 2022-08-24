package comgep.sigpesng.gateway.config;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import comgep.sigpesng.gateway.filter.AuthenticationFilter;

@Configuration
public class GatewayConfig {

	@Autowired
	private AuthenticationFilter authenticationFilter;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Value("${check.key}")
	private String gkey;

	@Value("${check.gateway}")
	private String gcode;
	
	private String ip = "0.0.0.0";

	/*
	 * In order to services work properly, was necessary to rewrite the paths after
	 * matching routes, because mainly of the actuator/refresh, which doesn´t carry
	 * the service predicate (sgpng-**) in URI. The discovery.locator.enable = true
	 * was tested, but was giving some trouble with authentication filter, that is,
	 * for some yet unknown reason, didn´t find route after filter. So adding the
	 * rewritePath on filter, solved the problem.
	 * 
	 * regex from:
	 * https://stackoverflow.com/questions/64967797/spring-cloud-gateway-rewrite-
	 * path-syntax
	 **/

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			//nothing;
		}
		// @formatter:off
        return builder.routes()
                .route("domain-service", r -> r.path("/sgpng-domain/**")
                        .filters(f -> f
                        		.filter(authenticationFilter)
                        		.rewritePath("/sgpng-domain/(?<segment>.*)","/$\\{segment}")
                        		.addRequestHeader(gkey, passwordEncoder.encode(gcode))
                        		.addRequestHeader("ipg",ip)
                        		.circuitBreaker(c -> c
                        				.setName("myCircuitBreaker")
                        				.setFallbackUri("forward:/fallback"))
                        		)
                        .uri("lb://sgpng-domain"))

                .route("core-service", r -> r.path("/sgpng-core/**")
                        .filters(f -> f
                        		.filter(authenticationFilter)
                        		.rewritePath("/sgpng-core/(?<segment>.*)","/$\\{segment}")
                        		.addRequestHeader(gkey, passwordEncoder.encode(gcode))
                        		.addRequestHeader("ipg",ip)
                        		.circuitBreaker(c -> c
                        				.setName("myCircuitBreaker")
                        				.setFallbackUri("forward:/fallback"))
                        		)
                        .uri("lb://sgpng-core"))
                
                .route("populate-service", r -> r.path("/sgpng-db-populate/**")
                        .filters(f -> f
                        		.rewritePath("/sgpng-db-populate/(?<segment>.*)","/$\\{segment}")
                        		.addRequestHeader(gkey, passwordEncoder.encode(gcode))
                        		.addRequestHeader("ipg",ip)
                        		.circuitBreaker(c -> c
                        				.setName("myCircuitBreaker")
                        				.setFallbackUri("forward:/fallback"))
                        		)
                        .uri("lb://sgpng-db-populate"))
                        
                .route("oauth-service", r -> r.path("/sgpng-oauth/**")
                        .filters(f -> f
                        		.filter(authenticationFilter)
                        		.rewritePath("/sgpng-oauth/(?<segment>.*)","/$\\{segment}")
                        		.addRequestHeader(gkey, passwordEncoder.encode(gcode))
                        		.addRequestHeader("ipG",ip)
                        		.circuitBreaker(c -> c
                        				.setName("myCircuitBreaker")
                        				.setFallbackUri("forward:/fallback"))
                        		)
                        .uri("lb://sgpng-oauth"))
                
                .build();
        // @formatter:on
	}

}
