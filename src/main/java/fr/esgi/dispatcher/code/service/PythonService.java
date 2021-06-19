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

    public CodeResult executeCode(String fileName, String folderName) {
        return executeCode(CONTAINER_TAG, EXECUTE_PYTHON_MAIN_COMMAND + fileName + PYTHON_EXTENSION, folderName);
    }

    protected CodeResult executeCode(String containerTag, String file, String folderName) {
        try {
            var process = executeDockerCommand(WORKDIR + " " + containerTag + file, folderName);
            CodeResult output = new CodeResult(getResult(process.getInputStream()), STATUS.SUCCESS);
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
