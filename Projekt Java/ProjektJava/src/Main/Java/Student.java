public class Student {
    // Deklaracja pól klasy
    private String name;
    private int age;
    private double grade;
    private String studentID;

    // Konstruktor z pełnymi parametrami (do utworzenia obiektu z wszystkimi danymi)
    public Student(String name, int age, double grade, String studentID) {
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.studentID = studentID;
    }

    // Konstruktor bezparametrowy
    public Student() {
    }

    // Getter dla pola studentID
    public String getStudentID() {
        return studentID;
    }

    // Setter dla pola studentID
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    // Getter dla pola name
    public String getName() {
        return name;
    }

    // Setter dla pola name
    public void setName(String name) {
        this.name = name;
    }

    // Getter dla pola age
    public int getAge() {
        return age;
    }

    // Setter dla pola age
    public void setAge(int age) {
        this.age = age;
    }

    // Getter dla pola grade
    public double getGrade() {
        return grade;
    }

    // Setter dla pola grade
    public void setGrade(double grade) {
        this.grade = grade;
    }
}