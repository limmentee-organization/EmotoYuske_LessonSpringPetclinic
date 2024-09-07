package org.springframework.samples.petclinic.org.springframework.samples.petclinic.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class WelcomeController {
	
	@GetMapping("/")
	public String welcome() {
		// welcome.htmlを返却する
		return "welcome";
	}
}
