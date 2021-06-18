package fr.esgi.dispatcher.code.controller;

import fr.esgi.dispatcher.code.model.CodeRequest;
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

    @PostMapping("/Java")
    public ResponseEntity<CodeResult> compileJava(@RequestBody CodeRequest codeRequest) {
        String fileName = codeRequest.getExerciseTitle();
        fileService.createFile(codeRequest.getCode(), fileName + JAVA_EXTENSION, codeRequest.getUserId());
        var result = javaService.compileCode(fileName, codeRequest.getUserId());
        fileService.deleteFile(fileName + JAVA_EXTENSION, codeRequest.getUserId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/Python")
    public ResponseEntity<CodeResult> compilePython(@RequestBody CodeRequest codeRequest) {
        fileService.createFile(codeRequest.getCode(), codeRequest.getExerciseTitle() + codeRequest.getUserId() + PYTHON_EXTENSION, codeRequest.getUserId());
        var result = pythonService.executeCode("fileName", "folderName");
        fileService.deleteFile(codeRequest.getExerciseTitle() + codeRequest.getUserId() + PYTHON_EXTENSION, codeRequest.getUserId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
