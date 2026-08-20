import { useEffect, useState } from "react";
export default function EntityManager({ title, api, fields }) {
  const empty = Object.fromEntries(fields.map((f) => [f.name, ""]));
  const [items, setItems] = useState([]),
    [form, setForm] = useState(empty),
    [editing, setEditing] = useState(null),
    [error, setError] = useState("");
  const load = async () => setItems((await api.list()).data);
  useEffect(() => {
    load();
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    setError("");

    const emptyRequiredField = fields.find(
      (field) =>
        field.required !== false &&
        String(form[field.name] ?? "").trim() === "",
    );

    if (emptyRequiredField) {
      setError(`${emptyRequiredField.label} is required`);
      return;
    }

    try {
      editing ? await api.update(editing, form) : await api.create(form);
      setForm(empty);
      setEditing(null);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || "Operation failed");
    }
  };
  const edit = (x) => {
    setEditing(x.id);
    setForm(Object.fromEntries(fields.map((f) => [f.name, x[f.name] ?? ""])));
  };
  const remove = async (id) => {
    if (confirm("Delete this record?")) {
      try {
        await api.remove(id);
        await load();
      } catch (err) {
        setError(err.response?.data?.message || "Delete failed");
      }
    }
  };
  return (
    <section>
      <h2>{title}</h2>
      {error && <div className="error">{error}</div>}
      <form className="form-grid" onSubmit={submit}>
        {fields.map((f) => (
          <label key={f.name}>
            {f.label}
            <input
              type={f.type || "text"}
              value={form[f.name]}
              onChange={(e) => setForm({ ...form, [f.name]: e.target.value })}
              required={f.required !== false}
              readOnly={editing !== null && Boolean(f.readOnlyOnEdit)}
              className={
                editing !== null && f.readOnlyOnEdit ? "readonly-field" : ""
              }
            />
          </label>
        ))}
        <div className="actions">
          <button type="submit">{editing ? "Update" : "Add"}</button>
          {editing && (
            <button
              type="button"
              className="secondary"
              onClick={() => {
                setEditing(null);
                setForm(empty);
              }}
            >
              Cancel
            </button>
          )}
        </div>
      </form>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            {fields.map((f) => (
              <th key={f.name}>{f.label}</th>
            ))}
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {items.map((x) => (
            <tr key={x.id}>
              <td>{x.id}</td>
              {fields.map((f) => (
                <td key={f.name}>{x[f.name]}</td>
              ))}
              <td>
                <button className="small" onClick={() => edit(x)}>
                  Edit
                </button>{" "}
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
