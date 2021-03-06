package fr.esgi.dispatcher.code.service;

import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.model.STATUS;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class JavaService extends AbstractProgramingLanguageService {

    private static final String CONTAINER_TAG = "java:8";
    private static final String COMPILE_JAVA_CODE_COMMAND = " javac ";
    private static final String EXECUTE_JAVA_MAIN_COMMAND = " java ";
    private static final String JAVA_EXTENSION = ".java";

    public CodeResult compileCode(String fileName, String folderName, List<String> rulesViolationList) {
        try {
            var process = executeDockerCommand(WORKDIR + " " + CONTAINER_TAG + COMPILE_JAVA_CODE_COMMAND + fileName + JAVA_EXTENSION, folderName);
            var output = new CodeResult(getResult(process.getErrorStream()), STATUS.ERROR, rulesViolationList);

            if (output.getOutputConsole() != null) return output;
            return executeCode(process, fileName, folderName, rulesViolationList);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CodeResult(FAILED + " " + e.getMessage(), STATUS.UNCOMPILED, rulesViolationList);
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

    private CodeResult executeCode(Process process, String fileName, String folderName, List<String> rulesViolationList) throws InterruptedException {
        if (process.waitFor() == 0) {
            return executeCode(fileName, folderName,rulesViolationList);
        }
        return new CodeResult("", STATUS.ERROR, rulesViolationList);
    }

    public CodeResult executeCode(String fileName, String folderName, List<String> rulesViolationList) {
        return executeCode(CONTAINER_TAG, EXECUTE_JAVA_MAIN_COMMAND + fileName, folderName, rulesViolationList);
    }

    @Override
    public CodeResult executeCode(String fileName, String folderName) {
        return null;
    }

    @Override
    protected Process executeDockerCommand(String command, String folderName) throws IOException {
        String currentPath = new File("./" + folderName).getCanonicalPath();
        return Runtime.getRuntime().exec(DOCKER_RUN_COMMAND + currentPath + command);
    }
}

