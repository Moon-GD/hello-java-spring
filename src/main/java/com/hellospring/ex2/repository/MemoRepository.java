package com.hellospring.ex2.repository;

import com.hellospring.ex2.entity.Memo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    // Query Methods
    List<Memo> getMemosByMemoTextContaining(Long number);
    List<Memo> getMemosByMemoTextContaining(Long number, Pageable pageable);

    // @Query
    @Query("SELECT m from Memo m ORDER BY m.mno DESC")
    List<Memo> getMemosByDesc();

    @Query("UPDATE Memo m set m.memoText = :memoText WHERE m.mno = :mno")
    int updateMemoText(@Param("mno") Long mno, @Param("memoText") String memoText);

    @Query("UPDATE Memo m set m.memoText = :#{#memoObj.memoText} WHERE m.mno = :#{#memoObj.mno}")
    int updateMemoTextUsingMemoObj(@Param("memoObj") Memo memo);

    @Query(value = "SELECT m FROM Memo m WHERE m.mno > :mno",
            countQuery = "SELECT count(m) FROM Memo m WHERE m.mno > :mno")
    Page<Memo> getMemosUsingAnnotation(Long mno, Pageable pageable);

    @Query(value = "SELECT m.mno, m.memoText, current_date FROM Memo m WHERE m.mno > :mno",
        countQuery = "SELECT count(m) FROM Memo m WHERE m.mno > :mno")
    Page<Object[]> getMemosWithTime(Long mno, Pageable pageable);

    @Query(value = "SELECT * FROM tbl_memo WHERE mno > 0", nativeQuery = true)
    Page<Memo[]> getMemosWithMariaSQL();

    void deleteByMnoGreaterThan(Long num);
}
