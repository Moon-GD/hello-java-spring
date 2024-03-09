package com.hellospring.ex2.repository;

import com.hellospring.ex2.entity.Memo;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@SpringBootTest
public class MemoRepositoryTests {
    @Autowired
    MemoRepository memoRepository;

    int TEST_DATA_LENGTH = 100;

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
}
