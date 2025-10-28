package com.example.finalprojectmp.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.finalprojectmp.models.Organizer;
import com.example.finalprojectmp.models.Participant;
import com.example.finalprojectmp.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "EventManagementDB";
    private static final int DATABASE_VERSION = 3;

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
        String query = "SELECT * FROM \" + TABLE_USERS + \" WHERE \" + KEY_EMAIL + \" = ?";
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
}