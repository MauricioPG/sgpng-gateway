package comgep.sigpesng.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import comgep.sigpesng.util.jwt.JwtUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {
	@Value("${feign.key}")
	private String pkey;

	@Autowired
	private RouterValidatorFilter routerValidator;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		if (this.isAuthMissing(request)) {
			if (request.getHeaders().containsKey(pkey)) {
				return chain.filter(exchange);
			}
		}

		if (routerValidator.isSecured.test(request)) {
			if (this.isAuthMissing(request)) {
				return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
			}

			final String token = this.getAuthHeader(request).replace("Bearer ", "");

			try {
				if (jwtUtil.isExpired(token)) {
					return this.onError(exchange, "Authorization Token expired: ", HttpStatus.UNAUTHORIZED);
				}
			} catch (Exception e) {
				return this.onError(exchange, "Authorization header is invalid: " + e.getLocalizedMessage(),
						HttpStatus.UNAUTHORIZED);
			}

			try {
				this.populateRequestWithHeaders(exchange, token);
			} catch (Exception e) {
				return this.onError(exchange, "Authorization header is invalid: " + e.getLocalizedMessage(),
						HttpStatus.UNAUTHORIZED);
			}

			if (routerValidator.isAdminRequired.test(request)) {
				List<String> authorities = jwtUtil.getAuthorities(token);
				if (!checkAuthoritie(authorities, "ADMIN")) {
					return this.onError(exchange, "You don´t have enough clearance for ADMIN features...",
							HttpStatus.FORBIDDEN);
				}
			}

			if (routerValidator.isDirapRequired.test(request)) {
				List<String> authorities = jwtUtil.getAuthorities(token);
				if (!checkAuthoritie(authorities, "DIRAP")) {
					return this.onError(exchange, "You don´t have enough clearance for DIRAP features...",
							HttpStatus.FORBIDDEN);
				}
			}
		}

		return chain.filter(exchange);
	}

	/* PRIVATE */

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);

		// https://stackoverflow.com/questions/48047645/how-to-write-messages-to-http-body-in-spring-webflux-webexceptionhandlder
		DataBuffer db = new DefaultDataBufferFactory().wrap(err.getBytes(StandardCharsets.UTF_8));
		return response.writeWith(Flux.just(db));
	}

	private String getAuthHeader(ServerHttpRequest request) {
		return request.getHeaders().getOrEmpty("Authorization").get(0);
	}

	private boolean isAuthMissing(ServerHttpRequest request) {
		return !request.getHeaders().containsKey("Authorization");
	}

	private boolean checkAuthoritie(List<String> list, String reference) {
		for (String s : list) {
			if (s.contains(reference)) {
				return true;
			}
		}
		return false;
	}

	private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
		exchange.getRequest().mutate().header("userData", String.valueOf(jwtUtil.getUserCpfAndName(token))).build();
	}
}