package fr.esgi.dispatcher.code.service;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class JavaService {

    private static final String DOCKER_RUN_COMMAND = "sudo docker run --rm -v ";
    private static final String WORKDIR = ":/app -w /app";
    private static final String CONTAINER_TAG = "demo/oracle-java:8";
    private static final String COMPILE_JAVA_CODE_COMMAND = " javac Main.java";
    private static final String EXECUTE_JAVA_MAIN_COMMAND = " java Main";
    private static final String FAILED = "failed";

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

    private Process executeDockerCommand(String Command) throws IOException {
        String currentPath = new File(".").getCanonicalPath();
        return Runtime.getRuntime().exec(DOCKER_RUN_COMMAND + currentPath + Command);
    }

    private String getResult(InputStream in) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(in));
        var output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null){
            output.append(line).append("\n");
        }
        if(!output.toString().isEmpty()){
            return output.toString();
        }
        return null;
    }

    private String executeCode(){
        try{
            var process = executeDockerCommand(WORKDIR + " " + CONTAINER_TAG + EXECUTE_JAVA_MAIN_COMMAND);
            String output = getResult(process.getInputStream());
            if (output != null) return output;

            output = getResult(process.getErrorStream());
            if (output != null) return output;
            return "success";

        } catch (IOException e){
            return FAILED + " " + e.getMessage();
        }
    }
}
