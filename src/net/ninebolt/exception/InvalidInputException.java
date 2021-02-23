package net.ninebolt.exception;

public class InvalidInputException extends Exception{

    private static final long serialVersionUID = 2263294310945111701L;

    public InvalidInputException(String errorMsg) {
        super(errorMsg);
    }
}
