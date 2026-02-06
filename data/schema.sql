CREATE TABLE IF NOT EXISTS students (
    student_id TEXT PRIMARY KEY,
    full_name TEXT NOT NULL,
    programme TEXT NOT NULL,
    level INTEGER NOT NULL,
    gpa REAL NOT NULL,
    email TEXT NOT NULL,
    phone_number TEXT NOT NULL,
    date_added TEXT NOT NULL,
    status TEXT NOT NULL
);