package com.hellospring.ex2.repository;

import com.hellospring.ex2.entity.Memo;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.LongStream;

@SpringBootTest
public class MemoRepositoryTests {
    @Autowired
    MemoRepository memoRepository;

    // CRUD Variable
    int TEST_DATA_LENGTH = 100;

    // Paging And Sort Variables
    int PAGE_START = 0;
    int PAGE_SIZE = 10;

    // CRUD Test

    @Test
    public void createDummies() {
        LongStream.rangeClosed(1, TEST_DATA_LENGTH).forEach(i -> {
            Memo memo = Memo.builder().memoText(i + "th Sample data").build();
            memoRepository.save(memo);
        });
    }

    @Test
    public void findMemo() {
        Long memoId = new Random().nextLong(TEST_DATA_LENGTH) + 1;

        Optional<Memo> memo = memoRepository.findById(memoId);

        if(memo.isEmpty()) {
            System.out.printf("%d번째 Memo 객체를 찾지 못했습니다.", memoId);

            return;
        }

        System.out.printf("%d번째 Memo 객체: %s", memoId, memo);
    }

    @Test
    public void findMemoWithGetOne() {
        Long memoId = new Random().nextLong(TEST_DATA_LENGTH) + 1;

        Memo memo = memoRepository.getOne(memoId);

        try {
            System.out.printf("%d번째 Memo 객체: %s", memoId, memo);
        } catch(LazyInitializationException e) {
            System.out.printf("%d번째 객체 lazy initialization 실패", memoId);
        }
    }

    @Test
    public void updateDummy() {
        Long memoId = new Random().nextLong(TEST_DATA_LENGTH) + 1;

        Memo memo = Memo.builder().mno(memoId).memoText("테스트 코드로 업데이트된 객체").build();

        memoRepository.save(memo);
    }

    @Test
    public void deleteAllDummies() {
        memoRepository.deleteAll();
    }

    // Paging And Sort Test
    @Test
    public void pageTest() {
        Pageable pageable = PageRequest.of(PAGE_START, PAGE_SIZE);
        Page<Memo> memoPage = memoRepository.findAll(pageable);

        // 전체 엔티티 정보 출력
        System.out.println("getTotalElements(): " + memoPage.getTotalElements());

        // 전체 페이지 개수 출력
        System.out.println("getTotalPages(): " + memoPage.getTotalPages());

        // 페이지별 데이터의 개수 출력
        System.out.println("getSize(): " + memoPage.getSize());

        // 다음 페이지 존재 여부 출력
        System.out.println("hasNext(): " + memoPage.hasNext());

        // 첫 번째 페이지 여부 출력
        System.out.println("isFirst(): " + memoPage.isFirst());
    }

    @Test
    public void PageAndSortTest() {
        Sort sortById = Sort.by("mno").ascending();
        Sort sortByText = Sort.by("memoText").descending();
        Sort sortInfo = sortById.and(sortByText);
        Pageable pageable = PageRequest.of(PAGE_START, PAGE_SIZE, sortInfo);

        Page<Memo> sortedMemoPage = memoRepository.findAll(pageable);

        sortedMemoPage.getContent().forEach(System.out::println);
    }

    // Query Test
    @Test
    public void queryMethodTest() {
        List<Memo> memos = memoRepository.getMemosByMemoTextContaining(4L);
        memos.forEach(System.out::println);
    }

    @Test
    public void sortedQueryMethodTest() {
        Pageable pageable = PageRequest.of(PAGE_START, PAGE_SIZE, Sort.by("mno").ascending());
        List<Memo> memos = memoRepository.getMemosByMemoTextContaining(4L, pageable);
        memos.forEach(System.out::println);
    }


    @Transactional
    @Test
    public void queryMethodDeleteTest() {
        memoRepository.deleteByMnoGreaterThan(0L);
    }
}
