package fr.esgi.dispatcher.code.service;

public class Backup {
    /*package fr.esgi.dispatcher.code.service;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class JavaService {

    public String compileCode() {
        try {
            var process = executeDockerCommand(":/app -w /app demo/oracle-java:8 javac Main.java");
            String output = buildResult(process);
            if (output != null) return output;

            if (process.waitFor() == 0) {
                return executeCompiledCode();
            }
            return "failed";

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return e.getMessage();
        }
    }

    private String executeCompiledCode() {
        try {
            var process = executeDockerCommand(":/app -w /app demo/oracle-java:8 java Main > logs.log");
            return getResult(process);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return e.getMessage();
        }
    }

    private Process executeDockerCommand(String command) throws IOException {
        String currentPath = new File(".").getCanonicalPath();

        return Runtime.getRuntime()
                .exec(
                        "sudo docker run --rm -v "
                                + currentPath
                                + command
                );
    }

    private String buildResult(Process process) throws IOException {
        var output = new StringBuilder();

        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line)
                    .append("\n");
        }

        if (!output.toString().isEmpty()) {
            return output.toString();
        }
        return null;
    }

    private String getResult(Process process) throws IOException, InterruptedException {
        var output = new StringBuilder();

        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        int exitVal = process.waitFor();

        if (!output.toString().isEmpty()) {
            return output.toString();
        }

        if (exitVal == 0) {
            return output.toString();
        }
        return "failed";
    }
}
*/
}
