package no.fintlabs;

public enum ErrorCode {
    GENERAL_SYSTEM_ERROR,
    CASE_DISPATCH_ERROR;

    private static final String ERROR_PREFIX = "FINT_FLYT_DISPATCH_SERVICE_";

    public String getCode() {
        return ERROR_PREFIX + name();
    }

}
