package com.example.midterm;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_AGE = 17;

    private EditText etName, etId;
    private TextView tvAge;
    private SeekBar seekAge;
    private ListView listStudents;

    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private final ArrayList<String> displayList = new ArrayList<>();
    private final ArrayList<String[]> dataList = new ArrayList<>(); // {id, name, age}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etId = findViewById(R.id.etId);
        tvAge = findViewById(R.id.tvAge);
        seekAge = findViewById(R.id.seekAge);
        listStudents = findViewById(R.id.listStudents);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnView = findViewById(R.id.btnView);

        dbHelper = new DatabaseHelper(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listStudents.setAdapter(adapter);

        seekAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvAge.setText("Age: " + (progress + MIN_AGE));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Klik item list -> isi form
        listStudents.setOnItemClickListener((parent, view, position, id) -> {
            String[] s = dataList.get(position);
            etId.setText(s[0]);
            etName.setText(s[1]);
            seekAge.setProgress(Integer.parseInt(s[2]) - MIN_AGE);
        });

        btnAdd.setOnClickListener(v -> {
            if (!validate()) return;
            boolean ok = dbHelper.insertStudent(getId(), getName(), getAge());
            toast(ok ? "Data added" : "Failed: Student ID already exists");
            if (ok) { clearForm(); loadData(); }
        });

        btnEdit.setOnClickListener(v -> {
            if (!validate()) return;
            boolean ok = dbHelper.updateStudent(getId(), getName(), getAge());
            toast(ok ? "Data updated" : "Failed: Student ID not found");
            if (ok) { clearForm(); loadData(); }
        });

        btnDelete.setOnClickListener(v -> {
            if (getId().isEmpty()) {
                toast("Enter Student ID to delete");
                return;
            }
            boolean ok = dbHelper.deleteStudent(getId());
            toast(ok ? "Data deleted" : "Failed: Student ID not found");
            if (ok) { clearForm(); loadData(); }
        });

        btnView.setOnClickListener(v -> loadData());

        loadData();
    }

    private void loadData() {
        displayList.clear();
        dataList.clear();
        Cursor c = dbHelper.getAllStudents();
        while (c.moveToNext()) {
            String id = c.getString(0);
            String name = c.getString(1);
            int age = c.getInt(2);
            dataList.add(new String[]{id, name, String.valueOf(age)});
            displayList.add("ID: " + id + "\nName: " + name + "\nAge: " + age);
        }
        c.close();
        adapter.notifyDataSetChanged();
        if (displayList.isEmpty()) toast("No data");
    }

    private boolean validate() {
        if (getName().isEmpty()) {
            etName.setError("Name required");
            return false;
        }
        if (getId().isEmpty()) {
            etId.setError("ID required");
            return false;
        }
        int age = getAge();
        if (age < 17 || age > 50) {
            toast("Age must be between 17 and 50");
            return false;
        }
        return true;
    }

    private String getName() { return etName.getText().toString().trim(); }
    private String getId() { return etId.getText().toString().trim(); }
    private int getAge() { return seekAge.getProgress() + MIN_AGE; }

    private void clearForm() {
        etName.setText("");
        etId.setText("");
        seekAge.setProgress(0);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}