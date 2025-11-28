# Student Management System

A comprehensive Java-based application for managing student information, courses, marks, fees, and analytics in an educational institution.

## Features

- **User Authentication**: Secure login and signup functionality
- **Student Management**:
  - Add new students with course selection
  - View student details
  - Update student information including course changes
  - Delete student records
- **Course Management**: Manage available courses and subjects
- **Marks Entry**: Record student marks based on their enrolled course subjects
- **Fee Management**: Handle student fee payments and records
- **Analytics Dashboard**: View statistical insights and reports
- **Search and Filter**: Advanced search capabilities for student records
- **Data Export**: Export student data and reports

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Ant (for building the project)
- NetBeans IDE (recommended for development)

## Installation

1. Clone or download the project to your local machine
2. Open the project in NetBeans IDE
3. Ensure the Derby database JAR is in the `lib/` directory
4. Build the project using Ant:
   ```bash
   ant clean
   ant compile
   ant jar
   ```

## Usage

1. Run the application:
   ```bash
   java -jar dist/StudentManagementSystem.jar
   ```
   Or run directly from NetBeans

2. The application will initialize the database connection and launch the login page

3. Use the main dashboard to access various features:
   - Add/View/Update/Delete students
   - Manage courses and subjects
   - Enter student marks
   - Manage fees
   - View analytics

## Database

The application uses Apache Derby (embedded database):
- Database files are located in the `sample/` directory
- **Important**: Do not modify files in `sample/`, `sample/log/`, or `sample/seg0/` directories as they contain the actual database data and recovery files

## Project Structure

- `src/studentmanegmentsystem/`: Main source code
- `build/`: Compiled classes
- `lib/`: Library dependencies (Derby JAR)
- `nbproject/`: NetBeans project files
- `sample/`: Derby database files

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the terms of the [MIT License](LICENSE).

## Recent Updates

- Added course selection when adding students
- Updated marks entry to load subjects based on student's course
- Enabled course changes in student updates
- Enhanced database schema with course relationships
