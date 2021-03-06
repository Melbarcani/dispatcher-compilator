package fr.esgi.dispatcher.code.controller;

import fr.esgi.dispatcher.code.model.CodeRequest;
import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fr.esgi.dispatcher.code.model.Extension.*;

@RestController
@RequestMapping("/api/compiler")
@RequiredArgsConstructor
public class
CodeController {

    private final JavaService javaService;
    private final FileService fileService;
    private final PythonService pythonService;
    private final SecurityService securityService;
    private final CService cService;
    private final CodeAnalyserService codeAnalyserService;

    @PostMapping("/Java")
    public ResponseEntity<CodeResult> compileJava(@RequestBody CodeRequest codeRequest) {
        String fileName = codeRequest.getExerciseTitle();
        CodeResult maliciousResult = securityService.checkJavaMaliciousCode(codeRequest.getCode());
        if(maliciousResult != null){
            return new ResponseEntity<>(maliciousResult, HttpStatus.OK);
        }
        fileService.createFile(codeRequest.getCode(), fileName + JAVA_EXTENSION, codeRequest.getUserId());
        List<String> rulesViolation = codeAnalyserService.checkBadCode(codeRequest.getUserId());
        var result = javaService.compileCode(fileName, codeRequest.getUserId(), rulesViolation);
        fileService.deleteFile(fileName + JAVA_EXTENSION, codeRequest.getUserId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/Python")
    public ResponseEntity<CodeResult> compilePython(@RequestBody CodeRequest codeRequest){
        CodeResult maliciousResult = securityService.checkPythonMaliciousCode(codeRequest.getCode());
        if(maliciousResult != null){
            return new ResponseEntity<>(maliciousResult, HttpStatus.OK);
        }

        fileService.createFile(codeRequest.getCode(), codeRequest.getExerciseTitle() + PYTHON_EXTENSION, codeRequest.getUserId());
        var result = pythonService.executeCode(codeRequest.getExerciseTitle(), codeRequest.getUserId());
        fileService.deleteFile(codeRequest.getExerciseTitle() + codeRequest.getUserId() + PYTHON_EXTENSION, codeRequest.getUserId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/C")
    public ResponseEntity<CodeResult> compileC(@RequestBody CodeRequest codeRequest){
        String fileName = codeRequest.getExerciseTitle();
        CodeResult maliciousResult = securityService.checkCMaliciousCode(codeRequest.getCode());
        if(maliciousResult != null){
            return new ResponseEntity<>(maliciousResult, HttpStatus.OK);
        }
        fileService.createFile(codeRequest.getCode(), fileName + C_EXTENSION, codeRequest.getUserId());
        var result = cService.compileCode(fileName, codeRequest.getUserId());
        fileService.deleteFile(fileName + C_EXTENSION, codeRequest.getUserId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
