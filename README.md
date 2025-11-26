# USA Attendance Management System

A comprehensive institute attendance management system built with Next.js (frontend) and Spring Boot (backend).

## 🚀 Project Overview

This system helps educational institutes manage:

- **Student Management**: Add, edit, and organize students by batches and subjects
- **Attendance Tracking**: Real-time attendance marking with parent notifications
- **Institute Management**: Manage batches (academic years) and subjects
- **Parent Communication**: Send broadcast messages and fee reminders
- **Fee Management**: Track student fees and payment status

## 📋 Features

### Core Functionality

- ✅ **Authentication System** - Secure admin login with JWT tokens
- ✅ **Student Management** - CRUD operations with batch and subject filtering
- ✅ **Attendance System** - Student ID validation and real-time marking
- ✅ **Reporting** - Daily attendance reports with CSV export
- ✅ **Messaging** - Parent notifications for attendance and fee reminders
- ✅ **Fee Tracking** - Payment management and overdue tracking

### User Interface

- 📱 **Responsive Design** - Works on desktop, tablet, and mobile
- 🎨 **Modern UI** - Clean, professional interface with Tailwind CSS
- 🚀 **Fast Performance** - Optimized with Next.js and TypeScript
- 🔒 **Secure** - Protected routes and API authentication

## 🛠️ Tech Stack

### Frontend

- **Next.js 16** - React framework with app router
- **TypeScript** - Type-safe development
- **Tailwind CSS** - Utility-first styling
- **Axios** - API communication
- **Lucide React** - Icon library

### Backend

- **Spring Boot** - Java web framework
- **Spring Security** - Authentication and authorization
- **PostgreSQL** - Database
- **Flyway** - Database migrations
- **JWT** - Token-based authentication

## 🚦 Getting Started

### Prerequisites

- Node.js 18+ and npm
- Java 17+
- PostgreSQL database

### Backend Setup

1. Navigate to backend directory:

   ```bash
   cd backend/backend
   ```

2. Configure database in `application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/attendance_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup

1. Navigate to frontend directory:

   ```bash
   cd frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Configure environment variables in `.env.local`:

   ```env
   NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
   ```

4. Start development server:

   ```bash
   npm run dev
   ```

5. Access the application at `http://localhost:3000`

## 📱 Usage Guide

### Initial Setup

1. **Login**: Use admin credentials to access the dashboard
2. **Create Batches**: Add academic year batches (e.g., 2024, 2025)
3. **Add Subjects**: Create subjects like Chemistry, Physics, Biology
4. **Add Students**: Register students with their batch and subject selections

### Daily Operations

1. **Mark Attendance**:

   - Select batch and subject for the class
   - Students enter their ID codes to mark present
   - Parents receive automatic SMS notifications

2. **View Reports**:

   - Generate daily attendance reports
   - Download CSV files for record keeping
   - View present/absent student lists

3. **Send Messages**:
   - Broadcast important announcements
   - Send automated fee payment reminders
   - Target specific batches or subjects

### Fee Management

1. **Create Fee Records**: Add tuition fees with due dates
2. **Track Payments**: Monitor paid/pending amounts
3. **Send Reminders**: Automated notifications for overdue payments

## 🔧 API Documentation

### Authentication

```
POST /api/auth/login
Body: { "username": "admin", "password": "password" }
Response: { "token": "jwt_token_here" }
```

### Key Endpoints

- `GET /api/admin/students` - List students with filters
- `POST /api/admin/students` - Create new student
- `POST /api/attendance/mark` - Mark attendance
- `GET /api/admin/attendance/report` - Generate reports
- `POST /api/admin/messaging/broadcast` - Send messages

## 📂 Project Structure

```
USA-Attendance-system-NEW-/
├── backend/
│   ├── src/main/java/com/usa/attendancesystem/
│   │   ├── controller/     # REST API controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access layer
│   │   ├── model/          # JPA entities
│   │   └── dto/           # Data transfer objects
│   └── src/main/resources/
│       ├── application.properties
│       └── db/migration/   # Database migrations
│
├── frontend/
│   ├── src/
│   │   ├── app/           # Next.js pages
│   │   ├── components/    # Reusable UI components
│   │   ├── contexts/      # React contexts (auth)
│   │   ├── lib/          # Utilities and API client
│   │   └── types/        # TypeScript interfaces
│   └── public/           # Static assets
```

## 🔒 Security Features

- **JWT Authentication**: Secure token-based login system
- **Protected Routes**: Frontend route protection for admin pages
- **API Security**: Backend endpoints secured with Spring Security
- **Input Validation**: Client and server-side data validation
- **CORS Configuration**: Proper cross-origin request handling

## 🚀 Deployment

### Frontend (Vercel/Netlify)

```bash
npm run build
```

### Backend (Production Server)

```bash
./mvnw clean package
java -jar target/attendancesystem-0.0.1-SNAPSHOT.jar
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:

- Create an issue in the repository
- Contact the development team

## 🎯 Future Enhancements

- [ ] Excel import for bulk student registration
- [ ] Mobile app for student attendance marking
- [ ] Advanced analytics and reporting
- [ ] Email notifications in addition to SMS
- [ ] Multi-language support
- [ ] Offline attendance capability

---

**Built with ❤️ for educational institutes**
