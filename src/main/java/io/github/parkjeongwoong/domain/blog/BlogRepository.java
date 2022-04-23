package io.github.parkjeongwoong.domain.blog;

import io.github.parkjeongwoong.web.dto.PageVisitorsListResponseDtoInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogRepository extends JpaRepository<Visitors, Long> {

    @Query("SELECT v FROM Visitors v ORDER BY v.id DESC")
    List<Visitors> findAllDesc();

    @Query(value = "SELECT url, COUNT(1) as count FROM Visitors GROUP BY URL", nativeQuery = true)
    List<PageVisitorsListResponseDtoInterface> countVisitors_page();

    @Query(value = "SELECT url, COUNT(1) as count FROM Visitors WHERE just_visited = True GROUP BY URL", nativeQuery = true)
    List<PageVisitorsListResponseDtoInterface> countVisitors_firstPage();

}
