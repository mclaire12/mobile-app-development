package com.example.agniment22;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> students;
    private OnStudentClickListener listener;

    public interface OnStudentClickListener {
        void onEditClick(Student student);

        void onDeleteClick(Student student);
    }

    public StudentAdapter(List<Student> students, OnStudentClickListener listener) {
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.textViewName.setText(student.getName());
        holder.textViewStudentId.setText(student.getStudentId());
        holder.textViewDepartment.setText(student.getDepartment());

        // Load image if exists
        if (student.getPhotoPath() != null && !student.getPhotoPath().isEmpty()) {
            try {
                Uri imageUri = Uri.parse(student.getPhotoPath());
                InputStream inputStream = holder.itemView.getContext().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                holder.imageViewPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                holder.imageViewPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.imageViewPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.buttonEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(student);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPhoto;
        TextView textViewName;
        TextView textViewStudentId;
        TextView textViewDepartment;
        ImageButton buttonEdit;
        ImageButton buttonDelete;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPhoto = itemView.findViewById(R.id.imageView_student_photo);
            textViewName = itemView.findViewById(R.id.textView_student_name);
            textViewStudentId = itemView.findViewById(R.id.textView_student_id);
            textViewDepartment = itemView.findViewById(R.id.textView_department);
            buttonEdit = itemView.findViewById(R.id.button_edit);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}
