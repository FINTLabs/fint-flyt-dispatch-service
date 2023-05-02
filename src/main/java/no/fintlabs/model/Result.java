package no.fintlabs.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Status status;
    private String archiveCaseId;
    private String errorMessage;
}
