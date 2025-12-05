package com.example.finalprojectmp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalprojectmp.R;
import com.example.finalprojectmp.database.DatabaseHelper;
import com.example.finalprojectmp.models.Event;
import com.example.finalprojectmp.models.Food;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EventFormActivity extends AppCompatActivity {

    private static final String STATE_FOOD_SELECTION = "state_food_selection";

    private DatabaseHelper dbHelper;
    private int userId;
    private int eventId = -1; // -1 = New Event, > 0 = Edit Mode

    private TextView textHeader;
    private TextInputEditText inputTitle;
    private TextInputEditText inputDate;
    private TextInputEditText inputTime;
    private TextInputEditText inputDesc;
    private LinearLayout foodSection;
    private LinearLayout foodListLayout;
    private Button btnSave;
    private Button btnDelete;
    private Button btnFood;

    private final ArrayList<Food> selectedFoods = new ArrayList<>();

    private ActivityResultLauncher<Intent> foodLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        dbHelper = DatabaseHelper.getInstance(this);
        userId = getIntent().getIntExtra("USER_ID", -1);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        bindViews();
        setupPickers();
        setupFoodLauncher();

        if (savedInstanceState != null) {
            ArrayList<Food> restoredFoods = (ArrayList<Food>) savedInstanceState.getSerializable(STATE_FOOD_SELECTION);
            if (restoredFoods != null) {
                selectedFoods.clear();
                selectedFoods.addAll(restoredFoods);
                updateFoodSection();
            }
        }

        if (eventId != -1) {
            setupEditMode();
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveEvent());
        btnFood.setOnClickListener(v -> {
            Intent intent = new Intent(EventFormActivity.this, FoodFormActivity.class);
            foodLauncher.launch(intent);
        });

        btnDelete.setOnClickListener(v -> {
            if (eventId == -1) {
                finish();
                return;
            }
            if (dbHelper.deleteEvent(eventId)) {
                Toast.makeText(this, "Event Deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindViews() {
        textHeader = findViewById(R.id.textFormHeader);
        inputTitle = findViewById(R.id.inputEventTitle);
        inputDate = findViewById(R.id.inputEventDate);
        inputTime = findViewById(R.id.inputEventTime);
        inputDesc = findViewById(R.id.inputEventDesc);
        btnSave = findViewById(R.id.btnSaveEvent);
        btnFood = findViewById(R.id.btnAddFood);
        btnDelete = findViewById(R.id.btnDeleteEvent);
        foodSection = findViewById(R.id.layoutFoodSection);
        foodListLayout = findViewById(R.id.layoutFoodList);
    }

    private void setupFoodLauncher() {
        foodLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                        return;
                    }
                    Intent data = result.getData();
                    Food food = (Food) data.getSerializableExtra(FoodFormActivity.EXTRA_FOOD_DATA);
                    if (food == null) {
                        int foodId = data.getIntExtra(FoodFormActivity.EXTRA_FOOD_ID, -1);
                        if (foodId != -1) {
                            food = dbHelper.getFoodById(foodId);
                        }
                    }
                    if (food != null) {
                        addFoodToSelection(food);
                    }
                }
        );
    }

    private void setupEditMode() {
        textHeader.setText("Edit Event");
        btnSave.setText("Update Event");
        btnDelete.setVisibility(View.VISIBLE);

        Event event = dbHelper.getEventById(eventId, userId);
        if (event != null) {
            inputTitle.setText(event.getTitle());
            inputDate.setText(event.getDate());
            inputTime.setText(event.getTime());
            inputDesc.setText(event.getDescription());
        }

        if (selectedFoods.isEmpty()) {
            selectedFoods.addAll(dbHelper.getFoodsForEvent(eventId));
            updateFoodSection();
        }
    }

    private void addFoodToSelection(Food food) {
        for (Food existing : selectedFoods) {
            if (existing.getId() == food.getId()) {
                return;
            }
        }
        selectedFoods.add(food);
        updateFoodSection();
    }

    private void updateFoodSection() {
        foodListLayout.removeAllViews();
        if (selectedFoods.isEmpty()) {
            foodSection.setVisibility(View.GONE);
            return;
        }
        foodSection.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Food food : selectedFoods) {
            View itemView = inflater.inflate(R.layout.item_food_row, foodListLayout, false);
            TextView nameView = itemView.findViewById(R.id.textFoodName);
            TextView detailView = itemView.findViewById(R.id.textFoodMeta);
            ImageButton removeButton = itemView.findViewById(R.id.btnRemoveFood);

            nameView.setText(food.getName());
            String category = food.getCategory() == null || food.getCategory().isEmpty()
                    ? "Uncategorized"
                    : food.getCategory();
            String priceLabel = food.isFree()
                    ? getString(R.string.food_price_free)
                    : String.format(Locale.getDefault(), getString(R.string.food_price_template), food.getPrice());
            detailView.setText(String.format(Locale.getDefault(), "%s • %s", category, priceLabel));

            removeButton.setOnClickListener(v -> {
                selectedFoods.remove(food);
                updateFoodSection();
            });

            foodListLayout.addView(itemView);
        }
    }

    private void saveEvent() {
        String title = getText(inputTitle);
        String date = getText(inputDate);
        String time = getText(inputTime);
        String desc = getText(inputDesc);

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventId == -1) {
            long id = dbHelper.createNewEvent(userId, title, date, time, desc);
            if (id != -1) {
                dbHelper.replaceEventFoods((int) id, selectedFoods);
                Toast.makeText(this, "Event Created!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error creating event", Toast.LENGTH_SHORT).show();
            }

        } else {
            Event event = new Event();
            event.setId(eventId);
            event.setTitle(title);
            event.setDate(date);
            event.setTime(time);
            event.setDescription(desc);

            if (dbHelper.updateEvent(event)) {
                dbHelper.replaceEventFoods(eventId, selectedFoods);
                Toast.makeText(this, "Event Updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating event", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getText(TextInputEditText editText) {
        return editText != null && editText.getText() != null
                ? editText.getText().toString().trim()
                : "";
    }

    private void setupPickers() {
        inputDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                inputDate.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        inputTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                inputTime.setText(time);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!selectedFoods.isEmpty()) {
            outState.putSerializable(STATE_FOOD_SELECTION, selectedFoods);
        }
    }
}
