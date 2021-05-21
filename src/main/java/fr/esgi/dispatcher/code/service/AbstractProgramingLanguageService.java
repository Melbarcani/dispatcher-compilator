package fr.esgi.dispatcher.code.service;

import java.io.*;

public abstract class AbstractProgramingLanguageService {

    protected static final String FAILED = "failed";
    protected static final String WORKDIR = ":/app -w /app";
    protected static final String DOCKER_RUN_COMMAND = "sudo docker run --rm -v ";

    public abstract String executeCode();

    protected String executeCode(String containerTag, String file){
        try {
            var process = executeDockerCommand(WORKDIR + " " + containerTag + file);
            String output = getResult(process.getInputStream());
            if (output != null) return output;

            output = getResult(process.getErrorStream());
            if (output != null) return output;
            return "success";
        } catch (
                IOException e) {
            return FAILED + " " + e.getMessage();
        }
    }

    protected Process executeDockerCommand(String command) throws IOException {
        String currentPath = new File(".").getCanonicalPath();
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
