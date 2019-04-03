package BristolArchives.controllers;

import BristolArchives.services.DatabaseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
public class FileUploadController {

    @Autowired
    DatabaseGenerator dbGen;

    @GetMapping("/submitCsv")
    public String submitCsv(){
        return "submitCsv";
    }

    @PostMapping("/submitCsv")
    public String handleFileUpload(@RequestParam("uploaded_csv") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        dbGen.generateDatabase(convert(file), null);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    public File convert(MultipartFile file) throws IOException
    {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
