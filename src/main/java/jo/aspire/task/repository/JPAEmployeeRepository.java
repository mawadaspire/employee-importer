package jo.aspire.task.repository;

import jo.aspire.task.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAEmployeeRepository extends JpaRepository<EmployeeEntity,String> {

}
