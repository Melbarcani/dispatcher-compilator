package fr.esgi.dispatcher.code.controller;

import fr.esgi.dispatcher.code.model.CodeRequest;
import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.service.FileService;
import fr.esgi.dispatcher.code.service.JavaService;
import fr.esgi.dispatcher.code.service.PythonService;
import fr.esgi.dispatcher.code.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compiler")
@RequiredArgsConstructor
public class
CodeController {

    private static final String JAVA_EXTENSION = ".java";
    private static final String PYTHON_EXTENSION = ".py";

    private final JavaService javaService;
    private final FileService fileService;
    private final PythonService pythonService;
    private final SecurityService securityService;

    @PostMapping("/Java")
    public ResponseEntity<CodeResult> compileJava(@RequestBody CodeRequest codeRequest) {
        String fileName = codeRequest.getExerciseTitle();
        CodeResult maliciousResult = securityService.checkJavaMaliciousCode(codeRequest.getCode());
        if(maliciousResult != null){
            return new ResponseEntity<>(maliciousResult, HttpStatus.OK);
        }
        fileService.createFile(codeRequest.getCode(), fileName + JAVA_EXTENSION, codeRequest.getUserId());
        var result = javaService.compileCode(fileName, codeRequest.getUserId());
        fileService.deleteFile(fileName + JAVA_EXTENSION, codeRequest.getUserId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/Python")
    public ResponseEntity<CodeResult> compilePython(@RequestBody CodeRequest codeRequest) {
        CodeResult maliciousResult = securityService.checkPythonMaliciousCode(codeRequest.getCode());
        if(maliciousResult != null){
            return new ResponseEntity<>(maliciousResult, HttpStatus.OK);
        }
        fileService.createFile(codeRequest.getCode(), codeRequest.getExerciseTitle() + PYTHON_EXTENSION, codeRequest.getUserId());
        var result = pythonService.executeCode(codeRequest.getExerciseTitle(), codeRequest.getUserId(), codeRequest.getCode().lines().count());
        fileService.deleteFile(codeRequest.getExerciseTitle() + codeRequest.getUserId() + PYTHON_EXTENSION, codeRequest.getUserId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
