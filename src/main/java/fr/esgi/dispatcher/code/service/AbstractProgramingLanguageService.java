package fr.esgi.dispatcher.code.service;

import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.model.STATUS;

import java.io.*;

public abstract class AbstractProgramingLanguageService {

    protected static final String FAILED = "failed";
    protected static final String WORKDIR = ":/app -w /app";
    protected static final String DOCKER_RUN_COMMAND = "docker run --rm -v "; // sudo is here just to test local code, must be deleted

    public abstract CodeResult executeCode(String fileName, String folderName);

    protected CodeResult executeCode(String containerTag, String file, String folderName) {
        try {
            var process = executeDockerCommand(WORKDIR + " " + containerTag + file, folderName);
            CodeResult output = new CodeResult(getResult(process.getInputStream()), STATUS.SUCCESS, computeByteCodeLines(folderName, file));
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

    protected abstract long computeByteCodeLines(String folderName, String fileName);
}
