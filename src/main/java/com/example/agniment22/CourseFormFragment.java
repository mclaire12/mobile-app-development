package com.example.agniment22;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CourseFormFragment extends Fragment {

    private EditText editTextCourseName, editTextGrade, editTextCourseDate;
    private Button buttonSave, buttonCancel;
    private StudentDAO studentDAO;
    private StudentCourse currentCourse;
    private long studentId;
    private Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_form, container, false);

        studentDAO = new StudentDAO(getContext());
        studentDAO.open();
        calendar = Calendar.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            studentId = args.getLong("student_id");
            if (args.containsKey("course_id")) {
                long courseId = args.getLong("course_id");
                currentCourse = studentDAO.getCourseById(courseId);
            }
        }

        initializeViews(view);
        setupDatePicker();
        setupButtons();

        if (currentCourse != null) {
            loadCourseData();
        }

        return view;
    }

    private void initializeViews(View view) {
        editTextCourseName = view.findViewById(R.id.editText_course_name);
        editTextGrade = view.findViewById(R.id.editText_grade);
        editTextCourseDate = view.findViewById(R.id.editText_course_date);
        buttonSave = view.findViewById(R.id.button_save_course);
        buttonCancel = view.findViewById(R.id.button_cancel_course);
    }

    private void setupDatePicker() {
        editTextCourseDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        editTextCourseDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private void setupButtons() {
        buttonSave.setOnClickListener(v -> saveCourse());
        buttonCancel.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadCourseData() {
        if (currentCourse != null) {
            editTextCourseName.setText(currentCourse.getCourseName());
            editTextGrade.setText(String.valueOf(currentCourse.getGrade()));
            editTextCourseDate.setText(currentCourse.getCourseDate());
        }
    }

    private void saveCourse() {
        String courseName = editTextCourseName.getText().toString().trim();
        String gradeStr = editTextGrade.getText().toString().trim();
        String courseDate = editTextCourseDate.getText().toString().trim();

        if (TextUtils.isEmpty(courseName) || TextUtils.isEmpty(gradeStr) || TextUtils.isEmpty(courseDate)) {
            Toast.makeText(getContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        int grade;
        try {
            grade = Integer.parseInt(gradeStr);
            if (grade < 0 || grade > 100) {
                Toast.makeText(getContext(), getString(R.string.grade_hint), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), getString(R.string.grade_hint), Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentCourse == null) {
            // Create new course
            StudentCourse course = new StudentCourse(studentId, courseName, grade, courseDate);
            long id = studentDAO.insertCourse(course);
            if (id > 0) {
                Toast.makeText(getContext(), getString(R.string.course_saved), Toast.LENGTH_SHORT).show();
                // Go back to detail view
                if (getActivity() instanceof Activity3) {
                    Bundle args = new Bundle();
                    args.putLong("student_id", studentId);
                    StudentDetailFragment detailFragment = new StudentDetailFragment();
                    detailFragment.setArguments(args);
                    ((Activity3) getActivity()).loadFragment(detailFragment, false);
                }
            }
        } else {
            // Update existing course
            currentCourse.setCourseName(courseName);
            currentCourse.setGrade(grade);
            currentCourse.setCourseDate(courseDate);

            int rowsAffected = studentDAO.updateCourse(currentCourse);
            if (rowsAffected > 0) {
                Toast.makeText(getContext(), getString(R.string.course_saved), Toast.LENGTH_SHORT).show();
                // Go back to detail view
                if (getActivity() instanceof Activity3) {
                    Bundle args = new Bundle();
                    args.putLong("student_id", studentId);
                    StudentDetailFragment detailFragment = new StudentDetailFragment();
                    detailFragment.setArguments(args);
                    ((Activity3) getActivity()).loadFragment(detailFragment, false);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (studentDAO != null) {
            studentDAO.close();
        }
    }
}




