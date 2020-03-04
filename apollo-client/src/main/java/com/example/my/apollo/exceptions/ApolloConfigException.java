package com.example.my.apollo.exceptions;

/**
 * ApolloConfigException
 */
public class ApolloConfigException extends RuntimeException{

    public ApolloConfigException(String message) {
        super(message);
      }
    
      public ApolloConfigException(String message, Throwable cause) {
        super(message, cause);
      }
}