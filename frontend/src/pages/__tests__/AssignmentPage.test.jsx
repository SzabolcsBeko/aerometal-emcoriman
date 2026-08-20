import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import AssignmentPage from '../AssignmentPage';
import { accessRightApi, assignmentApi, componentApi, employeeApi } from '../../api/api';

vi.mock('../../api/api', () => ({
  employeeApi: { list: vi.fn() }, componentApi: { list: vi.fn() },
  accessRightApi: { list: vi.fn() },
  assignmentApi: { list: vi.fn(), create: vi.fn(), remove: vi.fn(), exportUrl: 'export' },
}));

const employee = { id: 1, firstName: 'John', lastName: 'Smith' };
const component = { id: 10, name: 'Engine' };
const right = { id: 20, name: 'READ' };
const assignment = { id: 100, employeeFirstName: 'John', employeeLastName: 'Smith', componentName: 'Engine', rightName: 'READ' };

describe('AssignmentPage', () => {
  beforeEach(() => {
    employeeApi.list.mockResolvedValue({ data: [employee] });
    componentApi.list.mockResolvedValue({ data: [component] });
    accessRightApi.list.mockResolvedValue({ data: [right] });
    assignmentApi.list.mockResolvedValue({ data: [assignment] });
    assignmentApi.create.mockResolvedValue({ data: {} });
    assignmentApi.remove.mockResolvedValue({ data: {} });
  });

  it('loads selection values and existing assignments', async () => {
    render(<AssignmentPage />);
    expect(await screen.findByRole('option', { name: /John\s+Smith/ })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'Engine' })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'READ' })).toBeInTheDocument();
    expect(screen.getByText('John Smith')).toBeInTheDocument();
  });

  it('requires all three selections', async () => {
    const user = userEvent.setup();
    render(<AssignmentPage />);
    await screen.findByRole('option', { name: /John\s+Smith/ });
    await user.click(screen.getByRole('button', { name: 'Save' }));
    expect(screen.getByText('Please select employee, component and right.')).toBeInTheDocument();
    expect(assignmentApi.create).not.toHaveBeenCalled();
  });

  it('creates an assignment with numeric ids', async () => {
    const user = userEvent.setup();
    render(<AssignmentPage />);
    await screen.findByRole('option', { name: /John\s+Smith/ });
    await user.selectOptions(screen.getByLabelText('Employee'), '1');
    await user.selectOptions(screen.getByLabelText('Component'), '10');
    await user.selectOptions(screen.getByLabelText('Right'), '20');
    await user.click(screen.getByRole('button', { name: 'Save' }));
    await waitFor(() => expect(assignmentApi.create).toHaveBeenCalledWith({ employeeId: 1, componentId: 10, accessRightId: 20 }));
  });

  it('shows an API error when save fails', async () => {
    const user = userEvent.setup();
    assignmentApi.create.mockRejectedValue({ response: { data: { message: 'Assignment already exists' } } });
    render(<AssignmentPage />);
    await screen.findByRole('option', { name: /John\s+Smith/ });
    await user.selectOptions(screen.getByLabelText('Employee'), '1');
    await user.selectOptions(screen.getByLabelText('Component'), '10');
    await user.selectOptions(screen.getByLabelText('Right'), '20');
    await user.click(screen.getByRole('button', { name: 'Save' }));
    expect(await screen.findByText('Assignment already exists')).toBeInTheDocument();
  });

  it('deletes an assignment and reloads data', async () => {
    const user = userEvent.setup();
    render(<AssignmentPage />);
    await screen.findByText('John Smith');
    await user.click(screen.getByRole('button', { name: 'Delete' }));
    await waitFor(() => expect(assignmentApi.remove).toHaveBeenCalledWith(100));
    expect(assignmentApi.list).toHaveBeenCalledTimes(2);
  });
});
