import { useState } from "react";
import EntityManager from "./components/EntityManager";
import AssignmentPage from "./pages/AssignmentPage";
import OverviewListPage from "./pages/OverviewListPage";
import { componentApi, employeeApi, accessRightApi } from "./api/api";
import "./styles.css";
export default function App() {
  const [tab, setTab] = useState("employees");
  const tabs = [
    ["employees", "Employees"],
    ["components", "Components"],
    ["rights", "Rights"],
    ["assignment", "Assignment"],
    ["overview-list", "Overview List"],
  ];
  return (
    <main>
      <header>
        <h1>Employee Component Rights</h1>
        <p>Manage employees, application components and permissions.</p>
      </header>
      <nav>
        {tabs.map(([k, l]) => (
          <button
            key={k}
            className={tab === k ? "tab active" : "tab"}
            onClick={() => setTab(k)}
          >
            {l}
          </button>
        ))}
      </nav>
      <div className="panel">
        {tab === "employees" && (
          <EntityManager
            title="Employees"
            api={employeeApi}
            fields={[
              { name: "firstName", label: "First Name" },
              { name: "lastName", label: "Last Name" },
              { name: "email", label: "Email", type: "email" },
              { name: "hireDate", label: "Hire Date", type: "Date" },
            ]}
          />
        )}{" "}
        {tab === "components" && (
          <EntityManager
            title="Components"
            api={componentApi}
            fields={[
              { name: "name", label: "Name" },
              { name: "description", label: "Description", required: false },
            ]}
          />
        )}{" "}
        {tab === "rights" && (
          <EntityManager
            title="Rights"
            api={accessRightApi}
            fields={[
              { name: "name", label: "Name" },
              { name: "description", label: "Description", required: false },
            ]}
          />
        )}{" "}
        {tab === "assignment" && <AssignmentPage />}
        {tab === "overview-list" && <OverviewListPage />}
      </div>
    </main>
  );
}
