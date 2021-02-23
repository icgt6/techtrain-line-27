package net.ninebolt.exception;

public class InvalidFormatException extends Exception {

    private static final long serialVersionUID = 381920653909890655L;

    public InvalidFormatException(String errorMsg) {
        super(errorMsg);
    }
}
