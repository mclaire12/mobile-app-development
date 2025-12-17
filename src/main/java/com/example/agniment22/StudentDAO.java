package com.example.agniment22;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
        private DatabaseHelper dbHelper;
        private SQLiteDatabase database;

        public StudentDAO(Context context) {
                dbHelper = new DatabaseHelper(context);
        }

        public void open() {
                database = dbHelper.getWritableDatabase();
        }

        public void close() {
                dbHelper.close();
        }

        // Insert Student
        public long insertStudent(Student student) {
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.StudentEntry.COLUMN_NAME, student.getName());
                values.put(DatabaseContract.StudentEntry.COLUMN_STUDENT_ID, student.getStudentId());
                values.put(DatabaseContract.StudentEntry.COLUMN_ENROLLMENT_DATE, student.getEnrollmentDate());
                values.put(DatabaseContract.StudentEntry.COLUMN_IS_ACTIVE, student.isActive() ? 1 : 0);
                values.put(DatabaseContract.StudentEntry.COLUMN_DEPARTMENT, student.getDepartment());
                values.put(DatabaseContract.StudentEntry.COLUMN_PHOTO_PATH, student.getPhotoPath());

                return database.insert(DatabaseContract.StudentEntry.TABLE_NAME, null, values);
        }

        // Get All Students
        public List<Student> getAllStudents() {
                List<Student> students = new ArrayList<>();
                Cursor cursor = database.query(
                                DatabaseContract.StudentEntry.TABLE_NAME,
                                null,
                                null,
                                null,
                                null,
                                null,
                                DatabaseContract.StudentEntry.COLUMN_NAME + " ASC");

                if (cursor != null && cursor.moveToFirst()) {
                        do {
                                Student student = new Student();
                                student.setId(cursor.getLong(
                                                cursor.getColumnIndexOrThrow(DatabaseContract.StudentEntry._ID)));
                                student.setName(
                                                cursor.getString(cursor.getColumnIndexOrThrow(
                                                                DatabaseContract.StudentEntry.COLUMN_NAME)));
                                student.setStudentId(cursor
                                                .getString(cursor.getColumnIndexOrThrow(
                                                                DatabaseContract.StudentEntry.COLUMN_STUDENT_ID)));
                                student.setEnrollmentDate(cursor
                                                .getString(cursor.getColumnIndexOrThrow(
                                                                DatabaseContract.StudentEntry.COLUMN_ENROLLMENT_DATE)));
                                student.setActive(cursor
                                                .getInt(cursor.getColumnIndexOrThrow(
                                                                DatabaseContract.StudentEntry.COLUMN_IS_ACTIVE)) == 1);
                                student.setDepartment(cursor
                                                .getString(cursor.getColumnIndexOrThrow(
                                                                DatabaseContract.StudentEntry.COLUMN_DEPARTMENT)));
                                student.setPhotoPath(cursor
                                                .getString(cursor.getColumnIndexOrThrow(
                                                                DatabaseContract.StudentEntry.COLUMN_PHOTO_PATH)));
                                students.add(student);
                        } while (cursor.moveToNext());
                        cursor.close();
                }

                return students;
        }

        // Get Student by ID
        public Student getStudentById(long id) {
                Cursor cursor = database.query(
                                DatabaseContract.StudentEntry.TABLE_NAME,
                                null,
                                DatabaseContract.StudentEntry._ID + " = ?",
                                new String[] { String.valueOf(id) },
                                null,
                                null,
                                null);

                Student student = null;
                if (cursor != null && cursor.moveToFirst()) {
                        student = new Student();
                        student.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.StudentEntry._ID)));
                        student.setName(cursor.getString(
                                        cursor.getColumnIndexOrThrow(DatabaseContract.StudentEntry.COLUMN_NAME)));
                        student.setStudentId(
                                        cursor.getString(cursor.getColumnIndexOrThrow(
                                                        DatabaseContract.StudentEntry.COLUMN_STUDENT_ID)));
                        student.setEnrollmentDate(cursor
                                        .getString(cursor.getColumnIndexOrThrow(
                                                        DatabaseContract.StudentEntry.COLUMN_ENROLLMENT_DATE)));
                        student.setActive(
                                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                                        DatabaseContract.StudentEntry.COLUMN_IS_ACTIVE)) == 1);
                        student.setDepartment(
                                        cursor.getString(cursor.getColumnIndexOrThrow(
                                                        DatabaseContract.StudentEntry.COLUMN_DEPARTMENT)));
                        student.setPhotoPath(
                                        cursor.getString(cursor.getColumnIndexOrThrow(
                                                        DatabaseContract.StudentEntry.COLUMN_PHOTO_PATH)));
                        cursor.close();
                }

                return student;
        }

        // Update Student
        public int updateStudent(Student student) {
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.StudentEntry.COLUMN_NAME, student.getName());
                values.put(DatabaseContract.StudentEntry.COLUMN_STUDENT_ID, student.getStudentId());
                values.put(DatabaseContract.StudentEntry.COLUMN_ENROLLMENT_DATE, student.getEnrollmentDate());
                values.put(DatabaseContract.StudentEntry.COLUMN_IS_ACTIVE, student.isActive() ? 1 : 0);
                values.put(DatabaseContract.StudentEntry.COLUMN_DEPARTMENT, student.getDepartment());
                values.put(DatabaseContract.StudentEntry.COLUMN_PHOTO_PATH, student.getPhotoPath());

                return database.update(
                                DatabaseContract.StudentEntry.TABLE_NAME,
                                values,
                                DatabaseContract.StudentEntry._ID + " = ?",
                                new String[] { String.valueOf(student.getId()) });
        }

        // Delete Student
        public int deleteStudent(long id) {
                // First delete related courses
                database.delete(
                                DatabaseContract.CourseEntry.TABLE_NAME,
                                DatabaseContract.CourseEntry.COLUMN_STUDENT_ID_FK + " = ?",
                                new String[] { String.valueOf(id) });

                // Then delete student
                return database.delete(
                                DatabaseContract.StudentEntry.TABLE_NAME,
                                DatabaseContract.StudentEntry._ID + " = ?",
                                new String[] { String.valueOf(id) });
        }

        // Insert Course
        public long insertCourse(StudentCourse course) {
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.CourseEntry.COLUMN_STUDENT_ID_FK, course.getStudentIdFk());
                values.put(DatabaseContract.CourseEntry.COLUMN_COURSE_NAME, course.getCourseName());
                values.put(DatabaseContract.CourseEntry.COLUMN_GRADE, course.getGrade());
                values.put(DatabaseContract.CourseEntry.COLUMN_COURSE_DATE, course.getCourseDate());

                return database.insert(DatabaseContract.CourseEntry.TABLE_NAME, null, values);
        }

        // Get Courses by Student ID
        public List<StudentCourse> getCoursesByStudentId(long studentId) {
                List<StudentCourse> courses = new ArrayList<>();
                Cursor cursor = database.query(
                                DatabaseContract.CourseEntry.TABLE_NAME,
                                null,
                                DatabaseContract.CourseEntry.COLUMN_STUDENT_ID_FK + " = ?",
                                new String[] { String.valueOf(studentId) },
                                null,
                                null,
                                null);

                if (cursor != null && cursor.moveToFirst()) {
                        do {
                                StudentCourse course = new StudentCourse();
                                course.setId(cursor.getLong(
                                                cursor.getColumnIndexOrThrow(DatabaseContract.CourseEntry._ID)));
                                course.setStudentIdFk(cursor
                                                .getLong(cursor.getColumnIndexOrThrow(
                                                                DatabaseContract.CourseEntry.COLUMN_STUDENT_ID_FK)));
                                course.setCourseName(cursor
                                                .getString(cursor.getColumnIndexOrThrow(
                                                                DatabaseContract.CourseEntry.COLUMN_COURSE_NAME)));
                                course.setGrade(cursor.getInt(cursor
                                                .getColumnIndexOrThrow(DatabaseContract.CourseEntry.COLUMN_GRADE)));
                                course.setCourseDate(cursor
                                                .getString(cursor.getColumnIndexOrThrow(
                                                                DatabaseContract.CourseEntry.COLUMN_COURSE_DATE)));
                                courses.add(course);
                        } while (cursor.moveToNext());
                        cursor.close();
                }

                return courses;
        }

        // Get Course by ID
        public StudentCourse getCourseById(long id) {
                Cursor cursor = database.query(
                                DatabaseContract.CourseEntry.TABLE_NAME,
                                null,
                                DatabaseContract.CourseEntry._ID + " = ?",
                                new String[] { String.valueOf(id) },
                                null,
                                null,
                                null);

                StudentCourse course = null;
                if (cursor != null && cursor.moveToFirst()) {
                        course = new StudentCourse();
                        course.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.CourseEntry._ID)));
                        course.setStudentIdFk(cursor
                                        .getLong(cursor.getColumnIndexOrThrow(
                                                        DatabaseContract.CourseEntry.COLUMN_STUDENT_ID_FK)));
                        course.setCourseName(cursor
                                        .getString(cursor.getColumnIndexOrThrow(
                                                        DatabaseContract.CourseEntry.COLUMN_COURSE_NAME)));
                        course.setGrade(cursor.getInt(
                                        cursor.getColumnIndexOrThrow(DatabaseContract.CourseEntry.COLUMN_GRADE)));
                        course.setCourseDate(cursor
                                        .getString(cursor.getColumnIndexOrThrow(
                                                        DatabaseContract.CourseEntry.COLUMN_COURSE_DATE)));
                        cursor.close();
                }

                return course;
        }

        // Update Course
        public int updateCourse(StudentCourse course) {
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.CourseEntry.COLUMN_COURSE_NAME, course.getCourseName());
                values.put(DatabaseContract.CourseEntry.COLUMN_GRADE, course.getGrade());
                values.put(DatabaseContract.CourseEntry.COLUMN_COURSE_DATE, course.getCourseDate());

                return database.update(
                                DatabaseContract.CourseEntry.TABLE_NAME,
                                values,
                                DatabaseContract.CourseEntry._ID + " = ?",
                                new String[] { String.valueOf(course.getId()) });
        }

        // Delete Course
        public int deleteCourse(long id) {
                return database.delete(
                                DatabaseContract.CourseEntry.TABLE_NAME,
                                DatabaseContract.CourseEntry._ID + " = ?",
                                new String[] { String.valueOf(id) });
        }
}
