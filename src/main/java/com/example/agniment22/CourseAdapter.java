package com.example.agniment22;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<StudentCourse> courses;
    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onEditClick(StudentCourse course);

        void onDeleteClick(StudentCourse course);
    }

    public CourseAdapter(List<StudentCourse> courses, OnCourseClickListener listener) {
        this.courses = courses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        StudentCourse course = courses.get(position);
        holder.textViewCourseName.setText(course.getCourseName());
        holder.textViewGrade.setText(String.valueOf(course.getGrade()));
        holder.textViewCourseDate.setText(course.getCourseDate());

        holder.buttonEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(course);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCourseName;
        TextView textViewGrade;
        TextView textViewCourseDate;
        ImageButton buttonEdit;
        ImageButton buttonDelete;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCourseName = itemView.findViewById(R.id.textView_course_name);
            textViewGrade = itemView.findViewById(R.id.textView_grade);
            textViewCourseDate = itemView.findViewById(R.id.textView_course_date);
            buttonEdit = itemView.findViewById(R.id.button_edit_course);
            buttonDelete = itemView.findViewById(R.id.button_delete_course);
        }
    }
}
