package com.example.agniment22;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StudentListFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private StudentDAO studentDAO;
    private List<Student> studentList;
    private TextView textViewEmpty;
    private FloatingActionButton fabAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);

        studentDAO = new StudentDAO(getContext());
        studentDAO.open();
        studentList = new ArrayList<>();

        initializeViews(view);
        setupRecyclerView();
        loadStudents();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView_students);
        textViewEmpty = view.findViewById(R.id.textView_empty);
        fabAdd = view.findViewById(R.id.fab_add);

        fabAdd.setOnClickListener(v -> {
            if (getActivity() instanceof Activity3) {
                ((Activity3) getActivity()).loadFragment(new StudentFormFragment(), true);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new StudentAdapter(studentList, new StudentAdapter.OnStudentClickListener() {
            @Override
            public void onEditClick(Student student) {
                Bundle args = new Bundle();
                args.putLong("student_id", student.getId());
                StudentFormFragment formFragment = new StudentFormFragment();
                formFragment.setArguments(args);
                if (getActivity() instanceof Activity3) {
                    ((Activity3) getActivity()).loadFragment(formFragment, true);
                }
            }

            @Override
            public void onDeleteClick(Student student) {
                int rowsAffected = studentDAO.deleteStudent(student.getId());
                if (rowsAffected > 0) {
                    Toast.makeText(getContext(), getString(R.string.student_deleted), Toast.LENGTH_SHORT).show();
                    loadStudents();
                }
            }

            @Override
            public void onViewClick(Student student) {
                Bundle args = new Bundle();
                args.putLong("student_id", student.getId());
                StudentDetailFragment detailFragment = new StudentDetailFragment();
                detailFragment.setArguments(args);
                if (getActivity() instanceof Activity3) {
                    ((Activity3) getActivity()).loadFragment(detailFragment, true);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    public void loadStudents() {
        studentList.clear();
        studentList.addAll(studentDAO.getAllStudents());
        adapter.notifyDataSetChanged();

        if (studentList.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStudents();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (studentDAO != null) {
            studentDAO.close();
        }
    }
}
