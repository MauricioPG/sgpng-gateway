package comgep.sigpesng.gateway.config;

import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import comgep.sigpesng.gateway.exception.ResourceExceptionHandler;
import comgep.sigpesng.util.jwt.JwtKeyProperties;
import comgep.sigpesng.util.jwt.JwtUtil;
import comgep.sigpesng.util.jwt.ReadPublicKey;

@Configuration
public class GatewayAppConfig {
	
	@Bean
	public JwtKeyProperties jwtKeyProperties() {
		return new JwtKeyProperties();
	}
	
	@Bean
	public JwtUtil jwtUtil() {
		return new JwtUtil();
	}
	
    // JWT
    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
    	// reading a local key on resources
    	// RSAPublicKey rkey = (RSAPublicKey) ReadPublicKey.getKeyFromResource("sgpngkey.pem");
    	
    	// reading from config server
    	RSAPublicKey rkey = (RSAPublicKey) ReadPublicKey.getKeyFromConfig(jwtKeyProperties().getKey());
        return NimbusJwtDecoder.withPublicKey(rkey).build();
    }
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public ResourceExceptionHandler resourceExceptionHandler() {
		return new ResourceExceptionHandler();
	}

}
