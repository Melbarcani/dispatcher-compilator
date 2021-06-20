package fr.esgi.dispatcher.code.model;

import lombok.Data;

@Data
public class CodeResult {
    private String outputConsole;
    private STATUS status;
    private String userId;
    private long instructionsCount;

    public CodeResult(String outputConsole, STATUS status, long instructionsCount) {
        this.outputConsole = outputConsole;
        this.status = status;
        this.userId = null;
        this.instructionsCount = instructionsCount;
    }

    public CodeResult(String outputConsole, STATUS status) {
        this.outputConsole = outputConsole;
        this.status = status;
        userId = null;
    }
}
