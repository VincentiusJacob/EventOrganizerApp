package com.example.finalprojectmp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Food;
import com.google.android.material.textfield.TextInputEditText;

public class FoodFormActivity extends AppCompatActivity {

    public static final String EXTRA_FOOD_ID = "FOOD_ID";
    public static final String EXTRA_FOOD_DATA = "FOOD_DATA";

    private TextInputEditText inputName;
    private TextInputEditText inputPrice;
    private TextInputEditText inputCategory;
    private TextInputEditText inputDescription;
    private Button btnSave;
    private Button btnCancel;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_form);

        dbHelper = DatabaseHelper.getInstance(this);

        inputName = findViewById(R.id.inputFoodName);
        inputPrice = findViewById(R.id.inputFoodPrice);
        inputCategory = findViewById(R.id.inputFoodCategory);
        inputDescription = findViewById(R.id.inputFoodDesc);
        btnSave = findViewById(R.id.btnSaveFood);
        btnCancel = findViewById(R.id.btnCancelFood);

        btnSave.setOnClickListener(v -> saveFood());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveFood() {
        String name = getText(inputName);
        String desc = getText(inputDescription);
        String priceText = getText(inputPrice);
        String category = getText(inputCategory);

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter food name", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0.0;
        if (!priceText.isEmpty()) {
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price value", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Food food = new Food();
        food.setName(name);
        food.setDescription(desc);
        food.setPrice(price);
        food.setFree(price <= 0.0);
        food.setCategory(category);
        food.setImageUrl("");
        food.setAvailable(true);

        long insertId = dbHelper.insertFood(food);
        if (insertId > 0) {
            food.setId((int) insertId);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_FOOD_ID, (int) insertId);
            intent.putExtra(EXTRA_FOOD_DATA, food);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to save food", Toast.LENGTH_SHORT).show();
        }
    }

    private String getText(TextInputEditText editText) {
        return editText != null && !TextUtils.isEmpty(editText.getText())
                ? editText.getText().toString().trim()
                : "";
    }
}
