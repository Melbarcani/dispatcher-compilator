package fr.esgi.dispatcher.code.service;

import fr.esgi.dispatcher.code.model.CodeResult;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class PythonService extends AbstractProgramingLanguageService {
    private static final String DOCKER_RUN_COMMAND = "sudo docker run --rm -v ";
    private static final String CONTAINER_TAG = "python";
    private static final String EXECUTE_PYTHON_MAIN_COMMAND = " python3 Main.py";

    public CodeResult executeCode(String fileName, String folderName) {
        return executeCode(CONTAINER_TAG, EXECUTE_PYTHON_MAIN_COMMAND);
    }

    @Override
    protected Process executeDockerCommand(String command, String folderName) throws IOException {
        String currentPath = new File(".").getCanonicalPath();
        return Runtime.getRuntime().exec(DOCKER_RUN_COMMAND + currentPath + command);
    }
}
