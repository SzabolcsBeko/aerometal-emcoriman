# Employee Component Right Manager

Full-stack example with Spring Boot + MySQL backend and React + Vite frontend.

## Model
- Employee(id, name, email)
- Component(id, name, description)
- AccessRight(id, name, description), persisted in `access_right` because RIGHT is a SQL keyword
- AccessAssignment(id, employee, component, right)
- Unique DB constraint: `(employee_id, component_id, right_id)`

## Run
1. Install Java 21, Maven, Node.js and MySQL.
2. MySQL credentials default to `root` / `root`; edit `backend/src/main/resources/application.yml` if needed.
3. Backend:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
4. Frontend:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
5. Open http://localhost:5173

## Excel
The Overview tab has an `Export Excel` button. Endpoint: `GET /api/assignments/export`.

## API
- `/api/employees` GET/POST, `/api/employees/{id}` PUT/DELETE
- `/api/components` GET/POST, `/api/components/{id}` PUT/DELETE
- `/api/rights` GET/POST, `/api/rights/{id}` PUT/DELETE
- `/api/assignments` GET/POST, `/api/assignments/{id}` DELETE
- `/api/assignments/export` GET
