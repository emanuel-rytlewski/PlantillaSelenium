package customerros;

public class PageObjectLoadingError extends Error {

  public PageObjectLoadingError(String message, Throwable cause) {
        super(message, cause);
    }

  public PageObjectLoadingError(String message) { super(message);
  }
}
