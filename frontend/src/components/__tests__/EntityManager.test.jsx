import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import EntityManager from "../EntityManager";

describe("EntityManager", () => {
  let api;
  const fields = [
    { name: "name", label: "Name", readOnlyOnEdit: true },
    { name: "description", label: "Description", required: true },
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    api = {
      list: vi.fn().mockResolvedValue({ data: [] }),
      create: vi.fn().mockResolvedValue({ data: {} }),
      update: vi.fn().mockResolvedValue({ data: {} }),
      remove: vi.fn().mockResolvedValue({ data: {} }),
    };
  });

  it("loads and displays records", async () => {
    api.list.mockResolvedValue({
      data: [{ id: 1, name: "Engine", description: "Engine module" }],
    });
    render(<EntityManager title="Components" api={api} fields={fields} />);
    expect(await screen.findByText("Engine")).toBeInTheDocument();
    expect(screen.getByText("Engine module")).toBeInTheDocument();
    expect(api.list).toHaveBeenCalledOnce();
  });

  it("creates a valid component and reloads the list", async () => {
    const user = userEvent.setup();
    render(<EntityManager title="Components" api={api} fields={fields} />);
    await waitFor(() => expect(api.list).toHaveBeenCalledOnce());

    await user.type(screen.getByLabelText("Name"), "Navigation");
    await user.type(
      screen.getByLabelText("Description"),
      "Navigation component",
    );
    await user.click(screen.getByRole("button", { name: "Add" }));

    await waitFor(() =>
      expect(api.create).toHaveBeenCalledWith({
        name: "Navigation",
        description: "Navigation component",
      }),
    );
    expect(api.list).toHaveBeenCalledTimes(2);
  });

  it("marks Description as required", () => {
    render(<EntityManager title="Components" api={api} fields={fields} />);
    expect(screen.getByLabelText("Description")).toBeRequired();
  });

  it("prevents whitespace-only Description from being saved", async () => {
    const user = userEvent.setup();
    render(<EntityManager title="Components" api={api} fields={fields} />);
    await waitFor(() => expect(api.list).toHaveBeenCalledOnce());

    await user.type(screen.getByLabelText("Name"), "Navigation");
    await user.type(screen.getByLabelText("Description"), "   ");
    // Calling requestSubmit bypasses user-event's browser validity gate so the
    // component's trim-based validation is tested directly.
    screen.getByRole("button", { name: "Add" }).closest("form").noValidate =
      true;
    await user.click(screen.getByRole("button", { name: "Add" }));

    expect(
      await screen.findByText("Description is required"),
    ).toBeInTheDocument();
    expect(api.create).not.toHaveBeenCalled();
  });

  it("edits and updates an existing record", async () => {
    const user = userEvent.setup();
    api.list.mockResolvedValue({
      data: [{ id: 7, name: "Old", description: "Old description" }],
    });
    render(<EntityManager title="Components" api={api} fields={fields} />);
    await screen.findByText("Old description");

    await user.click(screen.getByRole("button", { name: "Edit" }));
    const name = screen.getByLabelText("Name");
    expect(name).toHaveValue("Old");
    expect(name).toHaveAttribute("readonly");
    expect(name).toHaveClass("readonly-field");

    const description = screen.getByLabelText("Description");
    await user.clear(description);
    await user.type(description, "Updated description");
    await user.click(screen.getByRole("button", { name: "Update" }));

    await waitFor(() =>
      expect(api.update).toHaveBeenCalledWith(7, {
        name: "Old",
        description: "Updated description",
      }),
    );
  });

  it("cancels editing without updating", async () => {
    const user = userEvent.setup();
    api.list.mockResolvedValue({
      data: [{ id: 2, name: "Wing", description: "Wing module" }],
    });
    render(<EntityManager title="Components" api={api} fields={fields} />);
    await screen.findByText("Wing module");
    await user.click(screen.getByRole("button", { name: "Edit" }));
    await user.click(screen.getByRole("button", { name: "Cancel" }));
    expect(screen.getByRole("button", { name: "Add" })).toBeInTheDocument();
    expect(api.update).not.toHaveBeenCalled();
  });

  it("deletes after confirmation", async () => {
    const user = userEvent.setup();
    api.list.mockResolvedValue({
      data: [{ id: 3, name: "Radar", description: "Radar module" }],
    });
    vi.spyOn(window, "confirm").mockReturnValue(true);
    render(<EntityManager title="Components" api={api} fields={fields} />);
    await screen.findByText("Radar module");
    await user.click(screen.getByRole("button", { name: "Delete" }));
    expect(window.confirm).toHaveBeenCalledWith("Delete this record?");
    await waitFor(() => expect(api.remove).toHaveBeenCalledWith(3));
  });

  it("does not delete when confirmation is cancelled", async () => {
    const user = userEvent.setup();
    api.list.mockResolvedValue({
      data: [{ id: 4, name: "Cabin", description: "Cabin module" }],
    });
    vi.spyOn(window, "confirm").mockReturnValue(false);
    render(<EntityManager title="Components" api={api} fields={fields} />);
    await screen.findByText("Cabin module");
    await user.click(screen.getByRole("button", { name: "Delete" }));
    expect(api.remove).not.toHaveBeenCalled();
  });

  it("shows backend error when create fails", async () => {
    const user = userEvent.setup();
    api.create.mockRejectedValue({
      response: { data: { message: "Component already exists" } },
    });
    render(<EntityManager title="Components" api={api} fields={fields} />);
    await user.type(screen.getByLabelText("Name"), "Engine");
    await user.type(screen.getByLabelText("Description"), "Duplicate");
    await user.click(screen.getByRole("button", { name: "Add" }));
    expect(
      await screen.findByText("Component already exists"),
    ).toBeInTheDocument();
  });
});
