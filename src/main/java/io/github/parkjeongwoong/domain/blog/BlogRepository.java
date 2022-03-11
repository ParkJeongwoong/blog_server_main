package io.github.parkjeongwoong.domain.blog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogRepository extends JpaRepository<Visitors, Long> {

    @Query("SELECT v FROM Visitors v ORDER BY v.id DESC")
    List<Visitors> findAllDesc();

}
