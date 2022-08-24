package comgep.sigpesng.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class HomeResource {

	@GetMapping
	public ResponseEntity<String> welcome() {
		return ResponseEntity.ok().body("SGPNG-GATEWAY RUNNING");
	}

}
