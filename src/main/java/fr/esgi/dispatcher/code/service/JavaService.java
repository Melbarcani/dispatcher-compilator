package fr.esgi.dispatcher.code.service;

import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.model.STATUS;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class JavaService extends AbstractProgramingLanguageService {

    private static final String CONTAINER_TAG = "demo/oracle-java:8";
    private static final String COMPILE_JAVA_CODE_COMMAND = " javac ";
    private static final String EXECUTE_JAVA_MAIN_COMMAND = " java ";
    private static final String JAVA_EXTENSION = ".java";

    public CodeResult compileCode(String fileName, String folderName) {
        try {
            var process = executeDockerCommand(WORKDIR + " " + CONTAINER_TAG + COMPILE_JAVA_CODE_COMMAND + fileName + JAVA_EXTENSION, folderName);
            var output = new CodeResult(getResult(process.getErrorStream()), STATUS.ERROR);

            if (output.getOutputConsole() != null) return output;
            return executeCode(process, fileName, folderName);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CodeResult(FAILED + " " + e.getMessage(), STATUS.UNCOMPILED);
        }
    }

    public long computeByteCodeLines(String folderName, String fileName) {
        try {
            Process process = executeDockerCommand(WORKDIR + " " + CONTAINER_TAG + " javap -c " + fileName + "$ChallengeIntern", folderName);
            return getResult(process.getInputStream()).lines().count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private CodeResult executeCode(Process process, String fileName, String folderName) throws InterruptedException {
        if (process.waitFor() == 0) {
            return executeCode(fileName, folderName);
        }
        return new CodeResult("", STATUS.ERROR);
    }

    public CodeResult executeCode(String fileName, String folderName) {
        return executeCode(CONTAINER_TAG, EXECUTE_JAVA_MAIN_COMMAND + fileName, folderName);
    }

    @Override
    protected Process executeDockerCommand(String command, String folderName) throws IOException {
        String currentPath = new File("./" + folderName).getCanonicalPath();
        return Runtime.getRuntime().exec(DOCKER_RUN_COMMAND + currentPath + command);
    }
}

