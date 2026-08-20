import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import OverviewListPage from '../OverviewListPage';
import { assignmentApi, componentApi, employeeApi } from '../../api/api';

vi.mock('../../api/api', () => ({
  employeeApi: { list: vi.fn() }, componentApi: { list: vi.fn() },
  assignmentApi: { list: vi.fn(), exportUrl: 'http://localhost:8080/api/assignments/export' },
}));

const assignments = [
  { id: 1, employeeId: 1, employeeFirstName: 'John', employeeLastName: 'Smith', componentId: 10, componentName: 'Engine', rightName: 'READ' },
  { id: 2, employeeId: 2, employeeFirstName: 'Anna', employeeLastName: 'Brown', componentId: 11, componentName: 'Radar', rightName: 'WRITE' },
];

describe('OverviewListPage', () => {
  beforeEach(() => {
    employeeApi.list.mockResolvedValue({ data: [{ id: 1, firstName: 'John', lastName: 'Smith' }, { id: 2, firstName: 'Anna', lastName: 'Brown' }] });
    componentApi.list.mockResolvedValue({ data: [{ id: 10, name: 'Engine' }, { id: 11, name: 'Radar' }] });
    assignmentApi.list.mockResolvedValue({ data: assignments });
  });

  it('shows all assignments initially', async () => {
    render(<OverviewListPage />);
    expect(await screen.findByText('John Smith')).toBeInTheDocument();
    expect(screen.getByText('Anna Brown')).toBeInTheDocument();
    expect(screen.getByText(/Showing/).parentElement).toHaveTextContent('Showing 2 of 2 assignments');
  });

  it('filters assignments by employee', async () => {
    const user = userEvent.setup();
    render(<OverviewListPage />);
    await screen.findByText('John Smith');
    await user.selectOptions(screen.getByLabelText('Employee'), '1');
    expect(screen.getByText('John Smith')).toBeInTheDocument();
    expect(screen.queryByText('Anna Brown')).not.toBeInTheDocument();
  });

  it('filters assignments by component', async () => {
    const user = userEvent.setup();
    render(<OverviewListPage />);
    await screen.findByText('John Smith');
    await user.selectOptions(screen.getByLabelText('Component'), '11');
    expect(screen.getByText('Anna Brown')).toBeInTheDocument();
    expect(screen.queryByText('John Smith')).not.toBeInTheDocument();
  });

  it('clears active filters', async () => {
    const user = userEvent.setup();
    render(<OverviewListPage />);
    await screen.findByText('John Smith');
    await user.selectOptions(screen.getByLabelText('Employee'), '1');
    await user.click(screen.getByRole('button', { name: 'Clear Filters' }));
    expect(screen.getByText('Anna Brown')).toBeInTheDocument();
  });

  it('shows empty-state message when no assignment matches', async () => {
    const user = userEvent.setup();
    render(<OverviewListPage />);
    await screen.findByText('John Smith');
    await user.selectOptions(screen.getByLabelText('Employee'), '1');
    await user.selectOptions(screen.getByLabelText('Component'), '11');
    expect(screen.getByText('No assignments match the selected filters.')).toBeInTheDocument();
  });

  it('shows load errors', async () => {
    employeeApi.list.mockRejectedValue({ response: { data: { message: 'Backend unavailable' } } });
    render(<OverviewListPage />);
    expect(await screen.findByText('Backend unavailable')).toBeInTheDocument();
  });

  it('provides the Excel export link', async () => {
    render(<OverviewListPage />);
    await screen.findByText('John Smith');
    expect(screen.getByRole('link', { name: 'Export Excel' })).toHaveAttribute('href', 'http://localhost:8080/api/assignments/export');
  });
});
