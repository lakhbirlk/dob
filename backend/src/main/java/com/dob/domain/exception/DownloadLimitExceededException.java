package com.dob.domain.exception;

public class DownloadLimitExceededException extends DomainException {
    public DownloadLimitExceededException(int limit) {
        super("Download limit exceeded. Monthly limit: " + limit);
    }
}
