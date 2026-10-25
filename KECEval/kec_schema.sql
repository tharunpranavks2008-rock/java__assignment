-- ====================================================================
-- KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024
-- Student Academic & Continuous Evaluation System (KEC-R2024)
-- Database Schema Definition & Sample Data Script
-- ====================================================================

DROP DATABASE IF EXISTS kec_eval_db;
CREATE DATABASE kec_eval_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE kec_eval_db;

-- --------------------------------------------------------------------
-- 1. Students Table
-- --------------------------------------------------------------------
CREATE TABLE students (
    roll_number VARCHAR(15) NOT NULL,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(20) NOT NULL,
    current_semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_students PRIMARY KEY (roll_number),
    CONSTRAINT chk_students_sem CHECK (current_semester BETWEEN 1 AND 8)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 2. Courses Table
-- --------------------------------------------------------------------
CREATE TABLE courses (
    course_code VARCHAR(15) NOT NULL,
    title VARCHAR(150) NOT NULL,
    credits DECIMAL(3, 1) NOT NULL,
    course_type ENUM('THEORY', 'PRACTICAL', 'EMBEDDED') NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_courses PRIMARY KEY (course_code),
    CONSTRAINT chk_courses_credits CHECK (credits > 0.0),
    CONSTRAINT chk_courses_sem CHECK (semester BETWEEN 1 AND 8)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 3. Enrollments Table (Attendance Tracking & Condonation)
-- --------------------------------------------------------------------
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT NOT NULL,
    roll_number VARCHAR(15) NOT NULL,
    course_code VARCHAR(15) NOT NULL,
    semester INT NOT NULL,
    classes_held INT DEFAULT 0,
    classes_attended INT DEFAULT 0,
    medical_condonation BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_enrollments PRIMARY KEY (enrollment_id),
    CONSTRAINT fk_enroll_student FOREIGN KEY (roll_number) 
        REFERENCES students (roll_number) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_enroll_course FOREIGN KEY (course_code) 
        REFERENCES courses (course_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uk_student_course_sem UNIQUE (roll_number, course_code, semester),
    CONSTRAINT chk_classes_held CHECK (classes_held >= 0),
    CONSTRAINT chk_classes_attended CHECK (classes_attended >= 0 AND classes_attended <= classes_held)
) ENGINE=InnoDB;

-- --------------------------------------------------------------------
-- 4. Assessment Marks Table (Phased Incremental Evaluation)
-- Phase 1: CIA 1 & CIA 2 (0-100)
-- Phase 2: Assignment / Quiz / Lab Evaluation (0-100)
-- Phase 3: ESE Marks (0-100) or Absent Flag
-- --------------------------------------------------------------------
CREATE TABLE assessment_marks (
    mark_id INT AUTO_INCREMENT NOT NULL,
    enrollment_id INT NOT NULL,
    cia1_marks DECIMAL(5, 2) DEFAULT NULL,
    cia2_marks DECIMAL(5, 2) DEFAULT NULL,
    assignment_marks DECIMAL(5, 2) DEFAULT NULL,
    ese_marks DECIMAL(5, 2) DEFAULT NULL,
    is_absent_ese BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_assessment_marks PRIMARY KEY (mark_id),
    CONSTRAINT fk_mark_enrollment FOREIGN KEY (enrollment_id) 
        REFERENCES enrollments (enrollment_id) ON DELETE CASCADE,
    CONSTRAINT uk_mark_enrollment UNIQUE (enrollment_id),
    CONSTRAINT chk_cia1_range CHECK (cia1_marks IS NULL OR (cia1_marks >= 0.0 AND cia1_marks <= 100.0)),
    CONSTRAINT chk_cia2_range CHECK (cia2_marks IS NULL OR (cia2_marks >= 0.0 AND cia2_marks <= 100.0)),
    CONSTRAINT chk_assignment_range CHECK (assignment_marks IS NULL OR (assignment_marks >= 0.0 AND assignment_marks <= 100.0)),
    CONSTRAINT chk_ese_range CHECK (ese_marks IS NULL OR (ese_marks >= 0.0 AND ese_marks <= 100.0))
) ENGINE=InnoDB;

-- ====================================================================
-- SEED DATA: KONGU ENGINEERING COLLEGE (R2024) REALISTIC ACADEMIC DATA
-- ====================================================================

-- Insert Sample Students
INSERT INTO students (roll_number, name, department, current_semester) VALUES
('24ITR001', 'Aadhavan K', 'IT', 3),
('24ITR002', 'Bavithra S', 'IT', 3),
('24ITR003', 'Chandru M', 'IT', 3),
('24ITR004', 'Dinesh Kumar P', 'IT', 3),
('24ITR005', 'Elango R', 'IT', 3),
('24ITR006', 'Farhana A', 'IT', 3),
('24ITR007', 'Gokulnath V', 'IT', 3),
('24CSR001', 'Hariharan T', 'CSE', 3),
('24CSR002', 'Indhumathi N', 'CSE', 3),
('24CSR003', 'Jeevitha K', 'CSE', 3);

-- Insert Sample Courses (Regulation 2024 - 3rd Semester IT & CSE)
INSERT INTO courses (course_code, title, credits, course_type, semester) VALUES
('24ITT31', 'Data Structures and Algorithms', 3.0, 'THEORY', 3),
('24ITT32', 'Object Oriented Programming with Java', 3.0, 'THEORY', 3),
('24ITI31', 'Digital Principles and System Design', 4.0, 'EMBEDDED', 3),
('24ITL31', 'Data Structures & Java Laboratory', 1.5, 'PRACTICAL', 3),
('24CST31', 'Database Management Systems', 3.0, 'THEORY', 3),
('24CSI31', 'Operating Systems', 4.0, 'EMBEDDED', 3),
('24CSL31', 'Database Systems Laboratory', 1.5, 'PRACTICAL', 3);

-- --------------------------------------------------------------------
-- Enrollments & Marks for 24ITR001 (Outstanding Student - All Pass O / A+)
-- --------------------------------------------------------------------
-- 24ITR001: 24ITT31 (Theory) - 56/60 attended (93.3%)
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (1, '24ITR001', '24ITT31', 3, 60, 56, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (1, 95.0, 92.0, 98.0, 94.0, FALSE);

-- 24ITR001: 24ITT32 (Theory) - 58/60 attended (96.7%)
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (2, '24ITR001', '24ITT32', 3, 60, 58, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (2, 88.0, 90.0, 92.0, 91.0, FALSE);

-- 24ITR001: 24ITI31 (Embedded) - 55/60 attended (91.7%)
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (3, '24ITR001', '24ITI31', 3, 60, 55, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (3, 90.0, 86.0, 95.0, 88.0, FALSE);

-- 24ITR001: 24ITL31 (Practical) - 30/30 attended (100.0%)
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (4, '24ITR001', '24ITL31', 3, 30, 30, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (4, 96.0, 94.0, 98.0, 95.0, FALSE);

-- --------------------------------------------------------------------
-- Enrollments & Marks for 24ITR002 (Good Student - Pass A / B+)
-- --------------------------------------------------------------------
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (5, '24ITR002', '24ITT31', 3, 60, 50, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (5, 75.0, 78.0, 80.0, 76.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (6, '24ITR002', '24ITT32', 3, 60, 52, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (6, 70.0, 72.0, 75.0, 68.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (7, '24ITR002', '24ITI31', 3, 60, 48, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (7, 65.0, 68.0, 72.0, 64.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (8, '24ITR002', '24ITL31', 3, 30, 27, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (8, 85.0, 82.0, 90.0, 84.0, FALSE);

-- --------------------------------------------------------------------
-- Enrollments & Marks for 24ITR003 (Condoned Case - 68.3% Attendance with Medical Approval)
-- --------------------------------------------------------------------
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (9, '24ITR003', '24ITT31', 3, 60, 41, TRUE); -- 68.3% Medical condoned
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (9, 62.0, 65.0, 70.0, 58.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (10, '24ITR003', '24ITT32', 3, 60, 48, FALSE); -- 80.0% Normal
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (10, 58.0, 60.0, 65.0, 55.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (11, '24ITR003', '24ITI31', 3, 60, 42, TRUE); -- 70.0% Medical condoned
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (11, 60.0, 62.0, 65.0, 56.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (12, '24ITR003', '24ITL31', 3, 30, 24, FALSE); -- 80.0%
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (12, 75.0, 78.0, 80.0, 72.0, FALSE);

-- --------------------------------------------------------------------
-- Enrollments & Marks for 24ITR004 (Attendance Defaulter - 55% Attendance -> SA Grade)
-- --------------------------------------------------------------------
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (13, '24ITR004', '24ITT31', 3, 60, 33, FALSE); -- 55% Shortage (<65%) -> SA
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (13, 45.0, 40.0, 50.0, NULL, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (14, '24ITR004', '24ITT32', 3, 60, 32, FALSE); -- 53.3% Shortage (<65%) -> SA
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (14, 40.0, 42.0, 45.0, NULL, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (15, '24ITR004', '24ITI31', 3, 60, 46, FALSE); -- 76.7% Normal
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (15, 60.0, 65.0, 70.0, 62.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (16, '24ITR004', '24ITL31', 3, 30, 25, FALSE); -- 83.3% Normal
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (16, 70.0, 72.0, 75.0, 70.0, FALSE);

-- --------------------------------------------------------------------
-- Enrollments & Marks for 24ITR005 (Uncondoned 68% -> SA Grade)
-- --------------------------------------------------------------------
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (17, '24ITR005', '24ITT31', 3, 60, 41, FALSE); -- 68.3% but NOT condoned -> SA
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (17, 50.0, 52.0, 55.0, NULL, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (18, '24ITR005', '24ITT32', 3, 60, 49, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (18, 55.0, 58.0, 60.0, 52.0, FALSE);

-- --------------------------------------------------------------------
-- Enrollments & Marks for 24ITR006 (Absent in ESE -> AB Grade)
-- --------------------------------------------------------------------
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (19, '24ITR006', '24ITT31', 3, 60, 52, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (19, 72.0, 70.0, 78.0, NULL, TRUE); -- Absent in ESE -> AB

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (20, '24ITR006', '24ITT32', 3, 60, 51, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (20, 65.0, 68.0, 70.0, 60.0, FALSE);

-- --------------------------------------------------------------------
-- Enrollments & Marks for 24ITR007 (Failed ESE Minimum Threshold -> RA Grade)
-- --------------------------------------------------------------------
-- In 24ITT31 (Theory): ESE threshold is 45%. Student gets 38 in ESE. Total might be > 50 but ESE failed -> RA
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (21, '24ITR007', '24ITT31', 3, 60, 50, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (21, 80.0, 85.0, 85.0, 38.0, FALSE); -- ESE < 45 -> RA

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (22, '24ITR007', '24ITT32', 3, 60, 54, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (22, 65.0, 70.0, 72.0, 62.0, FALSE);

-- --------------------------------------------------------------------
-- Enrollments & Marks for CSE Students
-- --------------------------------------------------------------------
INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (23, '24CSR001', '24CST31', 3, 50, 48, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (23, 88.0, 92.0, 90.0, 89.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (24, '24CSR001', '24CSI31', 3, 50, 46, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (24, 82.0, 85.0, 88.0, 84.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (25, '24CSR001', '24CSL31', 3, 30, 29, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (25, 92.0, 90.0, 95.0, 91.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (26, '24CSR002', '24CST31', 3, 50, 45, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (26, 72.0, 75.0, 78.0, 70.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (27, '24CSR002', '24CSI31', 3, 50, 44, FALSE);
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (27, 68.0, 70.0, 72.0, 65.0, FALSE);

INSERT INTO enrollments (enrollment_id, roll_number, course_code, semester, classes_held, classes_attended, medical_condonation)
VALUES (28, '24CSR003', '24CST31', 3, 50, 28, FALSE); -- 56% Shortage -> SA
INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_absent_ese)
VALUES (28, 50.0, 48.0, 52.0, NULL, FALSE);

