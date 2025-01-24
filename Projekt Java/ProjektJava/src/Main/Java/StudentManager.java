import java.util.List;

// Interfejs zarządzający operacjami na studencie w bazie danych
public interface StudentManager {

    // Dodaje studenta do bazy danych
    void addStudent(Student student) throws DatabaseConnectionException;

    // Usuwa studenta z bazy danych na podstawie jego ID
    void removeStudent(String studentID) throws DatabaseConnectionException;

    // Aktualizuje dane studenta w bazie danych
    void updateStudent(String studentID, String name, int age, double grade) throws DatabaseConnectionException;

    // Zwraca listę wszystkich studentów zapisanych w bazie danych
    List<Student> displayAllStudents() throws DatabaseConnectionException;

    // Oblicza średnią ocenę wszystkich studentów
    double calculateAverageGrade() throws DatabaseConnectionException;

    // Sprawdza, czy dany ID studenta już istnieje w bazie danych
    boolean isStudentIDExist(String studentID) throws DatabaseConnectionException;

    // Zwraca obiekt studenta na podstawie jego ID
    Student getStudentByID(String studentID) throws DatabaseConnectionException;

    // Inicjalizuje połączenie z bazą danych
    void initializeDatabase() throws DatabaseConnectionException;
}
