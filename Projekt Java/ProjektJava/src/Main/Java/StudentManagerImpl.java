import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Implementacja interfejsu StudentManager, zarządzająca operacjami na studencie w bazie danych
public class StudentManagerImpl implements StudentManager {
    private final DatabaseConnection dbConnection;

    // Konstruktor przyjmujący połączenie z bazą danych
    public StudentManagerImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Dodaje studenta do bazy danych
    @Override
    public void addStudent(Student student) throws DatabaseConnectionException {
        try {
            // Sprawdzenie, czy student z danym ID już istnieje
            if (isStudentIDExist(student.getStudentID())) {
                throw new IllegalArgumentException("Student ID already exists: " + student.getStudentID());
            }

            String query = "INSERT INTO students (studentID, name, age, grade) VALUES (?, ?, ?, ?)";
            // Przygotowanie zapytania SQL i dodanie studenta
            try (Connection connection = dbConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, student.getStudentID());
                preparedStatement.setString(2, student.getName());
                preparedStatement.setInt(3, student.getAge());
                preparedStatement.setDouble(4, student.getGrade());

                int rowsAffected = preparedStatement.executeUpdate();

                // Sprawdzanie, czy zapytanie zakończyło się sukcesem
                if (rowsAffected == 0) {
                    throw new SQLException("Failed to add student: No rows affected.");
                }
            }
        } catch (IllegalArgumentException e) {
            throw new DatabaseConnectionException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error adding student: " + e.getMessage(), e);
        }
    }

    // Usuwa studenta z bazy danych na podstawie jego ID
    @Override
    public void removeStudent(String studentID) throws DatabaseConnectionException {
        String deleteQuery = "DELETE FROM students WHERE studentID = ?";
        // Przygotowanie zapytania SQL do usunięcia studenta
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setString(1, studentID);
            int rowsDeleted = stmt.executeUpdate();

            // Sprawdzanie, czy zapytanie zakończyło się sukcesem
            if (rowsDeleted == 0) {
                throw new IllegalArgumentException("No student found with ID: " + studentID);
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error removing student: " + e.getMessage(), e);
        }
    }

    // Aktualizuje dane studenta w bazie danych
    @Override
    public void updateStudent(String studentID, String name, int age, double grade) throws DatabaseConnectionException {
        // Pobranie studenta z danym ID
        Student student = getStudentByID(studentID);
        if (student != null) {
            String query = "UPDATE students SET name = ?, age = ?, grade = ? WHERE studentID = ?";
            // Przygotowanie zapytania SQL do aktualizacji danych studenta
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, name != null && !name.isEmpty() ? name : student.getName());
                stmt.setInt(2, age > 0 ? age : student.getAge());
                stmt.setDouble(3, (grade >= 0.0 && grade <= 100.0) ? grade : student.getGrade());
                stmt.setString(4, studentID);

                int rowsUpdated = stmt.executeUpdate();
                // Sprawdzanie, czy zapytanie zakończyło się sukcesem
                if (rowsUpdated == 0) {
                    throw new IllegalArgumentException("Student with ID: " + studentID + " not found.");
                }
            } catch (SQLException e) {
                throw new DatabaseConnectionException("Error updating student: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("Student with ID: " + studentID + " not found.");
        }
    }

    // Zwraca listę wszystkich studentów zapisanych w bazie danych
    @Override
    public List<Student> displayAllStudents() throws DatabaseConnectionException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students";

        // Przygotowanie zapytania SQL do pobrania wszystkich studentów
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Dodawanie studentów do listy
            while (rs.next()) {
                students.add(new Student(
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getDouble("grade"),
                        rs.getString("studentID")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error fetching students: " + e.getMessage(), e);
        }

        return students;
    }

    // Oblicza średnią ocenę wszystkich studentów
    @Override
    public double calculateAverageGrade() throws DatabaseConnectionException {
        String query = "SELECT AVG(grade) AS averageGrade FROM students";
        // Przygotowanie zapytania SQL do obliczenia średniej ocen
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            return rs.next() ? rs.getDouble("averageGrade") : 0.0;
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error calculating average grade: " + e.getMessage(), e);
        }
    }

    // Inicjalizuje bazę danych (tworzy tabelę, jeśli nie istnieje)
    @Override
    public void initializeDatabase() throws DatabaseConnectionException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students (" +
                "studentID TEXT PRIMARY KEY," +
                "name TEXT," +
                "age INTEGER," +
                "grade REAL)";
        // Przygotowanie zapytania SQL do stworzenia tabeli w bazie danych
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error initializing database: " + e.getMessage(), e);
        }
    }

    // Sprawdza, czy student z danym ID istnieje w bazie danych
    @Override
    public boolean isStudentIDExist(String studentID) throws DatabaseConnectionException {
        String query = "SELECT 1 FROM students WHERE studentID = ? LIMIT 1";
        // Przygotowanie zapytania SQL do sprawdzenia istnienia studenta
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error checking Student ID existence: " + e.getMessage(), e);
        }
    }

    // Zwraca studenta na podstawie jego ID
    @Override
    public Student getStudentByID(String studentID) throws DatabaseConnectionException {
        String query = "SELECT * FROM students WHERE studentID = ?";
        // Przygotowanie zapytania SQL do pobrania studenta na podstawie jego ID
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getDouble("grade"),
                        rs.getString("studentID")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error fetching student by ID: " + e.getMessage(), e);
        }
    }
}
