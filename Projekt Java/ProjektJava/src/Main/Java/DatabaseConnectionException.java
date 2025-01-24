public class DatabaseConnectionException extends Exception {
    // Konstruktor klasy wyjątku DatabaseConnectionException
    // Przyjmuje dwa parametry: komunikat o błędzie oraz przyczynę błędu
    public DatabaseConnectionException(String message, Throwable cause) {
        // Wywołanie konstruktora klasy nadrzędnej Exception, przekazując komunikat i przyczynę błędu
        super(message, cause);
    }
}