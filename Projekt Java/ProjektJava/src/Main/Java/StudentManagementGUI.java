import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class StudentManagementGUI {
    private JFrame frame;
    private JTextField studentIDField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField gradeField;
    private JTextArea outputArea;
    private StudentManager studentManager;

    // Konstruktor, który inicjuje GUI i przyjmuje obiekt studentManager
    public StudentManagementGUI(StudentManager studentManager) {
        this.studentManager = studentManager;
        initialize();
    }

    public void initialize() {
        // Tworzenie i konfiguracja głównego okna aplikacji
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new BorderLayout());

        // Panel wejściowy dla danych studenta (ID, Imię, Wiek, Ocena)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);

        // Pola wejściowe dla danych studenta
        JLabel studentIDLabel = new JLabel("Student ID:");
        inputPanel.add(studentIDLabel);
        studentIDField = new JTextField();
        inputPanel.add(studentIDField);

        JLabel nameLabel = new JLabel("Name:");
        inputPanel.add(nameLabel);
        nameField = new JTextField();
        inputPanel.add(nameField);

        JLabel ageLabel = new JLabel("Age:");
        inputPanel.add(ageLabel);
        ageField = new JTextField();
        inputPanel.add(ageField);

        JLabel gradeLabel = new JLabel("Grade:");
        inputPanel.add(gradeLabel);
        gradeField = new JTextField();
        inputPanel.add(gradeField);

        // Panel przycisków (dodawanie, usuwanie, aktualizowanie, wyświetlanie, średnia)
        JPanel buttonPanel = new JPanel();
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton addButton = new JButton("Add Student");
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Remove Student");
        buttonPanel.add(removeButton);

        JButton updateButton = new JButton("Update Student");
        buttonPanel.add(updateButton);

        JButton displayButton = new JButton("Display Students");
        buttonPanel.add(displayButton);

        JButton averageButton = new JButton("Calculate Average Grade");
        buttonPanel.add(averageButton);

        // Panel wyjściowy do wyświetlania wyników
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(outputPanel, BorderLayout.CENTER);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);

        // Obsługa zdarzeń dla przycisku "Add Student"
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Pobranie danych z pól tekstowych i ich walidacja
                    String studentID = studentIDField.getText().trim();
                    String name = nameField.getText().trim();
                    String ageText = ageField.getText().trim();
                    String gradeInput = gradeField.getText().trim();

                    // Sprawdzenie, czy wszystkie pola zostały wypełnione
                    if (studentID.isEmpty() || name.isEmpty() || ageText.isEmpty() || gradeInput.isEmpty()) {
                        outputArea.append("All fields are required. Please fill in all the fields.\n");
                        return;
                    }

                    // Walidacja ID studenta
                    if (!studentID.matches("\\d{6}")) {
                        outputArea.append("Student ID is invalid. It must be exactly 6 digits long and contain only numbers.\n");
                        return;
                    }

                    // Walidacja imienia
                    if (!name.matches("[a-zA-Z ]+")) {
                        outputArea.append("Name is invalid. It must contain only letters. Student was not added.\n");
                        return;
                    }

                    // Walidacja wieku
                    int age = 0;
                    try {
                        age = Integer.parseInt(ageText);
                        if (age <= 0) {
                            outputArea.append("Age must be a positive number greater than 0. Please enter a valid age.\n");
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        outputArea.append("Age is invalid. Please enter a valid number for age.\n");
                        return;
                    }

                    // Walidacja oceny
                    double grade = 0.0;
                    try {
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                        symbols.setDecimalSeparator(',');
                        DecimalFormat df = new DecimalFormat();
                        df.setDecimalFormatSymbols(symbols);
                        grade = df.parse(gradeInput).doubleValue();

                        if (grade < 0.0 || grade > 100.0) {
                            outputArea.append("Grade is invalid. It must be a number between 0.0 and 100.0. Please enter a valid grade.\n");
                            return;
                        }
                    } catch (NumberFormatException | java.text.ParseException ex) {
                        outputArea.append("Grade is invalid. Please enter a valid number for grade.\n");
                        return;
                    }

                    // Sprawdzenie, czy ID studenta już istnieje
                    if (studentManager.isStudentIDExist(studentID)) {
                        outputArea.append("Student ID already exists. Please choose another one.\n");
                        return;
                    }

                    // Tworzenie obiektu studenta
                    Student student = new Student();
                    student.setStudentID(studentID);
                    student.setName(name);
                    student.setAge(age);
                    student.setGrade(grade);

                    // Dodanie studenta do listy
                    studentManager.addStudent(student);
                    outputArea.append("Student added successfully.\n");
                } catch (DatabaseConnectionException ex) {
                    outputArea.append("Database Connection Error: " + ex.getMessage() + "\n");
                }
            }
        });

        // Obsługa zdarzeń dla przycisku "Remove Student"
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Pobranie ID studenta do usunięcia
                    String studentID = studentIDField.getText().trim();

                    // Walidacja ID
                    if (studentID.length() != 6) {
                        outputArea.append("Student ID is invalid. It must be exactly 6 digits.\n");
                        return;
                    }

                    // Usunięcie studenta
                    studentManager.removeStudent(studentID);
                    outputArea.append("Student removed successfully.\n");
                } catch (IllegalArgumentException ex) {
                    outputArea.append("No student found with ID: " + studentIDField.getText() + ".\n");
                } catch (DatabaseConnectionException ex) {
                    outputArea.append("Database Connection Error: " + ex.getMessage() + "\n");
                }
            }
        });

        // Obsługa zdarzeń dla przycisku "Update Student"
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Pobranie ID studenta do aktualizacji
                    String studentID = studentIDField.getText().trim();
                    if (!studentID.matches("\\d{6}")) {
                        outputArea.append("Student ID is invalid. It must be exactly 6 digits long and contain only numbers.\n");
                        return;
                    }

                    // Pobranie studenta po ID
                    Student student = studentManager.getStudentByID(studentID);
                    if (student == null) {
                        outputArea.append("No student found with ID: " + studentID + ". Student was not updated.\n");
                        return;
                    }

                    // Aktualizacja danych studenta (jeśli są wprowadzone nowe)
                    String name = nameField.getText().trim();
                    if (!name.isEmpty() && !name.matches("[a-zA-Z ]+")) {
                        outputArea.append("Name is invalid. It must contain only letters. Student was not updated.\n");
                        return;
                    }
                    if (!name.isEmpty()) student.setName(name);

                    String ageText = ageField.getText().trim();
                    if (!ageText.isEmpty()) {
                        int age = 0;
                        try {
                            age = Integer.parseInt(ageText);
                            if (age <= 0) {
                                outputArea.append("Age must be a positive number greater than 0. Please enter a valid age.\n");
                                return;
                            }
                            student.setAge(age);
                        } catch (NumberFormatException ex) {
                            outputArea.append("Age is invalid. Please enter a valid number for age.\n");
                            return;
                        }
                    }

                    String gradeText = gradeField.getText().trim();
                    if (!gradeText.isEmpty()) {
                        double grade = 0.0;
                        try {
                            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                            symbols.setDecimalSeparator(',');
                            DecimalFormat df = new DecimalFormat();
                            df.setDecimalFormatSymbols(symbols);
                            grade = df.parse(gradeText).doubleValue();

                            if (grade < 0.0 || grade > 100.0) {
                                outputArea.append("Grade is invalid. It must be a number between 0.0 and 100.0. Please enter a valid grade.\n");
                                return;
                            }
                            student.setGrade(grade);
                        } catch (NumberFormatException | java.text.ParseException ex) {
                            outputArea.append("Grade is invalid. Please enter a valid number for grade.\n");
                            return;
                        }
                    }

                    // Aktualizacja studenta
                    studentManager.updateStudent(student.getStudentID(), student.getName(), student.getAge(), student.getGrade());
                    outputArea.append("Student updated successfully.\n");

                } catch (DatabaseConnectionException ex) {
                    outputArea.append("Database Connection Error: " + ex.getMessage() + "\n");
                }
            }
        });

        // Obsługa zdarzeń dla przycisku "Display Students"
        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Pobranie wszystkich studentów i wyświetlenie ich w oknie tekstowym
                    List<Student> students = studentManager.displayAllStudents();

                    if (students.isEmpty()) {
                        outputArea.append("No students to display.\n");
                        return;
                    }

                    // Formatowanie wyników
                    String format = "%-10s %-20s %-5s %-10s%n";
                    outputArea.append(String.format(format, "ID", "Name", "Age", "Grade"));
                    outputArea.append("-------------------------------------------\n");

                    // Wyświetlenie danych studentów
                    for (Student student : students) {
                        outputArea.append(String.format(format,
                                student.getStudentID(),
                                student.getName(),
                                student.getAge(),
                                String.format("%.1f", student.getGrade()).replace('.', ',')));
                    }

                    outputArea.append("\n");
                } catch (DatabaseConnectionException ex) {
                    outputArea.append("Database Connection Error: " + ex.getMessage() + "\n");
                }
            }
        });

        // Obsługa zdarzeń dla przycisku "Calculate Average Grade"
        averageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Obliczenie średniej oceny
                    double averageGrade = studentManager.calculateAverageGrade();
                    outputArea.append("Average Grade: " + averageGrade + "\n");
                } catch (DatabaseConnectionException ex) {
                    outputArea.append("Database Connection Error: " + ex.getMessage() + "\n");
                }
            }
        });

        // Wyświetlenie okna aplikacji
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            // Inicjalizacja połączenia z bazą danych i uruchomienie aplikacji
            DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
            StudentManager studentManager = new StudentManagerImpl(databaseConnection);

            SwingUtilities.invokeLater(() -> new StudentManagementGUI(studentManager));
        } catch (DatabaseConnectionException ex) {
            System.out.println("Błąd połączenia z bazą danych: " + ex.getMessage());
        }
    }
}
