package BristolArchives.admin;

import BristolArchives.services.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class adminLoginController {
    @Autowired
    private CollectionService collectionService;

    @GetMapping("/login")
    public String Login() {
        return "login";
    }

    @GetMapping("/admin")
    public String Admin(ModelMap model) {
        model.addAttribute("collectionList", collectionService.getAllCollections());
        return "adminManagement";
    }

}
