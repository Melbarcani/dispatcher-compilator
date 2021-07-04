package fr.esgi.dispatcher.code.service;

import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.model.STATUS;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class PythonService {
    protected static final String WORKDIR = ":/app -w /app";
    protected static final String DOCKER_RUN_COMMAND = "sudo docker run --rm -v ";
    private static final String CONTAINER_TAG = "python";
    private static final String EXECUTE_PYTHON_MAIN_COMMAND = " python3 ";
    private static final String PYTHON_EXTENSION = ".py";
    private static final String REGEX_LINES = "&lines&";
    private static final String REGEX_COUNT = "&count&";


    public CodeResult executeCode(String file, String folderName) {
        var fileName = EXECUTE_PYTHON_MAIN_COMMAND + file + PYTHON_EXTENSION;
        try {
            var process = executeDockerCommand(WORKDIR + " " + CONTAINER_TAG + fileName, folderName);
            var consoleOutput = getResult(process.getInputStream());
            var linesCount = Integer.valueOf(consoleOutput.substring(consoleOutput.indexOf(REGEX_LINES) + REGEX_LINES.length(), consoleOutput.indexOf(REGEX_COUNT)).trim());
            var output = new CodeResult(consoleOutput.split(REGEX_LINES)[0], STATUS.SUCCESS,linesCount);
            if (output.getOutputConsole() != null) return output;

            output = new CodeResult(getResult(process.getErrorStream()), STATUS.ERROR);
            if (output.getOutputConsole() != null) return output;
            return new CodeResult("", STATUS.SUCCESS);
        } catch (
                IOException e) {
            return new CodeResult("Exception", STATUS.ERROR);
        }
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
