package fr.esgi.dispatcher.code.service;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class JavaService extends AbstractProgramingLanguageService{
    private static final String CONTAINER_TAG = "demo/oracle-java:8";
    private static final String COMPILE_JAVA_CODE_COMMAND = " javac Main.java";
    private static final String EXECUTE_JAVA_MAIN_COMMAND = " java Main";

    public String compileCode() {
        try{
            var process = executeDockerCommand(WORKDIR + " " + CONTAINER_TAG + COMPILE_JAVA_CODE_COMMAND);
            String output = getResult(process.getErrorStream());

            if (output != null) return output;
            return executeCode(process);

        } catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            return FAILED + " " + e.getMessage();
        }
    }

    private String executeCode(Process process) throws InterruptedException {
        if(process.waitFor()==0){
            return executeCode();
        }
        return FAILED;
    }

    public String executeCode(){
        return executeCode(CONTAINER_TAG, EXECUTE_JAVA_MAIN_COMMAND);
    }
    protected Process executeDockerCommand(String command) throws IOException {
        String currentPath = new File(".").getCanonicalPath();
        return Runtime.getRuntime().exec(DOCKER_RUN_COMMAND + currentPath + command);
    }
}

