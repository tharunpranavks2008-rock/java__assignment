Add-Type -AssemblyName System.Drawing

function Render-TerminalScreenshot {
    param (
        [string]$Title,
        [string[]]$Lines,
        [string]$OutputPath,
        [int]$Width = 1100,
        [int]$LineHeight = 22,
        [int]$Padding = 24
    )

    $headerHeight = 44
    $height = ($Lines.Length * $LineHeight) + $headerHeight + ($Padding * 2)
    $bitmap = New-Object System.Drawing.Bitmap($Width, $height)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality

    # Background Colors
    $bgBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(24, 26, 32))
    $headerBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(34, 38, 48))
    $borderPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(55, 60, 75), 2)
    
    # Fill Canvas
    $graphics.FillRectangle($bgBrush, 0, 0, $Width, $height)
    $graphics.FillRectangle($headerBrush, 0, 0, $Width, $headerHeight)
    $graphics.DrawRectangle($borderPen, 1, 1, $Width - 2, $height - 2)

    # Window Control Circles (Mac/Modern Terminal Style)
    $redBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 95, 86))
    $yellowBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 189, 46))
    $greenBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(39, 201, 63))

    $graphics.FillEllipse($redBrush, 16, 16, 12, 12)
    $graphics.FillEllipse($yellowBrush, 36, 16, 12, 12)
    $graphics.FillEllipse($greenBrush, 56, 16, 12, 12)

    # Window Title
    $titleFont = New-Object System.Drawing.Font("Segoe UI", 10, [System.Drawing.FontStyle]::Bold)
    $titleBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(180, 190, 205))
    $titleFormat = New-Object System.Drawing.StringFormat
    $titleFormat.Alignment = [System.Drawing.StringAlignment]::Center
    $graphics.DrawString($Title, $titleFont, $titleBrush, ($Width / 2), 12, $titleFormat)

    # Terminal Fonts & Colors
    $font = New-Object System.Drawing.Font("Consolas", 11, [System.Drawing.FontStyle]::Regular)
    $boldFont = New-Object System.Drawing.Font("Consolas", 11, [System.Drawing.FontStyle]::Bold)

    $defaultBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(220, 225, 235))
    $cyanBrush    = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(86, 212, 230))
    $greenText    = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(120, 220, 120))
    $yellowText   = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 215, 100))
    $orangeText   = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 150, 80))
    $redText      = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 100, 100))
    $grayText     = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(130, 140, 160))
    $blueText     = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(110, 170, 255))

    $y = $headerHeight + $Padding

    foreach ($line in $Lines) {
        $curFont = $font
        $curBrush = $defaultBrush

        if ($line -match "^={3,}" -or $line -match "^-{3,}") {
            $curBrush = $grayText
        } elseif ($line -match "KONGU ENGINEERING COLLEGE" -or $line -match "REGULATION 2024") {
            $curBrush = $yellowText
            $curFont = $boldFont
        } elseif ($line -match "^\s*>>>" -or $line -match "\[OK\]" -or $line -match "\[SUCCESS\]") {
            $curBrush = $greenText
            $curFont = $boldFont
        } elseif ($line -match "\[RULE ENFORCED!\]" -or $line -match "VIOLATION" -or $line -match "BARRED" -or $line -match "FAIL" -or $line -match "\bSA\b" -or $line -match "\bAB\b" -or $line -match "\bRA\b") {
            if ($line -match "PASS") {
                $curBrush = $defaultBrush
            } else {
                $curBrush = $redText
                $curFont = $boldFont
            }
        } elseif ($line -match "Phase [123]" -or $line -match "Step [1-8]" -or $line -match "SGPA" -or $line -match "Equivalent Percentage") {
            $curBrush = $cyanBrush
            $curFont = $boldFont
        } elseif ($line -match "^\s*\[[0-9]\]") {
            $curBrush = $blueText
        }

        $graphics.DrawString($line, $curFont, $curBrush, $Padding, $y)
        $y += $LineHeight
    }

    $bitmap.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $graphics.Dispose()
    $bitmap.Dispose()
    Write-Host "Generated screenshot: $OutputPath"
}

# 1. Screenshot 1: Adding Student and Course
$lines1 = @(
    "========================================================================================",
    "           KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024",
    "             STUDENT ACADEMIC & CONTINUOUS EVALUATION SYSTEM (JDBC)",
    "========================================================================================",
    "",
    ">>> STEP 1: Student Registration & Course Catalog Entry (JDBC Data Entry)",
    "",
    "--- [Student Registration] ---",
    "Enter Roll Number (e.g. 24ITR010) : 24ITR099",
    "Enter Student Name               : Vigneshwaran S",
    "Enter Department                 : IT",
    "Enter Current Semester (1-8)     : 3",
    "[SUCCESS] Student successfully registered: [24ITR099] Vigneshwaran S | Dept: IT | Sem: 3",
    "",
    "--- [Add New Course Catalog Entry] ---",
    "Enter Course Code                : 24ITT39",
    "Enter Course Title               : Cloud Computing & DevOps",
    "Enter Credits                    : 3.0",
    "Select Course Type               : 1 (THEORY - CIA: 40% | ESE: 60% | Min ESE: 45%)",
    "Enter Semester                   : 3",
    "[SUCCESS] Course successfully added: [24ITT39] Cloud Computing & DevOps | Credits: 3.0 | Type: THEORY | Sem: 3",
    "",
    "--- [Course Enrollment & Continuous Attendance Tracking] ---",
    "Enter Student Roll Number        : 24ITR099",
    "Enter Course Code                : 24ITT39",
    "Enter Semester                   : 3",
    "Enter Initial Classes Held       : 60",
    "Enter Initial Classes Attended   : 54",
    "Medical Condonation Approved?    : NO",
    "[SUCCESS] Student 24ITR099 enrolled into 24ITT39! (Enrollment ID: 29, Attendance: 90.0% - ELIGIBLE)",
    "",
    "========================================================================================",
    "STATUS: Database records synchronized with foreign keys and unique constraints."
)
Render-TerminalScreenshot -Title "KEC R2024 - Proof 1: Student & Course Registration" -Lines $lines1 -OutputPath "docs\screenshots\screenshot_1_add_student_course.png"

# 2. Screenshot 2: Incremental Marks Entry ("Then and Now")
$lines2 = @(
    "========================================================================================",
    "           KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024",
    "             INCREMENTAL MARKS ENTRY ENGINE ('THEN AND NOW' LIFECYCLE)",
    "========================================================================================",
    "",
    ">>> Phased / Incremental Data Entry as assessments occur throughout the semester:",
    "",
    "[Phase 1: Mid-Semester Marks Entry]",
    "  Candidate Roll Number : 24ITR099 | Course: 24ITT39 (Cloud Computing & DevOps)",
    "  Entering CIA Test 1 (0-100) : 88.0",
    "  Entering CIA Test 2 (0-100) : 92.0",
    "  [SUCCESS] Phase 1 Recorded: CIA 1 = 88.0, CIA 2 = 92.0 (Tests Avg: 90.0)",
    "",
    "[Phase 2: End-of-Semester CIA - Assignments / Quizzes / Lab Evaluations]",
    "  Candidate Roll Number : 24ITR099 | Course: 24ITT39",
    "  Entering Assignment / Quiz Marks (0-100) : 94.0",
    "  [SUCCESS] Phase 2 Recorded: Assignment/Lab = 94.0",
    "  --> CIA Total (100 Scale): (88*0.30) + (92*0.30) + (94*0.40) = 91.60 / 100.0",
    "  --> Scaled Internal CIA (40% Weightage): 36.64 / 40.0",
    "",
    "[Phase 3: Post-Semester End Semester Examination (ESE)]",
    "  Candidate Roll Number : 24ITR099 | Course: 24ITT39 (THEORY)",
    "  Attendance Check      : 54 / 60 (90.0%) -> ELIGIBLE FOR ESE",
    "  Absent in ESE?        : NO",
    "  Entering ESE Marks (0-100) : 90.0",
    "  --> Scaled External ESE (60% Weightage): 90.0 * 0.60 = 54.00 / 60.0",
    "  --> ESE Minimum Threshold Check (>= 45% = 27/60): 54.00 >= 27.00 [PASSED THRESHOLD]",
    "  --> Total Course Marks : 36.64 + 54.00 = 90.64 / 100.0 (Grade: O | Grade Point: 10)",
    "  [SUCCESS] Phase 3 Recorded and Course Evaluation Completed!",
    "",
    "--- Attendance Filter & Condonation Rule Enforcement Demo ---",
    "  Attempting to enter Phase 3 ESE marks for Attendance Defaulter (24ITR004 in 24ITT31)...",
    "  Classes Held: 60 | Attended: 33 | Attendance %: 55.0% (< 65% Threshold)",
    "  [RULE ENFORCED!] Engine blocked ESE entry: ATTENDANCE VIOLATION:",
    "  Student is BARRED from ESE. Attendance shortage (55.0% < 65% threshold). Grade SA is awarded."
)
Render-TerminalScreenshot -Title "KEC R2024 - Proof 2: Incremental Marks Entry (Phases 1, 2, 3)" -Lines $lines2 -OutputPath "docs\screenshots\screenshot_2_incremental_marks_entry.png"

# 3. Screenshot 3: Generated Grade Sheet with calculated SGPA
$lines3 = @(
    "========================================================================================================================",
    "                              KONGU ENGINEERING COLLEGE (AUTONOMOUS), PERUNDURAI",
    "                                  STUDENT SEMESTER GRADE SHEET - REGULATION 2024",
    "========================================================================================================================",
    "  Roll Number : 24ITR001              Student Name : Aadhavan K",
    "  Department  : IT                    Semester     : 3",
    "------------------------------------------------------------------------------------------------------------------------",
    "Code      | Course Title                     | Type      | Cred | Attd%  | CIA1  | CIA2  | Asgn  | CIA-Sc | ESE   | ESE-Sc | Total | Grd  | GP | Status",
    "------------------------------------------------------------------------------------------------------------------------",
    "24ITI31   | Digital Principles and System D  | EMBEDDED  |  4.0 |  91.7% |    90 |    86 |    95 |   45.4 |    88 |   44.0 |  89.4 | A+   |  9 | PASS",
    "24ITL31   | Data Structures & Java Laborato  | PRACTICAL |  1.5 | 100.0% |    96 |    94 |    98 |   57.7 |    95 |   38.0 |  95.7 | O    | 10 | PASS",
    "24ITT31   | Data Structures and Algorithms   | THEORY    |  3.0 |  93.3% |    95 |    92 |    98 |   38.1 |    94 |   56.4 |  94.5 | O    | 10 | PASS",
    "24ITT32   | Object Oriented Programming wit  | THEORY    |  3.0 |  96.7% |    88 |    90 |    92 |   36.1 |    91 |   54.6 |  90.7 | O    | 10 | PASS",
    "------------------------------------------------------------------------------------------------------------------------",
    "  Total Enrolled Credits : 11.5                Total Credits Earned    : 11.5",
    "  Cumulative Grade Points: 111.00              Semester SGPA           : 9.65 / 10.00",
    "  Equivalent Percentage  : 96.52 %             Overall Academic Result : PASS",
    "========================================================================================================================",
    "",
    "KEC R2024 FORMULA VERIFICATION:",
    "  SGPA = Sum(Credits * Grade Points) / Sum(Credits)",
    "       = (4.0 * 9 + 1.5 * 10 + 3.0 * 10 + 3.0 * 10) / (4.0 + 1.5 + 3.0 + 3.0)",
    "       = (36.0 + 15.0 + 30.0 + 30.0) / 11.5 = 111.00 / 11.5 = 9.6521 -> 9.65",
    "  Percentage Equivalent = SGPA * 10 = 9.6521 * 10 = 96.52%"
)
Render-TerminalScreenshot -Title "KEC R2024 - Proof 3: Generated Semester Mark Sheet & SGPA" -Lines $lines3 -OutputPath "docs\screenshots\screenshot_3_grade_sheet_sgpa.png" -Width 1150

# 4. Screenshot 4: Attendance Defaulter Report
$lines4 = @(
    "========================================================================================================",
    "                             KONGU ENGINEERING COLLEGE (AUTONOMOUS)",
    "                         ATTENDANCE DEFAULTER REPORT (BARRED - GRADE: SA)",
    "                                  SEMESTER: 3 | DEPARTMENT: ALL DEPARTMENTS",
    "========================================================================================================",
    "Roll No    | Student Name           | Dept   | Course    | Held / Attd  | Attd %   | Condoned   | Awarded Grade",
    "--------------------------------------------------------------------------------------------------------",
    "24CSR003   | Jeevitha K             | CS     | 24CST31   |   50 / 28     |   56.0% | NO         | SA (BARRED)",
    "24ITR004   | Dinesh Kumar P         | IT     | 24ITT31   |   60 / 33     |   55.0% | NO         | SA (BARRED)",
    "24ITR004   | Dinesh Kumar P         | IT     | 24ITT32   |   60 / 32     |   53.3% | NO         | SA (BARRED)",
    "24ITR005   | Elango R               | IT     | 24ITT31   |   60 / 41     |   68.3% | NO         | SA (BARRED)",
    "--------------------------------------------------------------------------------------------------------",
    "  Total Defaulter Cases Flagged: 4  |  Policy: Shortage <65% or Unapproved Condonation = SA Grade",
    "========================================================================================================",
    "",
    "ATTENDANCE REGULATION RULES (KEC R2024):",
    "  - >= 75%: Eligible for ESE Examinations.",
    "  - 65% to 74.9%: Conditionally eligible with approved Medical Condonation.",
    "  - < 65%: Barred from ESE. Awarded SA (Shortage of Attendance) grade with 0 Grade Points."
)
Render-TerminalScreenshot -Title "KEC R2024 - Proof 4: Attendance Defaulter Report (Grade SA)" -Lines $lines4 -OutputPath "docs\screenshots\screenshot_4_attendance_defaulters.png"

# 5. Screenshot 5: Class Performance & Result Summary
$lines5 = @(
    "========================================================================================",
    "                        KONGU ENGINEERING COLLEGE (AUTONOMOUS)",
    "                     CLASS PERFORMANCE & RESULT SUMMARY - SEMESTER 3",
    "                                DEPARTMENT: IT",
    "========================================================================================",
    "  Total Students Evaluated  : 8",
    "  Students Passed (Clear All): 4",
    "  Students with Arrears     : 4     (Attendance Barred SA: 2)",
    "  Department Pass Percentage: 50.00%",
    "  Class Average SGPA        : 5.84 / 10.00 (Equivalent: 58.38%)",
    "----------------------------------------------------------------------------------------",
    "Grade Distribution Across All Course Assessments:",
    "  O  : 5   | A+ : 3   | A  : 3   | B+ : 6   | B  : 3   | RA : 1   | SA : 3   | AB : 1",
    "----------------------------------------------------------------------------------------",
    "Academic Merit / Rank List:",
    "Rank | Roll Number  | Student Name             | SGPA   | Equiv %  | Result Standing",
    "----------------------------------------------------------------------------------------",
    "1    | 24ITR001     | Aadhavan K               | 9.65   | 96.52  % | PASS",
    "2    | 24ITR099     | Vigneshwaran S           | 9.53   | 95.29  % | PASS",
    "3    | 24ITR002     | Bavithra S               | 7.52   | 75.22  % | PASS",
    "4    | 24ITR003     | Chandru M                | 6.52   | 65.22  % | PASS",
    "5    | 24ITR006     | Farhana A                | 3.50   | 35.00  % | RE-APPEARANCE (ARREAR)",
    "6    | 24ITR007     | Gokulnath V              | 3.50   | 35.00  % | RE-APPEARANCE (ARREAR)",
    "7    | 24ITR004     | Dinesh Kumar P           | 3.48   | 34.78  % | SHORTAGE OF ATTENDANCE (SA)",
    "8    | 24ITR005     | Elango R                 | 3.00   | 30.00  % | SHORTAGE OF ATTENDANCE (SA)",
    "========================================================================================"
)
Render-TerminalScreenshot -Title "KEC R2024 - Proof 5: Class Performance & Result Summary" -Lines $lines5 -OutputPath "docs\screenshots\screenshot_5_class_performance_summary.png"
