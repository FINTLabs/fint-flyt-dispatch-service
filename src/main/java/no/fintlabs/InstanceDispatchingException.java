package no.fintlabs;

import lombok.Getter;
import no.fintlabs.model.Status;

public class InstanceDispatchingException extends RuntimeException {

    @Getter
    private final Status status;

    public InstanceDispatchingException(Status status) {
        super("Dispatch failed with status='" + status + "'");
        this.status = status;
    }

}
