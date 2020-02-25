package com.example.my.apollo.common.exception;

import org.springframework.http.HttpStatus;

/**
 * NotFoundException
 */
public class NotFoundException extends AbstractApolloHttpException {

    public NotFoundException(String str) {
        super(str);
        setHttpStatus(HttpStatus.NOT_FOUND);
    }

}