package com.example.agniment22;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StudentDetailFragment extends Fragment {

    private Student student;
    private StudentDAO studentDAO;
    private List<StudentCourse> courseList;
    private CourseAdapter courseAdapter;
    private RecyclerView recyclerViewCourses;
    private TextView textViewName, textViewStudentId, textViewEnrollmentDate, textViewDepartment, textViewStatus;
    private ImageView imageViewPhoto;
    private FloatingActionButton fabAddCourse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_detail, container, false);

        studentDAO = new StudentDAO(getContext());
        studentDAO.open();
        courseList = new ArrayList<>();

        Bundle args = getArguments();
        if (args != null && args.containsKey("student_id")) {
            long studentId = args.getLong("student_id");
            student = studentDAO.getStudentById(studentId);
        }

        if (student == null) {
            Toast.makeText(getContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return view;
        }

        initializeViews(view);
        loadStudentDetails();
        setupCoursesRecyclerView();
        loadCourses();

        return view;
    }

    private void initializeViews(View view) {
        textViewName = view.findViewById(R.id.textView_detail_name);
        textViewStudentId = view.findViewById(R.id.textView_detail_student_id);
        textViewEnrollmentDate = view.findViewById(R.id.textView_detail_enrollment_date);
        textViewDepartment = view.findViewById(R.id.textView_detail_department);
        textViewStatus = view.findViewById(R.id.textView_detail_status);
        imageViewPhoto = view.findViewById(R.id.imageView_detail_photo);
        recyclerViewCourses = view.findViewById(R.id.recyclerView_courses);
        fabAddCourse = view.findViewById(R.id.fab_add_course);

        fabAddCourse.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("student_id", student.getId());
            CourseFormFragment courseFormFragment = new CourseFormFragment();
            courseFormFragment.setArguments(args);
            if (getActivity() instanceof Activity3) {
                ((Activity3) getActivity()).loadFragment(courseFormFragment, true);
            }
        });
    }

    private void loadStudentDetails() {
        textViewName.setText(student.getName());
        textViewStudentId.setText(student.getStudentId());
        textViewEnrollmentDate.setText(student.getEnrollmentDate());
        textViewDepartment.setText(student.getDepartment());
        textViewStatus.setText(student.isActive() ? getString(R.string.active) : getString(R.string.inactive));

        // Load image if exists
        if (student.getPhotoPath() != null && !student.getPhotoPath().isEmpty()) {
            try {
                Uri imageUri = Uri.parse(student.getPhotoPath());
                InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewPhoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                imageViewPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            imageViewPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void setupCoursesRecyclerView() {
        courseAdapter = new CourseAdapter(courseList, new CourseAdapter.OnCourseClickListener() {
            @Override
            public void onEditClick(StudentCourse course) {
                Bundle args = new Bundle();
                args.putLong("course_id", course.getId());
                args.putLong("student_id", student.getId());
                CourseFormFragment courseFormFragment = new CourseFormFragment();
                courseFormFragment.setArguments(args);
                if (getActivity() instanceof Activity3) {
                    ((Activity3) getActivity()).loadFragment(courseFormFragment, true);
                }
            }

            @Override
            public void onDeleteClick(StudentCourse course) {
                int rowsAffected = studentDAO.deleteCourse(course.getId());
                if (rowsAffected > 0) {
                    Toast.makeText(getContext(), getString(R.string.course_deleted), Toast.LENGTH_SHORT).show();
                    loadCourses();
                }
            }
        });

        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCourses.setAdapter(courseAdapter);
    }

    private void loadCourses() {
        courseList.clear();
        courseList.addAll(studentDAO.getCoursesByStudentId(student.getId()));
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (student != null) {
            // Reload student data in case it was updated
            student = studentDAO.getStudentById(student.getId());
            if (student != null) {
                loadStudentDetails();
            }
            loadCourses();
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




