package ru.spliterash.pcmasterclient.api;

import lombok.Data;


@Data
public class MethodExecuteException extends Exception {
    public MethodExecuteException(ExceptionCode code, String error, String... other) {
        super(error);
        this.code = code;
        this.error = error;
        this.other = other;
    }


    private ExceptionCode code;
    private String error;
    private String[] other;

    public enum  ExceptionCode {
        METHOD_EXCEPTION, SERVER_ERROR
    }
}
