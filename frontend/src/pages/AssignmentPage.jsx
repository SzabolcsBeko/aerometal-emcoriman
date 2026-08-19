import { useEffect, useState } from "react";
import {
  accessRightApi,
  assignmentApi,
  componentApi,
  employeeApi,
} from "../api/api";
export default function OverviewPage() {
  const [employees, setEmployees] = useState([]),
    [components, setComponents] = useState([]),
    [accessRights, setAccessRights] = useState([]),
    [rows, setRows] = useState([]),
    [form, setForm] = useState({
      employeeId: "",
      componentId: "",
      accessRightId: "",
    }),
    [error, setError] = useState("");
  const load = async () => {
    const [e, c, r, a] = await Promise.all([
      employeeApi.list(),
      componentApi.list(),
      accessRightApi.list(),
      assignmentApi.list(),
    ]);
    setEmployees(e.data);
    setComponents(c.data);
    setAccessRights(r.data);
    setRows(a.data);
  };
  useEffect(() => {
    load();
  }, []);
  const save = async () => {
    setError("");
    if (!form.employeeId || !form.componentId || !form.accessRightId) {
      setError("Please select employee, component and right.");
      return;
    }
    try {
      await assignmentApi.create({
        employeeId: Number(form.employeeId),
        componentId: Number(form.componentId),
        accessRightId: Number(form.accessRightId),
      });
      setForm({ employeeId: "", componentId: "", accessRightId: "" });
      await load();
    } catch (err) {
      setError(err.response?.data?.message || "Save failed");
    }
  };
  const remove = async (id) => {
    await assignmentApi.remove(id);
    await load();
  };
  return (
    <section>
      <div className="title-row">
        <h2>Access Overview</h2>
      </div>
      <p>Select one value from each list and save the assignment.</p>
      {error && <div className="error">{error}</div>}
      <div className="combo-grid">
        <label>
          Employee
          <select
            value={form.employeeId}
            onChange={(e) => setForm({ ...form, employeeId: e.target.value })}
          >
            <option value="">Choose employee...</option>
            {employees.map((x) => (
              <option key={x.id} value={x.id}>
                {x.firstName + "  " + x.lastName}
              </option>
            ))}
          </select>
        </label>
        <label>
          Component
          <select
            value={form.componentId}
            onChange={(e) => setForm({ ...form, componentId: e.target.value })}
          >
            <option value="">Choose component...</option>
            {components.map((x) => (
              <option key={x.id} value={x.id}>
                {x.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          Right
          <select
            value={form.accessRightId}
            onChange={(e) =>
              setForm({ ...form, accessRightId: e.target.value })
            }
          >
            <option value="">Choose right...</option>
            {accessRights.map((x) => (
              <option key={x.id} value={x.id}>
                {x.name}
              </option>
            ))}
          </select>
        </label>
        <button onClick={save}>Save</button>
      </div>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Employee</th>
            <th>Component</th>
            <th>Right</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((x) => (
            <tr key={x.id}>
              <td>{x.id}</td>
              <td>{x.employeeFirstName + " " + x.employeeLastName}</td>
              <td>{x.componentName}</td>
              <td>{x.rightName}</td>
              <td>
                <button className="small danger" onClick={() => remove(x.id)}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
