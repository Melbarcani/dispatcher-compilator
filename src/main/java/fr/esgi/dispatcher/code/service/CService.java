package fr.esgi.dispatcher.code.service;

import fr.esgi.dispatcher.code.model.CodeResult;
import fr.esgi.dispatcher.code.model.STATUS;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CService extends AbstractProgramingLanguageService {

    private static final String CONTAINER_TAG = "gcc:49";
    private static final String COMPILE_GCC_CODE_COMMAND = " gcc -o ";
    private static final String EXECUTE_C_PROGRAM_MAIN_COMMAND = " ./";
    private static final String C_EXTENSION = ".c";

    public CodeResult compileCode(String fileName, String folderName) {
        try {
            var process = executeDockerCommand(WORKDIR + " " + CONTAINER_TAG + COMPILE_GCC_CODE_COMMAND + fileName + " "+fileName + C_EXTENSION, folderName);
            var output = new CodeResult(getResult(process.getErrorStream()), STATUS.ERROR);

            if (output.getOutputConsole() != null) return output;
            return executeCode(process, fileName, folderName);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CodeResult(FAILED + " " + e.getMessage(), STATUS.UNCOMPILED);
        }
    }

    public long computeByteCodeLines(String folderName, String fileName) {
        try{
            String currentPath = new File(folderName).getCanonicalPath();
            try(var s = Files.lines(Path.of(currentPath+ fileName.substring(2)+".c"), StandardCharsets.UTF_8);) {
                return s.count();
            }
        } catch (IOException e){
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
        return executeCode(CONTAINER_TAG, EXECUTE_C_PROGRAM_MAIN_COMMAND + fileName, folderName);
    }

    @Override
    protected Process executeDockerCommand(String command, String folderName) throws IOException {
        String currentPath = new File("./" + folderName).getCanonicalPath();
        return Runtime.getRuntime().exec(DOCKER_RUN_COMMAND + currentPath + command);
    }
}
