package ru.alexefremov.depositapp.depositservice.exception;

public class BusinessLayerException extends RuntimeException {
    public BusinessLayerException(String message) {
        super(message);
    }
}
