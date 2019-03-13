package ru.bvn13.adastor.exceptions;

/**
 * @author boykovn at 13.03.2019
 */
public class AdastorException extends Exception {

    public AdastorException() {
    }

    public AdastorException(String message) {
        super(message);
    }

    public AdastorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdastorException(Throwable cause) {
        super(cause);
    }

    public AdastorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
