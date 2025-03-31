package com.example.labseven;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private EditText edData;
    private Button btnSave, btnLoad, btnWriteFile, btnReadFile, btnLogin, btnSignUp;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "StorageAppPrefs";
    private static final String KEY_DATA = "savedData";
    private static final int REQUEST_CODE_WRITE_PERM = 401;
    private static final String FILE_NAME = "storage_data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edData = findViewById(R.id.ed_data);
        btnSave = findViewById(R.id.btn_save);
        btnLoad = findViewById(R.id.btn_load);
        btnWriteFile = findViewById(R.id.btn_write_file);
        btnReadFile = findViewById(R.id.btn_read_file);
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_signup);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        requestStoragePermission();

        btnSave.setOnClickListener(v -> {
            String data = edData.getText().toString();
            if (!data.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_DATA, data);
                editor.apply();
                Toast.makeText(MainActivity.this, "Данные сохранены в SharedPreferences", Toast.LENGTH_SHORT).show();
            }
        });

        btnLoad.setOnClickListener(v -> {
            String savedData = sharedPreferences.getString(KEY_DATA, "Нет данных");
            edData.setText(savedData);
            Toast.makeText(MainActivity.this, "Данные загружены из SharedPreferences", Toast.LENGTH_SHORT).show();
        });

        btnWriteFile.setOnClickListener(v -> writeFile(edData.getText().toString()));
        btnReadFile.setOnClickListener(v -> {
            String data = readFile();
            edData.setText(data);
            Toast.makeText(MainActivity.this, "Данные загружены из файла", Toast.LENGTH_SHORT).show();
        });

        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        btnSignUp.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_PERM);
        }
    }

    private void writeFile(String data) {
        File file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes());
            Toast.makeText(this, "Данные записаны в файл", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка записи в файл", Toast.LENGTH_SHORT).show();
        }
    }

    private String readFile() {
        File file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
        if (!file.exists()) {
            return "Файл не найден";
        }
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int ch;
            while ((ch = fis.read()) != -1) {
                bos.write(ch);
            }
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка чтения файла";
        }
    }
}
