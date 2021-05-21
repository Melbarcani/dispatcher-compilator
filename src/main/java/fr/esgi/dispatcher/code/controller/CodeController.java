package fr.esgi.dispatcher.code.controller;

import fr.esgi.dispatcher.code.service.FileService;
import fr.esgi.dispatcher.code.service.JavaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compiler")
public class CodeController {

    private static final String MAIN = "Main";
    private static final String JAVA_EXTENSION = ".java";

    private final JavaService javaService;
    private final FileService fileService;

    public CodeController(JavaService javaService, FileService fileService) {
        this.javaService = javaService;
        this.fileService = fileService;
    }

    @PostMapping("/java")
    public ResponseEntity<String> getCode(@RequestBody String code) {
        fileService.createFile(code, MAIN + JAVA_EXTENSION);
        var result = javaService.compileCode();
        fileService.deleteFile(JAVA_EXTENSION);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
