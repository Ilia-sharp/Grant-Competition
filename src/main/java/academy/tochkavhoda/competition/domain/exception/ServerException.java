package academy.tochkavhoda.competition.domain.exception;

public class ServerException extends Exception {
    private static final long serialVersionUID = 1L;

    private final ServerErrorCode errorCode;

    public ServerException(ServerErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessageTemplate(), args));
        this.errorCode = errorCode;
    }

    public ServerErrorCode getErrorCode() {
        return errorCode;
    }
}
