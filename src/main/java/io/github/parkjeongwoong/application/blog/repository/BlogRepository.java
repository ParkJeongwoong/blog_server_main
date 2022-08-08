package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.Visitors;
import io.github.parkjeongwoong.application.blog.dto.PageVisitorsListResponseDtoInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogRepository extends JpaRepository<Visitors, Long> {

    @Query("SELECT v FROM Visitors v ORDER BY v.id DESC")
    List<Visitors> findAllDesc();

    @Query("SELECT url as url, COUNT(1) as count FROM Visitors GROUP BY URL ORDER BY 2 DESC")
    List<PageVisitorsListResponseDtoInterface> countVisitors_page();

    @Query(value = "SELECT url as url, COUNT(1) as count FROM visitors WHERE just_visited = True GROUP BY URL ORDER BY 2 DESC", nativeQuery = true)
    List<PageVisitorsListResponseDtoInterface> countVisitors_firstPage();

}
