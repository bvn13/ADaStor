package ru.bvn13.adastor.exceptions;

/**
 * @author boykovn at 13.03.2019
 */
public class InternalServerError extends AdastorException {

    public InternalServerError() {
        super();
    }

    public InternalServerError(String message) {
        super(message);
    }

    public InternalServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerError(Throwable cause) {
        super(cause);
    }
}
