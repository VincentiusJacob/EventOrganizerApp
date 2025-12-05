package com.example.finalprojectmp.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.finalprojectmp.models.Attendance;
import com.example.finalprojectmp.models.Event;
import com.example.finalprojectmp.models.Food;
import com.example.finalprojectmp.models.Organizer;
import com.example.finalprojectmp.models.Participant;
import com.example.finalprojectmp.models.QRCode;
import com.example.finalprojectmp.models.Rsvp;
import com.example.finalprojectmp.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "EventManagementDB";
    private static final int DATABASE_VERSION = 5;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PARTICIPANTS = "participants";
    public static final String TABLE_ORGANIZERS = "organizers";
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_RSVPS = "rsvps";
    public static final String TABLE_QRCODES = "qrcodes";
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String TABLE_FOOD = "food"; // Added Food table
    public static final String TABLE_EVENT_FOOD = "event_food"; // Junction table for event-food relationship

    // Common column names
    public static final String KEY_ID = "id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_EVENT_ID = "event_id";
    public static final String KEY_FOOD_ID = "food_id";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";

    // USERS Table
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_PROFILE_PICTURE = "profile_picture";
    public static final String KEY_USER_TYPE = "user_type"; // "participant" or "organizer"
    public static final String KEY_IS_ACTIVE = "is_active";

    // PARTICIPANTS Table - specific columns
    public static final String KEY_INTERESTS = "interests";
    public static final String KEY_DIETARY_PREFERENCES = "dietary_preferences";
    public static final String KEY_EMERGENCY_CONTACT = "emergency_contact";
    public static final String KEY_EMERGENCY_CONTACT_PHONE = "emergency_contact_phone"; // Added missing field
    public static final String KEY_TOTAL_EVENTS_ATTENDED = "total_events_attended";
    public static final String KEY_TOTAL_RSVPS = "total_rsvps";
    public static final String KEY_ATTENDANCE_RATE = "attendance_rate";
    public static final String KEY_PREFERRED_EVENT_TYPES = "preferred_event_types";
    public static final String KEY_EMAIL_NOTIFICATIONS = "email_notifications";
    public static final String KEY_SMS_NOTIFICATIONS = "sms_notifications";

    // ORGANIZERS Table - specific columns
    public static final String KEY_ORGANIZATION_NAME = "organization_name";
    public static final String KEY_ORGANIZATION_TYPE = "organization_type";
    public static final String KEY_ORGANIZATION_ADDRESS = "organization_address";
    public static final String KEY_ORGANIZATION_WEBSITE = "organization_website";
    public static final String KEY_DESIGNATION = "designation";
    public static final String KEY_DEPARTMENT = "department";
    public static final String KEY_TOTAL_EVENTS_CREATED = "total_events_created";
    public static final String KEY_TOTAL_EVENTS_COMPLETED = "total_events_completed";
    public static final String KEY_TOTAL_ATTENDEES_MANAGED = "total_attendees_managed";
    public static final String KEY_AVERAGE_EVENT_RATING = "average_event_rating";
    public static final String KEY_IS_VERIFIED = "is_verified";
    public static final String KEY_VERIFICATION_DATE = "verification_date";
    public static final String KEY_BANK_ACCOUNT_DETAILS = "bank_account_details";
    public static final String KEY_TAX_ID = "tax_id";
    public static final String KEY_EVENT_BUDGET_LIMIT = "event_budget_limit";
    public static final String KEY_SPECIALIZATIONS = "specializations";

    // EVENTS Table
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CREATED_BY = "created_by";

    // RSVPS Table
    public static final String KEY_STATUS = "status";

    // QRCODES Table
    public static final String KEY_CODE_TEXT = "code_text";

    // ATTENDANCE Table
    public static final String KEY_CHECKED_IN = "checked_in";
    public static final String KEY_CHECK_IN_TIME = "check_in_time";

    // FOOD Table - Added
    public static final String KEY_FOOD_NAME = "name";
    public static final String KEY_FOOD_DESCRIPTION = "description";
    public static final String KEY_PRICE = "price";
    public static final String KEY_IS_FREE = "is_free";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_IS_AVAILABLE = "is_available";

    // Table Create Statements

    // Users table (base table)
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT NOT NULL,"
            + KEY_EMAIL + " TEXT UNIQUE NOT NULL,"
            + KEY_PASSWORD + " TEXT NOT NULL,"
            + KEY_PHONE_NUMBER + " TEXT,"
            + KEY_PROFILE_PICTURE + " TEXT,"
            + KEY_USER_TYPE + " TEXT NOT NULL CHECK(" + KEY_USER_TYPE + " IN ('participant', 'organizer')),"
            + KEY_IS_ACTIVE + " INTEGER DEFAULT 1,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + KEY_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    // Participants table (extends users)
    private static final String CREATE_TABLE_PARTICIPANTS = "CREATE TABLE " + TABLE_PARTICIPANTS + "("
            + KEY_USER_ID + " INTEGER PRIMARY KEY,"
            + KEY_INTERESTS + " TEXT,"
            + KEY_DIETARY_PREFERENCES + " TEXT,"
            + KEY_EMERGENCY_CONTACT + " TEXT,"
            + KEY_EMERGENCY_CONTACT_PHONE + " TEXT," // Added missing field
            + KEY_TOTAL_EVENTS_ATTENDED + " INTEGER DEFAULT 0,"
            + KEY_TOTAL_RSVPS + " INTEGER DEFAULT 0,"
            + KEY_ATTENDANCE_RATE + " REAL DEFAULT 0.0,"
            + KEY_PREFERRED_EVENT_TYPES + " TEXT,"
            + KEY_EMAIL_NOTIFICATIONS + " INTEGER DEFAULT 1,"
            + KEY_SMS_NOTIFICATIONS + " INTEGER DEFAULT 0,"
            + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ") ON DELETE CASCADE"
            + ")";

    // Organizers table
    private static final String CREATE_TABLE_ORGANIZERS = "CREATE TABLE " + TABLE_ORGANIZERS + "("
            + KEY_USER_ID + " INTEGER PRIMARY KEY,"
            + KEY_ORGANIZATION_NAME + " TEXT,"
            + KEY_ORGANIZATION_TYPE + " TEXT,"
            + KEY_ORGANIZATION_ADDRESS + " TEXT,"
            + KEY_ORGANIZATION_WEBSITE + " TEXT,"
            + KEY_DESIGNATION + " TEXT,"
            + KEY_DEPARTMENT + " TEXT,"
            + KEY_TOTAL_EVENTS_CREATED + " INTEGER DEFAULT 0,"
            + KEY_TOTAL_EVENTS_COMPLETED + " INTEGER DEFAULT 0,"
            + KEY_TOTAL_ATTENDEES_MANAGED + " INTEGER DEFAULT 0,"
            + KEY_AVERAGE_EVENT_RATING + " REAL DEFAULT 0.0,"
            + KEY_IS_VERIFIED + " INTEGER DEFAULT 0,"
            + KEY_VERIFICATION_DATE + " TEXT,"
            + KEY_BANK_ACCOUNT_DETAILS + " TEXT,"
            + KEY_TAX_ID + " TEXT,"
            + KEY_EVENT_BUDGET_LIMIT + " REAL DEFAULT 0.0,"
            + KEY_SPECIALIZATIONS + " TEXT,"
            + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ") ON DELETE CASCADE"
            + ")";

    // Events table
    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TITLE + " TEXT NOT NULL,"
            + KEY_DATE + " TEXT NOT NULL,"
            + KEY_TIME + " TEXT NOT NULL,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_CREATED_BY + " INTEGER NOT NULL,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + KEY_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + KEY_CREATED_BY + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")"
            + ")";

    // RSVPs table
    private static final String CREATE_TABLE_RSVPS = "CREATE TABLE " + TABLE_RSVPS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " INTEGER NOT NULL,"
            + KEY_EVENT_ID + " INTEGER NOT NULL,"
            + KEY_STATUS + " TEXT NOT NULL DEFAULT 'pending',"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + KEY_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "),"
            + "FOREIGN KEY(" + KEY_EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + KEY_ID + "),"
            + "UNIQUE(" + KEY_USER_ID + ", " + KEY_EVENT_ID + ")"
            + ")";

    // QRCodes table
    private static final String CREATE_TABLE_QRCODES = "CREATE TABLE " + TABLE_QRCODES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " INTEGER NOT NULL,"
            + KEY_EVENT_ID + " INTEGER NOT NULL,"
            + KEY_CODE_TEXT + " TEXT UNIQUE NOT NULL,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "),"
            + "FOREIGN KEY(" + KEY_EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + KEY_ID + "),"
            + "UNIQUE(" + KEY_USER_ID + ", " + KEY_EVENT_ID + ")"
            + ")";

    // Attendance table
    private static final String CREATE_TABLE_ATTENDANCE = "CREATE TABLE " + TABLE_ATTENDANCE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " INTEGER NOT NULL,"
            + KEY_EVENT_ID + " INTEGER NOT NULL,"
            + KEY_CHECKED_IN + " INTEGER DEFAULT 0,"
            + KEY_CHECK_IN_TIME + " DATETIME,"
            + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + "),"
            + "FOREIGN KEY(" + KEY_EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + KEY_ID + "),"
            + "UNIQUE(" + KEY_USER_ID + ", " + KEY_EVENT_ID + ")"
            + ")";

    // Food table - Added
    private static final String CREATE_TABLE_FOOD = "CREATE TABLE " + TABLE_FOOD + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_FOOD_NAME + " TEXT NOT NULL,"
            + KEY_FOOD_DESCRIPTION + " TEXT,"
            + KEY_PRICE + " REAL DEFAULT 0.0,"
            + KEY_IS_FREE + " INTEGER DEFAULT 0,"
            + KEY_CATEGORY + " TEXT,"
            + KEY_IMAGE_URL + " TEXT,"
            + KEY_IS_AVAILABLE + " INTEGER DEFAULT 1,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    // Event-Food junction table - Added
    private static final String CREATE_TABLE_EVENT_FOOD = "CREATE TABLE " + TABLE_EVENT_FOOD + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_EVENT_ID + " INTEGER NOT NULL,"
            + KEY_FOOD_ID + " INTEGER NOT NULL,"
            + "FOREIGN KEY(" + KEY_EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + KEY_ID + ") ON DELETE CASCADE,"
            + "FOREIGN KEY(" + KEY_FOOD_ID + ") REFERENCES " + TABLE_FOOD + "(" + KEY_ID + ") ON DELETE CASCADE,"
            + "UNIQUE(" + KEY_EVENT_ID + ", " + KEY_FOOD_ID + ")"
            + ")";

    private static DatabaseHelper instance;

    // Singleton pattern
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        instance.ensureSeedData();
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PARTICIPANTS);
        db.execSQL(CREATE_TABLE_ORGANIZERS);
        db.execSQL(CREATE_TABLE_EVENTS);
        db.execSQL(CREATE_TABLE_RSVPS);
        db.execSQL(CREATE_TABLE_QRCODES);
        db.execSQL(CREATE_TABLE_ATTENDANCE);
        db.execSQL(CREATE_TABLE_FOOD); // Added
        db.execSQL(CREATE_TABLE_EVENT_FOOD); // Added

        // Insert default admin organizer and sample participant
        insertDefaultUsers(db);
        insertSampleFood(db); // Added sample food
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables in reverse order of dependencies
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QRCODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RSVPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORGANIZERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Insert default users for testing
    private void insertDefaultUsers(SQLiteDatabase db) {
        // Insert admin organizer
        ContentValues adminValues = new ContentValues();
        adminValues.put(KEY_NAME, "Admin Organizer");
        adminValues.put(KEY_EMAIL, "admin@event.com");
        adminValues.put(KEY_PASSWORD, "admin123");
        adminValues.put(KEY_PHONE_NUMBER, "1234567890");
        adminValues.put(KEY_USER_TYPE, "organizer");
        adminValues.put(KEY_IS_ACTIVE, 1);
        long adminId = db.insert(TABLE_USERS, null, adminValues);

        // Insert organizer-specific data
        ContentValues orgValues = new ContentValues();
        orgValues.put(KEY_USER_ID, adminId);
        orgValues.put(KEY_ORGANIZATION_NAME, "Event Management Inc.");
        orgValues.put(KEY_ORGANIZATION_TYPE, "company");
        orgValues.put(KEY_IS_VERIFIED, 1);
        orgValues.put(KEY_VERIFICATION_DATE, getDateTime());
        db.insert(TABLE_ORGANIZERS, null, orgValues);

        // Insert sample participant
        ContentValues participantValues = new ContentValues();
        participantValues.put(KEY_NAME, "John Doe");
        participantValues.put(KEY_EMAIL, "john@example.com");
        participantValues.put(KEY_PASSWORD, "password123");
        participantValues.put(KEY_PHONE_NUMBER, "9876543210");
        participantValues.put(KEY_USER_TYPE, "participant");
        participantValues.put(KEY_IS_ACTIVE, 1);
        long participantId = db.insert(TABLE_USERS, null, participantValues);

        // Insert participant-specific data
        ContentValues partValues = new ContentValues();
        partValues.put(KEY_USER_ID, participantId);
        partValues.put(KEY_INTERESTS, "technology,music,sports");
        partValues.put(KEY_PREFERRED_EVENT_TYPES, "workshop,seminar");
        partValues.put(KEY_EMAIL_NOTIFICATIONS, 1);
        partValues.put(KEY_EMERGENCY_CONTACT, "Jane Doe");
        partValues.put(KEY_EMERGENCY_CONTACT_PHONE, "555-0123");
        db.insert(TABLE_PARTICIPANTS, null, partValues);

        insertSampleEvents(db, adminId, participantId);
    }

    // Insert sample food items
    private void insertSampleFood(SQLiteDatabase db) {
        String[][] foodItems = {
                {"Vegetarian Pizza", "Fresh vegetable pizza with cheese", "8.99", "0", "Main Course", "", "1"},
                {"Chicken Sandwich", "Grilled chicken sandwich with veggies", "6.50", "0", "Main Course", "", "1"},
                {"Caesar Salad", "Fresh Caesar salad with dressing", "5.99", "0", "Appetizer", "", "1"},
                {"Chocolate Cake", "Rich chocolate cake slice", "4.25", "0", "Dessert", "", "1"},
                {"Coffee", "Fresh brewed coffee", "0.00", "1", "Beverage", "", "1"},
                {"Orange Juice", "Fresh orange juice", "0.00", "1", "Beverage", "", "1"}
        };

        for (String[] food : foodItems) {
            ContentValues values = new ContentValues();
            values.put(KEY_FOOD_NAME, food[0]);
            values.put(KEY_FOOD_DESCRIPTION, food[1]);
            values.put(KEY_PRICE, food[2]);
            values.put(KEY_IS_FREE, food[3]);
            values.put(KEY_CATEGORY, food[4]);
            values.put(KEY_IMAGE_URL, food[5]);
            values.put(KEY_IS_AVAILABLE, food[6]);
            db.insert(TABLE_FOOD, null, values);
        }
    }

    private void insertSampleEvents(SQLiteDatabase db, long organizerId, long participantId) {
        if (organizerId <= 0) {
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long innovationSummitId = insertEvent(db, organizerId,
                "Innovation Summit",
                dateFormat.format(calendar.getTime()),
                "09:00",
                "Morning conference that highlights the latest campus projects and provides networking opportunities.");

        calendar.add(Calendar.DAY_OF_MONTH, 3);
        long foodFestivalId = insertEvent(db, organizerId,
                "Campus Food Festival",
                dateFormat.format(calendar.getTime()),
                "17:30",
                "Tasting booths from every student community with live acoustic performances and games.");

        calendar.add(Calendar.DAY_OF_MONTH, -7);
        long alumniNightId = insertEvent(db, organizerId,
                "Alumni Networking Night",
                dateFormat.format(calendar.getTime()),
                "19:00",
                "Panel talks and speed mentoring sessions between alumni and current students.");

        if (participantId > 0 && innovationSummitId > 0) {
            insertSampleRsvp(db, participantId, innovationSummitId);
        }

        if (participantId > 0 && foodFestivalId > 0) {
            insertSampleRsvp(db, participantId, foodFestivalId);
        }
    }

    private long insertEvent(SQLiteDatabase db, long organizerId, String title, String date, String time, String description) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_DATE, date);
        values.put(KEY_TIME, time);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_CREATED_BY, organizerId);
        values.put(KEY_CREATED_AT, getDateTime());
        values.put(KEY_UPDATED_AT, getDateTime());
        return db.insert(TABLE_EVENTS, null, values);
    }

    private void insertSampleRsvp(SQLiteDatabase db, long participantId, long eventId) {
        ContentValues rsvpValues = new ContentValues();
        rsvpValues.put(KEY_USER_ID, participantId);
        rsvpValues.put(KEY_EVENT_ID, eventId);
        rsvpValues.put(KEY_STATUS, Rsvp.STATUS_CONFIRMED);
        rsvpValues.put(KEY_CREATED_AT, getDateTime());
        rsvpValues.put(KEY_UPDATED_AT, getDateTime());
        db.insert(TABLE_RSVPS, null, rsvpValues);

        ensureQrCode(db, (int) participantId, (int) eventId);
    }

    private void ensureQrCode(SQLiteDatabase db, int userId, int eventId) {
        Cursor cursor = db.query(TABLE_QRCODES, null,
                KEY_USER_ID + "=? AND " + KEY_EVENT_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(eventId)},
                null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();

        if (!exists) {
            ContentValues qrValues = new ContentValues();
            qrValues.put(KEY_USER_ID, userId);
            qrValues.put(KEY_EVENT_ID, eventId);
            qrValues.put(KEY_CODE_TEXT, generateQrPayload(userId, eventId));
            qrValues.put(KEY_CREATED_AT, getDateTime());
            db.insert(TABLE_QRCODES, null, qrValues);
        }
    }

    private String generateQrPayload(int userId, int eventId) {
        return "EVENT:" + eventId + "|USER:" + userId + "|TS:" + System.currentTimeMillis() + "|KEY:" + UUID.randomUUID();
    }

    private void ensureQrCodeForRsvp(int userId, int eventId) {
        SQLiteDatabase db = getWritableDatabase();
        ensureQrCode(db, userId, eventId);
    }

    // Helper method to get datetime
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // Helper to get user email buat login
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE_NUMBER));
            String userType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_TYPE));

            User user = null;
            if ("participant".equals(userType)) {
                user = getParticipantById(id);
            } else if ("organizer".equals(userType)) {
                user = getOrganizerById(id);
            }

            cursor.close();
            return user;
        }

        cursor.close();
        return null;
    }

    public List<Event> getEventsForParticipant(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, u." + KEY_NAME + " AS organizer_name," +
                " (SELECT COUNT(*) FROM " + TABLE_RSVPS + " r WHERE r." + KEY_EVENT_ID + " = e." + KEY_ID +
                " AND r." + KEY_STATUS + " = '" + Rsvp.STATUS_CONFIRMED + "') AS total_rsvp," +
                " (SELECT COUNT(*) FROM " + TABLE_ATTENDANCE + " a WHERE a." + KEY_EVENT_ID + " = e." + KEY_ID +
                " AND a." + KEY_CHECKED_IN + " = 1) AS total_checked_in," +
                " (SELECT COUNT(*) FROM " + TABLE_RSVPS + " my WHERE my." + KEY_EVENT_ID + " = e." + KEY_ID +
                " AND my." + KEY_USER_ID + " = ? AND my." + KEY_STATUS + " = '" + Rsvp.STATUS_CONFIRMED + "') AS has_rsvp" +
                " FROM " + TABLE_EVENTS + " e" +
                " LEFT JOIN " + TABLE_USERS + " u ON u." + KEY_ID + " = e." + KEY_CREATED_BY +
                " ORDER BY e." + KEY_DATE + ", e." + KEY_TIME;

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        List<Event> events = new ArrayList<>();
        while (cursor.moveToNext()) {
            events.add(mapEventFromCursor(cursor));
        }
        cursor.close();
        return events;
    }

    public Event getEventById(int eventId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, u." + KEY_NAME + " AS organizer_name," +
                " (SELECT COUNT(*) FROM " + TABLE_RSVPS + " r WHERE r." + KEY_EVENT_ID + " = e." + KEY_ID +
                " AND r." + KEY_STATUS + " = '" + Rsvp.STATUS_CONFIRMED + "') AS total_rsvp," +
                " (SELECT COUNT(*) FROM " + TABLE_ATTENDANCE + " a WHERE a." + KEY_EVENT_ID + " = e." + KEY_ID +
                " AND a." + KEY_CHECKED_IN + " = 1) AS total_checked_in," +
                " (SELECT COUNT(*) FROM " + TABLE_RSVPS + " my WHERE my." + KEY_EVENT_ID + " = e." + KEY_ID +
                " AND my." + KEY_USER_ID + " = ? AND my." + KEY_STATUS + " = '" + Rsvp.STATUS_CONFIRMED + "') AS has_rsvp" +
                " FROM " + TABLE_EVENTS + " e" +
                " LEFT JOIN " + TABLE_USERS + " u ON u." + KEY_ID + " = e." + KEY_CREATED_BY +
                " WHERE e." + KEY_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(eventId)});
        Event event = null;
        if (cursor.moveToFirst()) {
            event = mapEventFromCursor(cursor);
            event.setMenus(new ArrayList<>(getFoodsForEvent(event.getId())));
        }
        cursor.close();
        return event;
    }

    private Event mapEventFromCursor(Cursor cursor) {
        Event event = new Event();
        event.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        event.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        event.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
        event.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TIME)));
        event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
        event.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
        int organizerIndex = cursor.getColumnIndex("organizer_name");
        if (organizerIndex >= 0) {
            event.setOrganizerName(cursor.getString(organizerIndex));
        }
        int totalRsvpIndex = cursor.getColumnIndex("total_rsvp");
        if (totalRsvpIndex >= 0) {
            event.setTotalRsvp(cursor.getInt(totalRsvpIndex));
        }
        int checkedInIndex = cursor.getColumnIndex("total_checked_in");
        if (checkedInIndex >= 0) {
            event.setTotalCheckedIn(cursor.getInt(checkedInIndex));
        }
        int hasRsvpIndex = cursor.getColumnIndex("has_rsvp");
        if (hasRsvpIndex >= 0) {
            event.setUserHasRsvp(cursor.getInt(hasRsvpIndex) > 0);
        }
        return event;
    }

    private Food mapFoodFromCursor(Cursor cursor) {
        Food food = new Food();
        food.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        food.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FOOD_NAME)));
        food.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FOOD_DESCRIPTION)));
        food.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PRICE)));
        food.setFree(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_FREE)) == 1);
        food.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)));
        food.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URL)));
        food.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_AVAILABLE)) == 1);
        return food;
    }

    public boolean setRsvpStatus(int userId, int eventId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_EVENT_ID, eventId);
        values.put(KEY_STATUS, status);
        values.put(KEY_UPDATED_AT, getDateTime());
        values.put(KEY_CREATED_AT, getDateTime());

        long insertResult = db.insertWithOnConflict(TABLE_RSVPS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        boolean success;
        if (insertResult == -1) {
            ContentValues updateValues = new ContentValues();
            updateValues.put(KEY_STATUS, status);
            updateValues.put(KEY_UPDATED_AT, getDateTime());
            int rows = db.update(TABLE_RSVPS, updateValues,
                    KEY_USER_ID + "=? AND " + KEY_EVENT_ID + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(eventId)});
            success = rows > 0;
        } else {
            success = true;
        }

        if (success && Rsvp.STATUS_CONFIRMED.equals(status)) {
            ensureQrCodeForRsvp(userId, eventId);
        }

        return success;
    }

    public Rsvp getRsvpForEvent(int userId, int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, e." + KEY_TITLE + " AS event_title, e." + KEY_DATE + " AS event_date, e." + KEY_TIME + " AS event_time" +
                " FROM " + TABLE_RSVPS + " r" +
                " JOIN " + TABLE_EVENTS + " e ON e." + KEY_ID + " = r." + KEY_EVENT_ID +
                " WHERE r." + KEY_USER_ID + " = ? AND r." + KEY_EVENT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(eventId)});
        Rsvp rsvp = null;
        if (cursor.moveToFirst()) {
            rsvp = mapRsvpFromCursor(cursor);
        }
        cursor.close();
        return rsvp;
    }

    public List<Rsvp> getRsvpsForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT r.*, e." + KEY_TITLE + " AS event_title, e." + KEY_DATE + " AS event_date, e." + KEY_TIME + " AS event_time" +
                " FROM " + TABLE_RSVPS + " r" +
                " JOIN " + TABLE_EVENTS + " e ON e." + KEY_ID + " = r." + KEY_EVENT_ID +
                " WHERE r." + KEY_USER_ID + " = ?" +
                " ORDER BY e." + KEY_DATE + ", e." + KEY_TIME;
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        List<Rsvp> rsvps = new ArrayList<>();
        while (cursor.moveToNext()) {
            rsvps.add(mapRsvpFromCursor(cursor));
        }
        cursor.close();
        return rsvps;
    }

    private Rsvp mapRsvpFromCursor(Cursor cursor) {
        Rsvp rsvp = new Rsvp();
        rsvp.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        rsvp.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
        rsvp.setEventId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EVENT_ID)));
        rsvp.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));
        int eventTitleIndex = cursor.getColumnIndex("event_title");
        if (eventTitleIndex >= 0) {
            rsvp.setEventTitle(cursor.getString(eventTitleIndex));
        }
        int eventDateIndex = cursor.getColumnIndex("event_date");
        if (eventDateIndex >= 0) {
            rsvp.setEventDate(cursor.getString(eventDateIndex));
        }
        int eventTimeIndex = cursor.getColumnIndex("event_time");
        if (eventTimeIndex >= 0) {
            rsvp.setEventTime(cursor.getString(eventTimeIndex));
        }
        return rsvp;
    }

    public List<QRCode> getQrCodesForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT q.*, e." + KEY_TITLE + " AS event_title, e." + KEY_DATE + " AS event_date, e." + KEY_TIME + " AS event_time" +
                " FROM " + TABLE_QRCODES + " q" +
                " JOIN " + TABLE_EVENTS + " e ON e." + KEY_ID + " = q." + KEY_EVENT_ID +
                " WHERE q." + KEY_USER_ID + " = ?" +
                " ORDER BY e." + KEY_DATE + ", e." + KEY_TIME;
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        List<QRCode> codes = new ArrayList<>();
        while (cursor.moveToNext()) {
            codes.add(mapQrCodeFromCursor(cursor));
        }
        cursor.close();
        return codes;
    }

    public QRCode getOrCreateQrCode(int userId, int eventId) {
        QRCode qrCode = fetchQrCode(userId, eventId);
        if (qrCode == null) {
            ensureQrCodeForRsvp(userId, eventId);
            qrCode = fetchQrCode(userId, eventId);
        }
        return qrCode;
    }

    private QRCode fetchQrCode(int userId, int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT q.*, e." + KEY_TITLE + " AS event_title, e." + KEY_DATE + " AS event_date, e." + KEY_TIME + " AS event_time" +
                " FROM " + TABLE_QRCODES + " q" +
                " JOIN " + TABLE_EVENTS + " e ON e." + KEY_ID + " = q." + KEY_EVENT_ID +
                " WHERE q." + KEY_USER_ID + " = ? AND q." + KEY_EVENT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(eventId)});
        QRCode qrCode = null;
        if (cursor.moveToFirst()) {
            qrCode = mapQrCodeFromCursor(cursor);
        }
        cursor.close();
        return qrCode;
    }

    private QRCode mapQrCodeFromCursor(Cursor cursor) {
        QRCode qrCode = new QRCode();
        qrCode.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        qrCode.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
        qrCode.setEventId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EVENT_ID)));
        qrCode.setCodeText(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CODE_TEXT)));
        int eventTitleIndex = cursor.getColumnIndex("event_title");
        if (eventTitleIndex >= 0) {
            qrCode.setEventTitle(cursor.getString(eventTitleIndex));
        }
        int eventDateIndex = cursor.getColumnIndex("event_date");
        if (eventDateIndex >= 0) {
            qrCode.setEventDate(cursor.getString(eventDateIndex));
        }
        int eventTimeIndex = cursor.getColumnIndex("event_time");
        if (eventTimeIndex >= 0) {
            qrCode.setEventTime(cursor.getString(eventTimeIndex));
        }
        return qrCode;
    }

    public boolean updateParticipantProfile(int userId, String dietaryPreference, String interests,
                                             boolean emailNotif, boolean smsNotif) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DIETARY_PREFERENCES, dietaryPreference);
        values.put(KEY_INTERESTS, interests);
        values.put(KEY_EMAIL_NOTIFICATIONS, emailNotif ? 1 : 0);
        values.put(KEY_SMS_NOTIFICATIONS, smsNotif ? 1 : 0);
        int rows = db.update(TABLE_PARTICIPANTS, values,
                KEY_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    // Helper buat insert participant
    public long insertParticipant(Participant participant) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues userValues = new ContentValues();
        userValues.put(KEY_NAME, participant.getName());
        userValues.put(KEY_EMAIL, participant.getEmail());
        userValues.put(KEY_PASSWORD, participant.getPassword());
        userValues.put(KEY_PHONE_NUMBER, participant.getPhoneNumber());
        userValues.put(KEY_USER_TYPE, "participant");
        userValues.put(KEY_IS_ACTIVE, participant.isActive() ? 1 : 0);

        long userId = db.insert(TABLE_USERS, null, userValues);

        if (userId != -1) {
            ContentValues partValues = new ContentValues();
            partValues.put(KEY_USER_ID, userId);
            partValues.put(KEY_INTERESTS, participant.getInterests());
            partValues.put(KEY_DIETARY_PREFERENCES, participant.getDietaryPreferences());
            partValues.put(KEY_EMERGENCY_CONTACT, participant.getEmergencyContact());
            partValues.put(KEY_EMERGENCY_CONTACT_PHONE, participant.getEmergencyContactPhone());
            partValues.put(KEY_TOTAL_EVENTS_ATTENDED, participant.getTotalEventsAttended());
            partValues.put(KEY_TOTAL_RSVPS, participant.getTotalRsvps());
            partValues.put(KEY_ATTENDANCE_RATE, participant.getAttendanceRate());
            partValues.put(KEY_PREFERRED_EVENT_TYPES, participant.getPreferredEventTypes());
            partValues.put(KEY_EMAIL_NOTIFICATIONS, participant.isEmailNotifications() ? 1 : 0);
            partValues.put(KEY_SMS_NOTIFICATIONS, participant.isSmsNotifications() ? 1 : 0);

            db.insert(TABLE_PARTICIPANTS, null, partValues);
        }

        return userId;
    }

    // Helper buat insert Organizer
    public long insertOrganizer(Organizer organizer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues userValues = new ContentValues();
        userValues.put(KEY_NAME, organizer.getName());
        userValues.put(KEY_EMAIL, organizer.getEmail());
        userValues.put(KEY_PASSWORD, organizer.getPassword());
        userValues.put(KEY_PHONE_NUMBER, organizer.getPhoneNumber());
        userValues.put(KEY_USER_TYPE, "organizer");
        userValues.put(KEY_IS_ACTIVE, organizer.isActive() ? 1 : 0);

        long userId = db.insert(TABLE_USERS, null, userValues);

        if (userId != -1) {
            ContentValues orgValues = new ContentValues();
            orgValues.put(KEY_USER_ID, userId);
            orgValues.put(KEY_ORGANIZATION_NAME, organizer.getOrganizationName());
            orgValues.put(KEY_ORGANIZATION_TYPE, organizer.getOrganizationType());
            orgValues.put(KEY_ORGANIZATION_ADDRESS, organizer.getOrganizationAddress());
            orgValues.put(KEY_ORGANIZATION_WEBSITE, organizer.getOrganizationWebsite());
            orgValues.put(KEY_DESIGNATION, organizer.getDesignation());
            orgValues.put(KEY_DEPARTMENT, organizer.getDepartment());
            orgValues.put(KEY_TOTAL_EVENTS_CREATED, organizer.getTotalEventsCreated());
            orgValues.put(KEY_TOTAL_EVENTS_COMPLETED, organizer.getTotalEventsCompleted());
            orgValues.put(KEY_TOTAL_ATTENDEES_MANAGED, organizer.getTotalAttendeesManaged());
            orgValues.put(KEY_AVERAGE_EVENT_RATING, organizer.getAverageEventRating());
            orgValues.put(KEY_IS_VERIFIED, organizer.isVerified() ? 1 : 0);
            orgValues.put(KEY_VERIFICATION_DATE, organizer.getVerificationDate());
            orgValues.put(KEY_BANK_ACCOUNT_DETAILS, organizer.getBankAccountDetails());
            orgValues.put(KEY_TAX_ID, organizer.getTaxId());
            orgValues.put(KEY_EVENT_BUDGET_LIMIT, organizer.getEventBudgetLimit());
            orgValues.put(KEY_SPECIALIZATIONS, organizer.getSpecializations());

            db.insert(TABLE_ORGANIZERS, null, orgValues);
        }

        return userId;
    }


    // Helper buat get participant by id
    public Participant getParticipantById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " u JOIN " + TABLE_PARTICIPANTS + " p ON u." + KEY_ID + " = p." + KEY_USER_ID + " WHERE u." + KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            Participant participant = new Participant();
            participant.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            participant.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
            participant.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
            participant.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)));
            participant.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE_NUMBER)));
            participant.setIsActive(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ACTIVE)) == 1);
            participant.setInterests(cursor.getString(cursor.getColumnIndexOrThrow(KEY_INTERESTS)));
            participant.setDietaryPreferences(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DIETARY_PREFERENCES)));
            participant.setEmergencyContact(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_CONTACT)));
            participant.setEmergencyContactPhone(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMERGENCY_CONTACT_PHONE)));
            participant.setTotalEventsAttended(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_EVENTS_ATTENDED)));
            participant.setTotalRsvps(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_RSVPS)));
            participant.setAttendanceRate(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_ATTENDANCE_RATE)));
            participant.setPreferredEventTypes(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PREFERRED_EVENT_TYPES)));
            participant.setEmailNotifications(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EMAIL_NOTIFICATIONS)) == 1);
            participant.setSmsNotifications(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SMS_NOTIFICATIONS)) == 1);

            cursor.close();
            return participant;
        }

        cursor.close();
        return null;
    }

    // Helper buat get Organizer by id
    public Organizer getOrganizerById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " u JOIN " + TABLE_ORGANIZERS + " o ON u." + KEY_ID + " = o." + KEY_USER_ID + " WHERE u." + KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            Organizer organizer = new Organizer();
            organizer.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            organizer.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
            organizer.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)));
            organizer.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD)));
            organizer.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE_NUMBER)));
            organizer.setIsActive(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_ACTIVE)) == 1);
            organizer.setOrganizationName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ORGANIZATION_NAME)));
            organizer.setOrganizationType(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ORGANIZATION_TYPE)));
            organizer.setOrganizationAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ORGANIZATION_ADDRESS)));
            organizer.setOrganizationWebsite(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ORGANIZATION_WEBSITE)));
            organizer.setDesignation(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESIGNATION)));
            organizer.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DEPARTMENT)));
            organizer.setTotalEventsCreated(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_EVENTS_CREATED)));
            organizer.setTotalEventsCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_EVENTS_COMPLETED)));
            organizer.setTotalAttendeesManaged(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TOTAL_ATTENDEES_MANAGED)));
            organizer.setAverageEventRating(cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_AVERAGE_EVENT_RATING)));
            organizer.setVerified(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_VERIFIED)) == 1);
            organizer.setVerificationDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_VERIFICATION_DATE)));
            organizer.setBankAccountDetails(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BANK_ACCOUNT_DETAILS)));
            organizer.setTaxId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TAX_ID)));
            organizer.setEventBudgetLimit(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_EVENT_BUDGET_LIMIT)));
            organizer.setSpecializations(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SPECIALIZATIONS)));

            cursor.close();
            return organizer;
        }

        cursor.close();
        return null;
    }

    private synchronized void ensureSeedData() {
        SQLiteDatabase db = getWritableDatabase();
        if (isTableEmpty(db, TABLE_USERS)) {
            insertDefaultUsers(db);
            insertSampleFood(db);
        }

        long organizerId = getFirstUserIdByType(db, "organizer");
        long participantId = getFirstUserIdByType(db, "participant");
        if (organizerId <= 0) {
            insertDefaultUsers(db);
            organizerId = getFirstUserIdByType(db, "organizer");
            participantId = getFirstUserIdByType(db, "participant");
        }

        if (organizerId > 0 && !hasUpcomingEvents(db)) {
            insertSampleEvents(db, organizerId, participantId);
        }
    }

    private boolean isTableEmpty(SQLiteDatabase db, String tableName) {
        return DatabaseUtils.queryNumEntries(db, tableName) == 0;
    }

    private long getFirstUserIdByType(SQLiteDatabase db, String userType) {
        Cursor cursor = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_USERS +
                " WHERE " + KEY_USER_TYPE + " = ? LIMIT 1", new String[]{userType});
        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }

    private boolean hasUpcomingEvents(SQLiteDatabase db) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date now = new Date();
        String today = dateFormat.format(now);
        String currentTime = timeFormat.format(now);
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_EVENTS +
                        " WHERE " + KEY_DATE + " > ? OR (" + KEY_DATE + " = ? AND " + KEY_TIME + " >= ?)",
                new String[]{today, today, currentTime});
        boolean hasUpcoming = false;
        if (cursor.moveToFirst()) {
            hasUpcoming = cursor.getInt(0) > 0;
        }
        cursor.close();
        return hasUpcoming;
    }


    // Helper buat get events by organizer
    public List<Event> getEventsByOrganizer(int organizerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS +
                " WHERE " + KEY_CREATED_BY + " = ? ORDER BY " + KEY_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(organizerId)});
        List<Event> events = new ArrayList<>();
        while (cursor.moveToNext()) {
            events.add(mapEventFromCursor(cursor));
        }
        cursor.close();
        return events;
    }

    // Helper buat organizer update event mereka
    public boolean updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, event.getTitle());
        values.put(KEY_DATE, event.getDate());
        values.put(KEY_TIME, event.getTime());
        values.put(KEY_DESCRIPTION, event.getDescription());
        values.put(KEY_UPDATED_AT, getDateTime());

        int rows = db.update(TABLE_EVENTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(event.getId())});
        return rows > 0;
    }

    // Helper buat organizer delete event kalau perlu
    public boolean deleteEvent(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RSVPS, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});
        db.delete(TABLE_QRCODES, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});
        db.delete(TABLE_ATTENDANCE, KEY_EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});

        int rows = db.delete(TABLE_EVENTS, KEY_ID + " = ?", new String[]{String.valueOf(eventId)});
        return rows > 0;
    }

    public String checkInUser(int userId, int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_RSVPS, null,
                KEY_USER_ID + "=? AND " + KEY_EVENT_ID + "=? AND " + KEY_STATUS + "=?",
                new String[]{String.valueOf(userId), String.valueOf(eventId), Rsvp.STATUS_CONFIRMED},
                null, null, null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            return "No confirmed RSVP found for this user.";
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_EVENT_ID, eventId);
        values.put(KEY_CHECKED_IN, 1);
        values.put(KEY_CHECK_IN_TIME, getDateTime());

        try {
            long result = db.insertWithOnConflict(TABLE_ATTENDANCE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (result != -1) return "Success: Check-in Confirmed!";
        } catch (Exception e) {
            return "Error updating DB";
        }
        return "Check-in Failed";
    }

    public List<Attendance> getEventAttendanceList(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a.*, u." + KEY_NAME + " as user_name " +
                "FROM " + TABLE_ATTENDANCE + " a " +
                "JOIN " + TABLE_USERS + " u ON a." + KEY_USER_ID + " = u." + KEY_ID + " " +
                "WHERE a." + KEY_EVENT_ID + " = ? AND a." + KEY_CHECKED_IN + " = 1";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(eventId)});
        List<Attendance> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Attendance att = new Attendance();
            att.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
            att.setCheckInTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CHECK_IN_TIME)));
            att.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("user_name")));
            list.add(att);
        }
        cursor.close();
        return list;
    }

    public Cursor getAllEventsForSpinner() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_ID + ", " + KEY_TITLE + " FROM " + TABLE_EVENTS + " ORDER BY " + KEY_DATE + " DESC";
        return db.rawQuery(query, null);
    }

    public int[] getEventStats(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int rsvpCount = 0;
        int checkedInCount = 0;

        Cursor cursorRSVP = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RSVPS +
                        " WHERE " + KEY_EVENT_ID + " = ? AND " + KEY_STATUS + " = ?",
                new String[]{String.valueOf(eventId), Rsvp.STATUS_CONFIRMED});

        if (cursorRSVP.moveToFirst()) {
            rsvpCount = cursorRSVP.getInt(0);
        }
        cursorRSVP.close();

        Cursor cursorCheckIn = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ATTENDANCE +
                        " WHERE " + KEY_EVENT_ID + " = ? AND " + KEY_CHECKED_IN + " = 1",
                new String[]{String.valueOf(eventId)});

        if (cursorCheckIn.moveToFirst()) {
            checkedInCount = cursorCheckIn.getInt(0);
        }
        cursorCheckIn.close();

        return new int[]{rsvpCount, checkedInCount};
    }


    public String getCheckedInAttendeesString(int eventId) {
        List<Attendance> attendees = getEventAttendanceList(eventId);

        if (attendees == null || attendees.isEmpty()) {
            return "Belum ada peserta yang melakukan check-in.";
        }

        StringBuilder sb = new StringBuilder();
        int counter = 1;
        for (Attendance att : attendees) {
            sb.append(counter++)
                    .append(". ")
                    .append(att.getUserName())
                    .append("\n   Waktu: ")
                    .append(att.getCheckInTime())
                    .append("\n\n");
        }
        return sb.toString();
    }


    public long insertFood(Food food) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FOOD_NAME, food.getName());
        values.put(KEY_FOOD_DESCRIPTION, food.getDescription());
        values.put(KEY_PRICE, food.getPrice());
        values.put(KEY_IS_FREE, food.isFree() ? 1 : 0);
        values.put(KEY_CATEGORY, food.getCategory());
        values.put(KEY_IMAGE_URL, food.getImageUrl());
        values.put(KEY_IS_AVAILABLE, food.isAvailable() ? 1 : 0);
        return db.insert(TABLE_FOOD, null, values);
    }

    public Food getFoodById(int foodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOOD, null, KEY_ID + "=?",
                new String[]{String.valueOf(foodId)}, null, null, null);
        Food food = null;
        if (cursor.moveToFirst()) {
            food = mapFoodFromCursor(cursor);
        }
        cursor.close();
        return food;
    }

    public List<Food> getFoodsForEvent(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT f.* FROM " + TABLE_FOOD + " f " +
                "INNER JOIN " + TABLE_EVENT_FOOD + " ef ON ef." + KEY_FOOD_ID + " = f." + KEY_ID + " " +
                "WHERE ef." + KEY_EVENT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(eventId)});
        List<Food> foods = new ArrayList<>();
        while (cursor.moveToNext()) {
            foods.add(mapFoodFromCursor(cursor));
        }
        cursor.close();
        return foods;
    }

    public void replaceEventFoods(int eventId, List<Food> foods) {
        if (eventId <= 0) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_EVENT_FOOD, KEY_EVENT_ID + "=?", new String[]{String.valueOf(eventId)});
            if (foods != null) {
                for (Food food : foods) {
                    if (food == null || food.getId() <= 0) {
                        continue;
                    }
                    ContentValues values = new ContentValues();
                    values.put(KEY_EVENT_ID, eventId);
                    values.put(KEY_FOOD_ID, food.getId());
                    db.insert(TABLE_EVENT_FOOD, null, values);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    public long createNewEvent(long organizerId, String title, String date, String time, String description) {
        return insertEvent(this.getWritableDatabase(), organizerId, title, date, time, description);
    }






}
