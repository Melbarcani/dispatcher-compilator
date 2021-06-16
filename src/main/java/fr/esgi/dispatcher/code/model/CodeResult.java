package fr.esgi.dispatcher.code.model;

import lombok.Data;

@Data
public class CodeResult {
    private String outputConsole;
    private STATUS status;
    private String userId;

    public CodeResult(String outputConsole, STATUS status) {
        this.outputConsole = outputConsole;
        this.status = status;
        userId = null;
    }
}
