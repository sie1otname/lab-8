package seg3x02.employeeGql.resolvers

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.resolvers.types.CreateEmployeeInput
import java.util.UUID

@Controller
class EmployeesResolver(private val employeesRepository: EmployeesRepository) {

    @QueryMapping
    fun employees(): List<Employee> = employeesRepository.findAll()

    @QueryMapping
    fun employeeById(@Argument id: String): Employee? =
        employeesRepository.findById(id).orElse(null)

    @MutationMapping
    fun newEmployee(@Argument createEmployeeInput: CreateEmployeeInput): Employee {
        val name = createEmployeeInput.name
        val dateOfBirth = createEmployeeInput.dateOfBirth
        val city = createEmployeeInput.city
        val salary = createEmployeeInput.salary
        if (name.isNullOrBlank() || dateOfBirth.isNullOrBlank() || city.isNullOrBlank() || salary == null) {
            throw IllegalArgumentException("name, dateOfBirth, city and salary are required")
        }
        val employee = Employee(
            name, dateOfBirth, city, salary,
            createEmployeeInput.gender, createEmployeeInput.email
        )
        employee.id = UUID.randomUUID().toString()
        return employeesRepository.save(employee)
    }

    @MutationMapping
    fun deleteEmployee(@Argument id: String): Boolean {
        if (!employeesRepository.existsById(id)) return false
        employeesRepository.deleteById(id)
        return true
    }

    @MutationMapping
    fun updateEmployee(@Argument id: String, @Argument createEmployeeInput: CreateEmployeeInput): Employee {
        val existing = employeesRepository.findById(id)
            .orElseThrow { NoSuchElementException("Employee $id not found") }
        val updated = Employee(
            createEmployeeInput.name ?: existing.name,
            createEmployeeInput.dateOfBirth ?: existing.dateOfBirth,
            createEmployeeInput.city ?: existing.city,
            createEmployeeInput.salary ?: existing.salary,
            createEmployeeInput.gender ?: existing.gender,
            createEmployeeInput.email ?: existing.email
        )
        updated.id = existing.id
        return employeesRepository.save(updated)
    }
}
