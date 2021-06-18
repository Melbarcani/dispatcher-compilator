package fr.esgi.dispatcher.code.model;

public class CodeRequest {
    public String getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }

    public String getExerciseTitle() {
        return exerciseTitle;
    }

    private String userId;
    private String code;
    private String exerciseTitle;

    public CodeRequest(String userId, String code, String exerciseTitle) {
        this.userId = userId;
        this.code = code;
        this.exerciseTitle = exerciseTitle;
    }
}
