import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import App from '../App';
import { accessRightApi, assignmentApi, componentApi, employeeApi } from '../api/api';

vi.mock('../api/api', () => ({
  employeeApi: { list: vi.fn(), create: vi.fn(), update: vi.fn(), remove: vi.fn() },
  componentApi: { list: vi.fn(), create: vi.fn(), update: vi.fn(), remove: vi.fn() },
  accessRightApi: { list: vi.fn(), create: vi.fn(), update: vi.fn(), remove: vi.fn() },
  assignmentApi: { list: vi.fn(), create: vi.fn(), remove: vi.fn(), exportUrl: 'http://localhost:8080/api/assignments/export' },
}));

describe('App navigation', () => {
  beforeEach(() => {
    employeeApi.list.mockResolvedValue({ data: [] });
    componentApi.list.mockResolvedValue({ data: [] });
    accessRightApi.list.mockResolvedValue({ data: [] });
    assignmentApi.list.mockResolvedValue({ data: [] });
  });

  it('opens Employees by default', async () => {
    render(<App />);
    expect(screen.getByRole('heading', { name: 'Employees' })).toBeInTheDocument();
  });

  it('switches to Components and requires Description', async () => {
    const user = userEvent.setup();
    render(<App />);
    await user.click(screen.getByRole('button', { name: 'Components' }));
    expect(screen.getByRole('heading', { name: 'Components' })).toBeInTheDocument();
    expect(screen.getByLabelText('Description')).toBeRequired();
  });

  it('keeps Rights Description optional', async () => {
    const user = userEvent.setup();
    render(<App />);
    await user.click(screen.getByRole('button', { name: 'Rights' }));
    expect(screen.getByRole('heading', { name: 'Rights' })).toBeInTheDocument();
    expect(screen.getByLabelText('Description')).not.toBeRequired();
  });

  it('navigates to Assignment', async () => {
    const user = userEvent.setup();
    render(<App />);
    await user.click(screen.getByRole('button', { name: 'Assignment' }));
    expect(screen.getByRole('heading', { name: 'Access Overview' })).toBeInTheDocument();
  });

  it('navigates to Overview List', async () => {
    const user = userEvent.setup();
    render(<App />);
    await user.click(screen.getByRole('button', { name: 'Overview List' }));
    expect(screen.getByRole('heading', { name: 'Overview List' })).toBeInTheDocument();
  });
});
