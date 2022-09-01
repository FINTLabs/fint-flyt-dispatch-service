package no.fintlabs;

import lombok.Getter;
import no.fintlabs.model.Status;

public class CaseDispatchingException extends RuntimeException {

    @Getter
    private final Status status;

    public CaseDispatchingException(Status status) {
        super("Dispatch failed with status='" + status + "'");
        this.status = status;
    }

}
