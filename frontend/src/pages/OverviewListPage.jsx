import { useEffect, useMemo, useState } from "react";
import { assignmentApi, componentApi, employeeApi } from "../api/api";

export default function OverviewListPage() {
  const [employees, setEmployees] = useState([]);
  const [components, setComponents] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [employeeId, setEmployeeId] = useState("");
  const [componentId, setComponentId] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        setError("");
        const [employeeResponse, componentResponse, assignmentResponse] =
          await Promise.all([
            employeeApi.list(),
            componentApi.list(),
            assignmentApi.list(),
          ]);

        setEmployees(employeeResponse.data);
        setComponents(componentResponse.data);
        setAssignments(assignmentResponse.data);
      } catch (err) {
        console.error("Could not load overview list:", err);
        setError(
          err.response?.data?.message || "Could not load the overview list.",
        );
      }
    };

    load();
  }, []);

  const filteredAssignments = useMemo(() => {
    return assignments.filter((assignment) => {
      const employeeMatches =
        !employeeId || assignment.employeeId === Number(employeeId);
      const componentMatches =
        !componentId || assignment.componentId === Number(componentId);
      return employeeMatches && componentMatches;
    });
  }, [assignments, employeeId, componentId]);

  const clearFilters = () => {
    setEmployeeId("");
    setComponentId("");
  };

  return (
    <section>
      <div>
        <div className="title-row">
          <h2>Overview List</h2>
          <a className="button-link" href={assignmentApi.exportUrl}>
            Export Excel
          </a>
        </div>
        <p>Filter the full assignment list by employee and component.</p>
      </div>

      {error && <div className="error">{error}</div>}

      <div className="filter-row">
        <div className="filter-group">
          <label>Employee</label>
          <select
            value={employeeId}
            onChange={(event) => setEmployeeId(event.target.value)}
          >
            <option value="">All employees</option>
            {employees.map((employee) => (
              <option key={employee.id} value={employee.id}>
                {employee.firstName} {employee.lastName}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label>Component</label>
          <select
            value={componentId}
            onChange={(event) => setComponentId(event.target.value)}
          >
            <option value="">All components</option>
            {components.map((component) => (
              <option key={component.id} value={component.id}>
                {component.name}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <button className="secondary" type="button" onClick={clearFilters}>
            Clear Filters
          </button>
        </div>
      </div>

      <div className="result-summary">
        Showing <strong>{filteredAssignments.length}</strong> of{" "}
        <strong>{assignments.length}</strong> assignments
      </div>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Employee</th>
            <th>Component</th>
            <th>Access Right</th>
          </tr>
        </thead>
        <tbody>
          {filteredAssignments.map((assignment) => (
            <tr key={assignment.id}>
              <td>{assignment.id}</td>
              <td>
                {assignment.employeeFirstName} {assignment.employeeLastName}
              </td>
              <td>{assignment.componentName}</td>
              <td>{assignment.rightName}</td>
            </tr>
          ))}
          {filteredAssignments.length === 0 && (
            <tr>
              <td colSpan="4" className="empty-row">
                No assignments match the selected filters.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </section>
  );
}
