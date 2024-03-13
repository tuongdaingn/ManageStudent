package com.example.manage_student;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class ManageStudentApplication {

  static String url = "jdbc:mysql://localhost:3306/manage_student";
  static String user = "root";
  static String password = "12345678";

  public static Connection conn = null;
  public static Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {

    //ĐĂNG NHẬP
    try {
      conn = DriverManager.getConnection(url, user, password);

      boolean isLoggedIn = login();

      if (isLoggedIn) {
        boolean isRunning = true;

        System.out.println("Đăng nhập thành công, vui lòng chọn chức năng");

        while (isRunning) {

          System.out.println("Chức năng: ");
          System.out.println("1. Nhập thông tin học sinh mới");
          System.out.println("2. Sửa thông tin học sinh");
          System.out.println("3. Xóa học sinh");
          System.out.println("4. Nhập điểm môn học");
          System.out.println("5. Sửa điểm môn học");
          System.out.println("6. Xóa điểm môn học");
          System.out.println("7. Hiển thị danh sách học sinh");
          System.out.println("8. Hiển thị danh sách điểm");
          System.out.println("9. Hiển thị học sinh theo id");
          System.out.println("10. Hiển thị điểm theo id của học sinh");
          System.out.println("11. Thoát");
          System.out.print("Chọn chức năng: ");

          int choice = scanner.nextInt();
          scanner.nextLine();

          switch (choice) {
            case 1:
              addStudent();
              break;
            case 2:
              editStudent();
              break;
            case 3:
              deleteStudent();
              break;
            case 4:
              inputScore();
              break;
            case 5:
              editScore();
              break;
            case 6:
              deleteScore();
              break;
            case 7:
              printStudents();
              break;
            case 8:
              printScores();
              break;
            case 9:
              printStudentById();
              break;
            case 10:
              printScoreById();
              break;
            case 11:
              isRunning = false;
              break;
            default:
              System.out.println("Chức năng không hợp lệ. Vui lòng chọn lại");
          }
        }

      } else {
        System.out.println("Đăng nhập thất bại");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  //IN RA DANH SÁCH ĐIỂM
  public static void printScores() {
    try {
      String sql = "SELECT scores.ScoreID, students.Fullname AS Fullname, subjects.SubjectName AS SubjectName, scores.Score " +
          "FROM scores " +
          "INNER JOIN students ON scores.StudentID = students.StudentID " +
          "INNER JOIN subjects ON scores.SubjectID = subjects.SubjectID";
      PreparedStatement statement = conn.prepareStatement(sql);
      ResultSet resultSet = statement.executeQuery();

      System.out.println("Danh sách điểm:");
      while (resultSet.next()) {
        int scoreID = resultSet.getInt("ScoreID");
        String fullname = resultSet.getString("Fullname");
        String subjectName = resultSet.getString("SubjectName");
        double score = resultSet.getDouble("Score");
        System.out.println(
            "ID score: " + scoreID + ", Student Name: " + fullname + ", Subject Name: " + subjectName
                + ", Score: " + score);
      }
    } catch (SQLException e) {
      System.out.println("Lỗi hiển thị danh sách điểm");
      e.printStackTrace();
    }
  }

  //IN RA DANH SÁCH HỌC SINH
  public static void printStudents() {
    try {
      String sql = "SELECT * FROM students";
      Statement statement = conn.createStatement();
      ResultSet resultSet = statement.executeQuery(sql);

      System.out.println("Danh sách học sinh:");
      while (resultSet.next()) {
        int student_id = resultSet.getInt("StudentID");
        String full_name = resultSet.getString("Fullname");
        Date dateOfBirth = resultSet.getDate("DateOfBirth");
        String class1 = resultSet.getString("Class");
        String address = resultSet.getString("Address");
        System.out.println(
            "ID: " + student_id + ", Student name: " + full_name + ", Date of birth: " + dateOfBirth
                + ", Class: " + class1 + ", Address: " + address);
      }
    } catch (SQLException e) {
      System.out.println("Lỗi hiển thị danh sách Student");
      e.printStackTrace();
    }
  }

  //IN RA HỌC SINH THEO ID
  public static void printStudentById() {

    try {
      String sql = "SELECT * FROM Students WHERE StudentID = ?";
      PreparedStatement statement = conn.prepareStatement(sql);
      System.out.print("Nhập ID của học sinh: ");
      int studentID = scanner.nextInt();
      scanner.nextLine();
      statement.setInt(1, studentID);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        String fullname = resultSet.getString("Fullname");
        Date dateOfBirth = resultSet.getDate("DateOfBirth");
        String className = resultSet.getString("Class");
        String address = resultSet.getString("Address");

        System.out.println("Thông tin của học sinh có ID " + studentID + ":");
        System.out.println("Họ và tên: " + fullname);
        System.out.println("Năm sinh: " + dateOfBirth);
        System.out.println("Lớp: " + className);
        System.out.println("Địa chỉ: " + address);
      } else {
        System.out.println("Không tìm thấy học sinh có ID " + studentID);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  //IN RA ĐIỂM THEO ID CỦA HỌC SINH
  public static void printScoreById() {
    try {
      String sql = "SELECT * FROM scores WHERE StudentID = ?";
      PreparedStatement statement = conn.prepareStatement(sql);
      System.out.print("Nhập ID của học sinh: ");
      int studentID = scanner.nextInt();
      scanner.nextLine();
      statement.setInt(1, studentID);
      ResultSet resultSet = statement.executeQuery();

      System.out.println("Danh sách điểm của học sinh có ID " + studentID + ":");
      System.out.println("ScoreID\tStudentID\tSubjectID\tScore");

      while (resultSet.next()) {
        int scoreID = resultSet.getInt("ScoreID");
        String subjectID = resultSet.getString("SubjectID");
        double score = resultSet.getDouble("Score");

        System.out.println(
            scoreID + "\t\t\t\t\t\t" + studentID + "\t\t\t\t\t\t" + subjectID + "\t\t\t\t" + score
        );
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  //CHỨC NĂNG ĐĂNG NHẬP
  private static boolean login() {
    try {
      System.out.println("Đăng nhập vào hệ thống:");
      System.out.print("Username: ");
      String username = scanner.nextLine();
      System.out.print("Password: ");
      String password = scanner.nextLine();

      String sql = "SELECT * FROM accounts WHERE Username = ? AND Password = ?";
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, username);
      statement.setString(2, password);

      ResultSet resultSet = statement.executeQuery();

      return resultSet.next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  //THÊM HỌC SINH
  private static void addStudent() {
    try {
      System.out.println("Nhập thông tin học sinh mới:");
      System.out.print("Tên đầy đủ: ");
      String fullname = scanner.nextLine();

      System.out.print("Ngày sinh (dd/MM/yyyy): ");
      String dateOfBirthString = scanner.nextLine();
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
      Date dateOfBirth = null;
      try {
        dateOfBirth = dateFormat.parse(dateOfBirthString);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }

      System.out.print("Lớp: ");
      String className = scanner.nextLine();

      System.out.print("Địa chỉ: ");
      String address = scanner.nextLine();

      String sql = "INSERT INTO students (Fullname, DateOfBirth, Class, Address) VALUES (?, ?, ?, ?)";
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setString(1, fullname);
      statement.setDate(2, new java.sql.Date(dateOfBirth.getTime()));
      statement.setString(3, className);
      statement.setString(4, address);

      int rowsInserted = statement.executeUpdate();
      if (rowsInserted > 0) {
        System.out.println("Đã thêm học sinh mới thành công.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  //SỬA HỌC SINH
  private static void editStudent() {
    try {
      System.out.println("Nhập ID của học sinh cần sửa: ");
      int studentID = scanner.nextInt();
      scanner.nextLine();

      String checkExistQuery = "SELECT * FROM students WHERE StudentID = ?";
      PreparedStatement checkExistStatement = conn.prepareStatement(checkExistQuery);
      checkExistStatement.setInt(1, studentID);
      ResultSet resultSet = checkExistStatement.executeQuery();

      if (resultSet.next()) {
        System.out.println("Nhập thông tin cần thay đổi: ");
        System.out.print("Tên đầy đủ: ");
        String fullname = scanner.nextLine();

        System.out.print("Ngày sinh (dd/MM/yyyy): ");
        String dateOfBirthString = scanner.nextLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateOfBirth = null;
        try {
          dateOfBirth = dateFormat.parse(dateOfBirthString);
        } catch (ParseException e) {
          System.out.println("Định dạng ngày tháng không hợp lệ!");
          e.printStackTrace();
          return;
        }

        System.out.print("Lớp: ");
        String Class = scanner.nextLine();

        System.out.print("Địa chỉ: ");
        String address = scanner.nextLine();

        String updateQuery = "UPDATE students SET fullname = ?, dateOfBirth = ?, Class = ?, address = ? WHERE StudentID = ?";
        PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
        updateStatement.setString(1, fullname);
        updateStatement.setDate(2, new java.sql.Date(dateOfBirth.getTime()));
        updateStatement.setString(3, Class);
        updateStatement.setString(4, address);
        updateStatement.setInt(5, studentID);

        int rowUpdated = updateStatement.executeUpdate();
        if (rowUpdated > 0) {
          System.out.println("Cập nhật thành công.");
        } else {
          System.out.println("Không tìm thấy học sinh có ID " + studentID);
        }
      } else {
        System.out.println("Không tìm thấy học sinh có ID " + studentID);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  //XÓA HỌC SINH
  private static void deleteStudent() {
    try {
      System.out.println("Nhập ID của học sinh cần xóa: ");
      int studentID = scanner.nextInt();
      scanner.nextLine();

      String checkExistQuery = "SELECT * FROM students WHERE StudentID = ?";
      PreparedStatement checkExistStatement = conn.prepareStatement(checkExistQuery);
      checkExistStatement.setInt(1, studentID);
      ResultSet resultSet = checkExistStatement.executeQuery();

      if (resultSet.next()) {
        String deleteQuery = "DELETE FROM students WHERE StudentID = ?";
        PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery);
        deleteStatement.setInt(1, studentID);

        int rowDeleted = deleteStatement.executeUpdate();
        if (rowDeleted > 0) {
          System.out.println("Xóa thành công");
        } else {
          System.out.println("Xóa thất bại");
        }
      } else {
        System.out.println("Không tìm thấy học sinh có ID " + studentID);
      }
    } catch (SQLException e) {
      e.printStackTrace();

    }
  }


  //NHẬP ĐIỂM
  private static void inputScore() {
    try {
      System.out.println("Nhập điểm môn học:");
      System.out.print("ID học sinh: ");
      int studentID = scanner.nextInt();
      scanner.nextLine();
      System.out.print("Nhập môn học (toán (phím 1), lý (phím 2), hóa (phím 3)): ");
      String subjectName = scanner.nextLine();
      System.out.print("Điểm: ");
      double score = scanner.nextDouble();

      String sql = "INSERT INTO scores (StudentID, SubjectID, Score) VALUES (?, ?, ?)";
      PreparedStatement statement = conn.prepareStatement(sql);
      statement.setInt(1, studentID);
      statement.setString(2, subjectName);
      statement.setDouble(3, score);

      int rowsInserted = statement.executeUpdate();
      if (rowsInserted > 0) {
        System.out.println("Đã nhập điểm thành công.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  //SỬA ĐIỂM
  private static void editScore() {
    try {
      displayStudents();
      System.out.print("Nhập ID của học sinh: ");
      int studentID = scanner.nextInt();
      scanner.nextLine();

      displaySubjects();
      System.out.print("Nhập ID của môn học: ");
      int subjectID = scanner.nextInt();
      scanner.nextLine();

      String checkExistQuery = "SELECT * FROM Scores WHERE StudentID = ? AND SubjectID = ?";
      PreparedStatement checkExistStatement = conn.prepareStatement(checkExistQuery);
      checkExistStatement.setInt(1, studentID);
      checkExistStatement.setInt(2, subjectID);
      ResultSet resultSet = checkExistStatement.executeQuery();

      if (resultSet.next()) {
        System.out.print("Nhập điểm mới cho môn học: ");
        double newScore = scanner.nextDouble();
        scanner.nextLine();

        String updateQuery = "UPDATE Scores SET Score = ? WHERE StudentID = ? AND SubjectID = ?";
        PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
        updateStatement.setDouble(1, newScore);
        updateStatement.setInt(2, studentID);
        updateStatement.setInt(3, subjectID);

        int rowsUpdated = updateStatement.executeUpdate();
        if (rowsUpdated > 0) {
          System.out.println("Đã cập nhật điểm mới thành công.");
        } else {
          System.out.println("Cập nhật điểm mới thất bại.");
        }
      } else {
        System.out.println("Không tìm thấy điểm của học sinh cho môn học này.");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  //HIỂN THỊ DANH SÁCH HỌC SINH ĐỂ CHỌN HỌC SINH CẦN SỬA ĐIỂM (THUỘC SỬA ĐIỂM)
  private static void displayStudents() {
    try {
      String sql = "SELECT * FROM students";
      PreparedStatement statement = conn.prepareStatement(sql);
      ResultSet resultSet = statement.executeQuery();

      System.out.println("Danh sách học sinh:");
      System.out.println("ID\tTên\t\tNăm sinh\tLớp\t\tĐịa chỉ");

      while (resultSet.next()) {
        int studentID = resultSet.getInt("StudentID");
        String fullname = resultSet.getString("Fullname");

        Date dateOfBirth = resultSet.getDate("DateOfBirth");
        String className = resultSet.getString("Class");
        String address = resultSet.getString("Address");

        System.out.println(
            studentID + "\t" + fullname + "\t" + dateOfBirth + "\t\t" + className + "\t\t"
                + address);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  //HIỂN THỊ DANH SÁCH MÔN HỌC ĐỂ CHỌN MÔN HỌC CẦN SỬA ĐIỂM (THUỘC SỬA ĐIỂM)
  private static void displaySubjects() {
    try {
      String sql = "SELECT * FROM subjects";
      PreparedStatement statement = conn.prepareStatement(sql);
      ResultSet resultSet = statement.executeQuery();

      System.out.println("Danh sách các môn học:");
      System.out.println("ID\tTên môn");

      while (resultSet.next()) {
        int subjectID = resultSet.getInt("SubjectID");
        String subjectName = resultSet.getString("SubjectName");

        System.out.println(subjectID + "\t" + subjectName);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  //XÓA ĐIỂM
  private static void deleteScore() {
    try {
      System.out.println("Nhập ID của điểm cần xóa: ");
      int scoreID = scanner.nextInt();
      scanner.nextLine();

      String checkExistQuery = "SELECT * FROM scores WHERE ScoreID = ?";
      PreparedStatement checkExistStatement = conn.prepareStatement(checkExistQuery);
      checkExistStatement.setInt(1, scoreID);
      ResultSet resultSet = checkExistStatement.executeQuery();

      if (resultSet.next()) {
        String deleteQuery = "DELETE FROM scores WHERE ScoreID = ?";
        PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery);
        deleteStatement.setInt(1, scoreID);

        int rowDeleted = deleteStatement.executeUpdate();
        if (rowDeleted > 0) {
          System.out.println("Xóa thành công");
        } else {
          System.out.println("Xóa thất bại");
        }
      } else {
        System.out.println("Không tìm thấy điểm có ID " + scoreID);
      }
    } catch (SQLException e) {
      e.printStackTrace();

    }
  }
}





