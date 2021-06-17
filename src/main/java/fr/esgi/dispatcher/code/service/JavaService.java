package fr.esgi.dispatcher.code.service;

import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.model.STATUS;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class JavaService extends AbstractProgramingLanguageService{

    private static final String CONTAINER_TAG = "demo/oracle-java:8";
    private static final String COMPILE_JAVA_CODE_COMMAND = " javac Main.java";
    private static final String EXECUTE_JAVA_MAIN_COMMAND = " java Main";

    public CodeResult compileCode() {
        try{
            var process = executeDockerCommand(WORKDIR + " " + CONTAINER_TAG + COMPILE_JAVA_CODE_COMMAND);
            var output = new CodeResult(getResult(process.getErrorStream()), STATUS.ERROR);

            if (output.getOutputConsole() != null) return output;
            return executeCode(process);

        } catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            return new CodeResult(FAILED + " " + e.getMessage(), STATUS.SUCCESS);
        }
    }

    private CodeResult executeCode(Process process) throws InterruptedException {
        if(process.waitFor()==0){
            return executeCode();
        }
        return new CodeResult("", STATUS.ERROR);
    }

    public CodeResult executeCode(){
        return executeCode(CONTAINER_TAG, EXECUTE_JAVA_MAIN_COMMAND);
    }

    @Override
    protected Process executeDockerCommand(String command) throws IOException {
        String currentPath = new File(".").getCanonicalPath();
        return Runtime.getRuntime().exec(DOCKER_RUN_COMMAND + currentPath + command);
    }
}

