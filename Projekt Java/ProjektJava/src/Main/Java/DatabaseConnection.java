import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Singleton - jedyna instancja klasy DatabaseConnection
    private static DatabaseConnection instance;

    // Połączenie z bazą danych
    private Connection connection;

    // URL do bazy danych SQLite
    private static final String DATABASE_URL = "jdbc:sqlite:Students_DB.db";

    // Prywatny konstruktor, aby uniemożliwić tworzenie instancji z innych klas
    private DatabaseConnection() throws DatabaseConnectionException {
        try {
            // Nawiązywanie połączenia z bazą danych
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            // Rzucenie własnego wyjątku w przypadku błędu połączenia
            throw new DatabaseConnectionException("Błąd połączenia z bazą danych", e);
        }
    }

    // Publiczna metoda, która zwraca jedyną instancję klasy DatabaseConnection (wzorzec Singleton)
    public static synchronized DatabaseConnection getInstance() throws DatabaseConnectionException {
        if (instance == null) {
            // Jeśli instancja jeszcze nie istnieje, tworzymy ją
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Metoda zwracająca aktualne połączenie z bazą danych
    public Connection getConnection() throws DatabaseConnectionException {
        try {
            // Sprawdzamy, czy połączenie jest null lub zamknięte, jeśli tak, nawiązujemy nowe połączenie
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DATABASE_URL);
            }
        } catch (SQLException e) {
            // Rzucenie własnego wyjątku w przypadku błędu przy uzyskiwaniu połączenia
            throw new DatabaseConnectionException("Błąd przy uzyskaniu połączenia", e);
        }
        return connection;
    }

    // Metoda do zamknięcia połączenia z bazą danych
    public void closeConnection() throws DatabaseConnectionException {
        if (connection != null) {
            try {
                // Tylko zamykamy połączenie, jeśli jest otwarte
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Rzucenie własnego wyjątku, jeśli zamknięcie połączenia nie powiodło się
                throw new DatabaseConnectionException("Błąd przy zamykaniu połączenia z bazą danych", e);
            }
        }
    }
}
