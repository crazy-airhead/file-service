package pro.redsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pro.redsoft.domain.FileInfo;
import pro.redsoft.storage.FileStorage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;


@Controller
public class FileUploadController {

    @Autowired
    private FileStorage fileStorage;


    @RequestMapping(value = "/")
    public String index(Model model) {

        HashMap<String,String> allFiles = new HashMap<>();

        fileStorage.getAll().forEach(file -> allFiles.put(file.getId().toString(),file.getFilename()));

        model.addAttribute("files", allFiles);
        return "uploadForm";
    }


    @RequestMapping(value = "/{fileId}")
    public @ResponseBody ResponseEntity serveFile(@PathVariable String fileId) {

        Optional<InputStream> file = fileStorage.read(fileId);

        if(file.isPresent()){
             return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filId=\""+fileId+"\"")
                    .body(file);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }

    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {


        FileInfo fileInfo= null;
        try {
            fileInfo = fileStorage.save(file.getInputStream(),file.getOriginalFilename());
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

}
