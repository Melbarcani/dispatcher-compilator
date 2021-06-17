package fr.esgi.dispatcher.code.controller;

import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.service.FileService;
import fr.esgi.dispatcher.code.service.JavaService;
import fr.esgi.dispatcher.code.service.PythonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compiler")
public class
CodeController {

    private static final String MAIN = "Main";
    private static final String JAVA_EXTENSION = ".java";
    private static final String PYTHON_EXTENSION = ".py";

    private final JavaService javaService;
    private final FileService fileService;
    private final PythonService pythonService;

    public CodeController(JavaService javaService, FileService fileService, PythonService pythonService) {
        this.javaService = javaService;
        this.fileService = fileService;
        this.pythonService = pythonService;
    }

    @PostMapping("/java")
    public ResponseEntity<CodeResult> compileJava(@RequestBody String code) {

        fileService.createFile(code, MAIN + JAVA_EXTENSION);
        var result = javaService.compileCode();
        fileService.deleteFile(MAIN + JAVA_EXTENSION);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/python")
    public ResponseEntity<CodeResult> compilePython(@RequestBody String code) {
        fileService.createFile(code, MAIN + PYTHON_EXTENSION);
        var result = pythonService.executeCode();
        fileService.deleteFile(MAIN + PYTHON_EXTENSION);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
