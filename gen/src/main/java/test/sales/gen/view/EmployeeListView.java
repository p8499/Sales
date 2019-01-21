package test.sales.gen.view;

import java.util.List;
import test.sales.gen.bean.Employee;

public interface EmployeeListView {
  void onEmployeeListReloaded(List<Employee> employeeList);

  void onEmployeeListAppended(List<Employee> employeeList);
}