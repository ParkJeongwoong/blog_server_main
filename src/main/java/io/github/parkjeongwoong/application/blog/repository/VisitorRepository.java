package io.github.parkjeongwoong.application.blog.repository;

import io.github.parkjeongwoong.application.blog.dto.*;
import io.github.parkjeongwoong.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    List<Visitor> findAllByOrderByIdDesc();

    @Query("SELECT url as url, COUNT(1) as count FROM Visitor GROUP BY URL ORDER BY 2 DESC")
    List<PageVisitorResponseDtoInterface> countVisitor_page();

    @Query("SELECT url as url, COUNT(1) as count FROM Visitor WHERE just_visited = True GROUP BY URL ORDER BY 2 DESC")
    List<PageVisitorResponseDtoInterface> countVisitor_firstPage();

    @Query("SELECT CAST(created_date AS LocalDate) as date, COUNT(1) as count FROM Visitor GROUP BY CAST(created_date AS LocalDate) ORDER BY 1 DESC")
    List<DailyVisitorResponseDtoInterface> countDailyVisitor();

    @Query("SELECT ip as ip, count(1) as count FROM Visitor GROUP BY IP ORDER BY 2 DESC")
    List<VisitorCountResponseDtoInterface> countVisitor();

    @Query("SELECT ip as ip, count(1) as count FROM Visitor WHERE CREATED_DATE BETWEEN :startDate AND :endDate GROUP BY IP ORDER BY 2 DESC")
    List<VisitorCountResponseDtoInterface> countVisitor_date(@Param("startDate") String startDate, @Param("endDate") String endDate);

    Visitor findTop1ByOrderByIdDesc();

    @Query("SELECT v FROM Visitor v WHERE CREATED_DATE BETWEEN :startDate AND :endDate ORDER BY 1 DESC")
    List<Visitor> getVisitorInHours(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
