package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.entity.Visitor;
import io.github.parkjeongwoong.application.blog.dto.PageVisitorListResponseDtoInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    List<Visitor> findAllByOrderByIdDesc();

    @Query("SELECT url as url, COUNT(1) as count FROM Visitor GROUP BY URL ORDER BY 2 DESC")
    List<PageVisitorListResponseDtoInterface> countVisitor_page();

    @Query(value = "SELECT url as url, COUNT(1) as count FROM visitor WHERE just_visited = True GROUP BY URL ORDER BY 2 DESC", nativeQuery = true)
    List<PageVisitorListResponseDtoInterface> countVisitor_firstPage();

}
