package fr.esgi.dispatcher.code.service;

import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.model.STATUS;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Collections;

import static fr.esgi.dispatcher.code.service.AbstractProgramingLanguageService.FAILED;

@Service
public class PythonService {
    protected static final String WORKDIR = ":/app -w /app";
    protected static final String DOCKER_RUN_COMMAND = "docker run --rm -v ";
    private static final String CONTAINER_TAG = "python";
    private static final String EXECUTE_PYTHON_MAIN_COMMAND = " python3 ";
    private static final String PYTHON_EXTENSION = ".py";
    private static final String REGEX_LINES = "&&&lines&&& ";
    private static final String REGEX_COUNT = " &&&count&&&";


    public CodeResult executeCode(String file, String folderName) {
        var fileName = EXECUTE_PYTHON_MAIN_COMMAND + file + PYTHON_EXTENSION;
        try {
            String currentPath = new File("./" + folderName).getCanonicalPath();
            Runtime.getRuntime().exec("autopep8 -i " + currentPath + "/" + file + ".py");
            var process = executeDockerCommand(WORKDIR + " " + CONTAINER_TAG + fileName, folderName);
            var output = new CodeResult(getResult(process.getErrorStream()), STATUS.ERROR, Collections.emptyList());
            if (output.getOutputConsole() != null) return output;
            var consoleOutput = getResult(process.getInputStream());
            output = createResult(consoleOutput);
            if (output.getOutputConsole() != null) return output;

            return new CodeResult("", STATUS.SUCCESS,Collections.emptyList());
        } catch (
                IOException e) {
            return new CodeResult(FAILED, STATUS.UNCOMPILED,Collections.emptyList());
        }
    }

    private CodeResult createResult(String consoleOutput) {
        var instructionsCount = 0;
        if (consoleOutput.contains(REGEX_COUNT) && consoleOutput.contains(REGEX_LINES)) {
            var counterString = consoleOutput.substring(consoleOutput.indexOf(REGEX_LINES) + 12, consoleOutput.indexOf(REGEX_COUNT));
            try {
                instructionsCount = Integer.parseInt(counterString);
            } catch (NumberFormatException e) {
                instructionsCount = 0;
            }
        }
        if (consoleOutput.contains(REGEX_COUNT)) {
            String[] parts = consoleOutput.split(REGEX_COUNT);
            return new CodeResult(parts[1], STATUS.SUCCESS, instructionsCount, Collections.emptyList());
        }
        return new CodeResult(consoleOutput, STATUS.SUCCESS, instructionsCount, Collections.emptyList());
    }

    protected Process executeDockerCommand(String command, String folderName) throws IOException {
        String currentPath = new File("./" + folderName).getCanonicalPath();
        return Runtime.getRuntime().exec(DOCKER_RUN_COMMAND + currentPath + command);
    }

    protected String getResult(InputStream in) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(in));
        var output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        if (!output.toString().isEmpty()) {
            return output.toString();
        }
        return null;
    }
}
