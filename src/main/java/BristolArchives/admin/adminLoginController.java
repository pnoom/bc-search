package BristolArchives.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class adminLoginController {
    @GetMapping("/login")
    public String Login() {
        return "login";
    }

}
