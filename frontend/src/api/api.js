import axios from "axios";
const api = axios.create({ baseURL: "http://localhost:8080/api" });
export const crud = (path) => ({
  list: () => api.get(path),
  create: (d) => api.post(path, d),
  update: (id, d) => api.put(`${path}/${id}`, d),
  remove: (id) => api.delete(`${path}/${id}`),
});
export const employeeApi = crud("/employees");
export const componentApi = crud("/components");
export const accessRightApi = crud("/accessrights");
export const assignmentApi = {
  list: () => api.get("/assignments"),
  create: (d) => api.post("/assignments", d),
  remove: (id) => api.delete(`/assignments/${id}`),
  exportUrl: "http://localhost:8080/api/assignments/export",
};
