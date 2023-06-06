package no.fintlabs.model;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Status status;
    private String archiveCaseId;
    private String errorMessage;
}
