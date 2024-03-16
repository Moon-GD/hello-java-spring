package com.hellospring.ex2.repository;

import com.hellospring.ex2.entity.Memo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> getMemosByMemoTextContaining(Long number);
    List<Memo> getMemosByMemoTextContaining(Long number, Pageable pageable);

    void deleteByMnoGreaterThan(Long num);
}
