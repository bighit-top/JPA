package springdatajpa.datajpa_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springdatajpa.datajpa_1.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
