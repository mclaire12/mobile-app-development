package com.example.agniment22;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StudentFormFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private EditText editTextName, editTextStudentId, editTextEnrollmentDate;
    private Spinner spinnerDepartment;
    private CheckBox checkboxIsActive;
    private ImageView imageViewPhoto;
    private Button buttonSave, buttonCancel, buttonSelectPhoto;
    private StudentDAO studentDAO;
    private Student currentStudent;
    private String selectedPhotoPath;
    private Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_form, container, false);

        studentDAO = new StudentDAO(getContext());
        studentDAO.open();
        calendar = Calendar.getInstance();

        initializeViews(view);
        setupSpinner();
        setupDatePicker();
        setupImagePicker();
        setupButtons();

        // Check if editing existing student
        Bundle args = getArguments();
        if (args != null && args.containsKey("student_id")) {
            long studentId = args.getLong("student_id");
            loadStudentData(studentId);
        }

        return view;
    }

    private void initializeViews(View view) {
        editTextName = view.findViewById(R.id.editText_name);
        editTextStudentId = view.findViewById(R.id.editText_student_id);
        editTextEnrollmentDate = view.findViewById(R.id.editText_enrollment_date);
        spinnerDepartment = view.findViewById(R.id.spinner_department);
        checkboxIsActive = view.findViewById(R.id.checkbox_is_active);
        imageViewPhoto = view.findViewById(R.id.imageView_photo);
        buttonSave = view.findViewById(R.id.button_save);
        buttonCancel = view.findViewById(R.id.button_cancel);
        buttonSelectPhoto = view.findViewById(R.id.button_select_photo);
    }

    private void setupSpinner() {
        String[] departments = {
                getString(R.string.dept_computer_science),
                getString(R.string.dept_mathematics),
                getString(R.string.dept_physics),
                getString(R.string.dept_chemistry),
                getString(R.string.dept_biology)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(adapter);
    }

    private void setupDatePicker() {
        editTextEnrollmentDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        editTextEnrollmentDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private void setupImagePicker() {
        buttonSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            selectedPhotoPath = imageUri.toString();
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupButtons() {
        buttonSave.setOnClickListener(v -> saveStudent());
        buttonCancel.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadStudentData(long studentId) {
        currentStudent = studentDAO.getStudentById(studentId);
        if (currentStudent != null) {
            editTextName.setText(currentStudent.getName());
            editTextStudentId.setText(currentStudent.getStudentId());
            editTextEnrollmentDate.setText(currentStudent.getEnrollmentDate());
            checkboxIsActive.setChecked(currentStudent.isActive());
            selectedPhotoPath = currentStudent.getPhotoPath();

            // Set spinner selection
            String[] departments = {
                    getString(R.string.dept_computer_science),
                    getString(R.string.dept_mathematics),
                    getString(R.string.dept_physics),
                    getString(R.string.dept_chemistry),
                    getString(R.string.dept_biology)
            };
            for (int i = 0; i < departments.length; i++) {
                if (departments[i].equals(currentStudent.getDepartment())) {
                    spinnerDepartment.setSelection(i);
                    break;
                }
            }

            // Load image if exists
            if (selectedPhotoPath != null && !selectedPhotoPath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(selectedPhotoPath);
                    InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageViewPhoto.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveStudent() {
        String name = editTextName.getText().toString().trim();
        String studentId = editTextStudentId.getText().toString().trim();
        String enrollmentDate = editTextEnrollmentDate.getText().toString().trim();
        String department = spinnerDepartment.getSelectedItem().toString();
        boolean isActive = checkboxIsActive.isChecked();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(studentId) || TextUtils.isEmpty(enrollmentDate)) {
            Toast.makeText(getContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentStudent == null) {
            // Create new student
            Student student = new Student(name, studentId, enrollmentDate, isActive, department, selectedPhotoPath);
            long id = studentDAO.insertStudent(student);
            if (id > 0) {
                Toast.makeText(getContext(), getString(R.string.student_saved), Toast.LENGTH_SHORT).show();
                // Switch to list fragment
                if (getActivity() instanceof Activity3) {
                    ((Activity3) getActivity()).loadFragment(new StudentListFragment(), false);
                }
            }
        } else {
            // Update existing student
            currentStudent.setName(name);
            currentStudent.setStudentId(studentId);
            currentStudent.setEnrollmentDate(enrollmentDate);
            currentStudent.setActive(isActive);
            currentStudent.setDepartment(department);
            currentStudent.setPhotoPath(selectedPhotoPath);

            int rowsAffected = studentDAO.updateStudent(currentStudent);
            if (rowsAffected > 0) {
                Toast.makeText(getContext(), getString(R.string.student_saved), Toast.LENGTH_SHORT).show();
                // Switch to list fragment
                if (getActivity() instanceof Activity3) {
                    ((Activity3) getActivity()).loadFragment(new StudentListFragment(), false);
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
