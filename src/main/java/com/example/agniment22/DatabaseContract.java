package com.example.agniment22;

import android.provider.BaseColumns;

public class DatabaseContract {

    private DatabaseContract() {
    }

    public static class StudentEntry implements BaseColumns {
        public static final String TABLE_NAME = "students";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_STUDENT_ID = "student_id";
        public static final String COLUMN_ENROLLMENT_DATE = "enrollment_date";
        public static final String COLUMN_IS_ACTIVE = "is_active";
        public static final String COLUMN_DEPARTMENT = "department";
        public static final String COLUMN_PHOTO_PATH = "photo_path";
    }

    public static class CourseEntry implements BaseColumns {
        public static final String TABLE_NAME = "courses";
        public static final String COLUMN_STUDENT_ID_FK = "student_id_fk";
        public static final String COLUMN_COURSE_NAME = "course_name";
        public static final String COLUMN_GRADE = "grade";
        public static final String COLUMN_COURSE_DATE = "course_date";
    }
}
