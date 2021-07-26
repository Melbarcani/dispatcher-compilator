package fr.esgi.dispatcher.code.model;

import lombok.Data;

import java.util.List;

@Data
public class CodeResult {
    private String outputConsole;
    private STATUS status;
    private String userId;
    private long instructionsCount;
    private List<String> rulesViolationList;

    public CodeResult(String outputConsole, STATUS status, long instructionsCount, List<String> rulesViolationList) {
        this.outputConsole = outputConsole;
        this.status = status;
        this.userId = null;
        this.instructionsCount = instructionsCount;
        this.rulesViolationList = rulesViolationList;
    }

    public CodeResult(String outputConsole, STATUS status, List<String> rulesViolationList) {
        this.outputConsole = outputConsole;
        this.status = status;
        this.rulesViolationList = rulesViolationList;
        userId = null;
    }
}
