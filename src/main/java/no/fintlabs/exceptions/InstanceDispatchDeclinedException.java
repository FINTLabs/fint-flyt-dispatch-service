package no.fintlabs.exceptions;

import lombok.Getter;

@Getter
public class InstanceDispatchDeclinedException extends RuntimeException {

    private final String errorMessage;

    public InstanceDispatchDeclinedException(String errorMessage) {
        super("Dispatch failed with message='" + errorMessage + "'");
        this.errorMessage = errorMessage;
    }

}
