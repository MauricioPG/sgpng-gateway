package comgep.sigpesng.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidatorFilter {

	// @formatter:off
	public static final List<String> openApiEndpoints = 
			List.of(
					"/users/login", 
					"/oauth/token"
			);

	public static final List<String> isAdminRoutes = 
			List.of(
					"/oauth/", 
					"/actuator/"
			);

	public static final List<String> isDIRAPRoutes = 
			List.of(
					"/sgpng-core/", 
					"/sgpng-user/", 
					"/sgpng-domain/"
			);
	// @formatter:on

	public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints.stream()
			.noneMatch(uri -> request.getURI().getPath().contains(uri));

	public Predicate<ServerHttpRequest> isAdminRequired = request -> isAdminRoutes.stream()
			.anyMatch(uri -> request.getURI().getPath().contains(uri));

	public Predicate<ServerHttpRequest> isDirapRequired = request -> isDIRAPRoutes.stream()
			.anyMatch(uri -> request.getURI().getPath().contains(uri));

}