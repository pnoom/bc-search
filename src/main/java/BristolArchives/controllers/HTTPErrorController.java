package BristolArchives.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;


@Controller
public class HTTPErrorController implements ErrorController {
    private static final String PATH = "/error";

    @GetMapping(value = PATH)
    public String handleError(HttpServletRequest request, ModelMap model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorCode;
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                errorCode = "error-404";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorCode = "error-500";
            }
            else
                errorCode = "Something went wrong";
        }
        else
            errorCode = "Nothing went wrong";
        model.addAttribute("errorMessage", errorCode);
        return "errorPage";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
