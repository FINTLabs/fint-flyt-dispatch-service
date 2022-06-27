package no.fintlabs.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Dispatch {

    private Status status;

    public Dispatch(Status status) {
        this.status = status;
    }

}
