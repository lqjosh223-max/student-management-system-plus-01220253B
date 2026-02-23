# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-02-23

### Added
- Complete CRUD operations for student records
- Dashboard with statistics (Total, Active, Inactive, Average GPA)
- Search and filter functionality (by ID, name, programme, level, status)
- Sort by GPA and Full Name
- Reports screen with 4 report types:
  - Top Performers (top 10 by GPA)
  - At-Risk Students (GPA below threshold)
  - GPA Distribution (bar chart)
  - Programme Summary (counts and averages)
- Import from CSV with validation and error reporting
- Export to CSV for all reports and student lists
- Settings screen with configurable at-risk threshold
- Database backup functionality
- Logging to data/app.log
- 12 unit tests with Maven integration

### Changed
- Improved UI with professional styling
- Enhanced error handling with user-friendly dialogs
- Optimized database queries with prepared statements

### Fixed
- Duplicate ID rejection during import
- Invalid data validation at service layer
- CSV export with proper escaping

### Technical
- Java: JDK 25
- JavaFX: 21.0.2
- SQLite: 3.45.1.0
- Maven: 3.9.0+
- JUnit: 5.10.2