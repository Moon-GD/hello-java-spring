package com.hellospring.ex3.controller;

import com.hellospring.ex3.dto.SampleDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Controller
@RequestMapping("/sample")
@Log4j2
public class SampleController {

    @GetMapping("/ex1")
    public String ex1() {
        log.info("ex1.....");

        return "sample/ex1";
    }

    @GetMapping("ex2")
    public String ex2(Model model) {
        log.info("ex2.....");

        List<SampleDto> list = LongStream.rangeClosed(1, 20).mapToObj(
                i ->  SampleDto.builder()
                        .sno(i)
                        .first("First..." + i)
                        .last("Last..."+i)
                        .regTime(LocalDateTime.now())
                        .build()
        ).toList();

        model.addAttribute("list", list);

        return "sample/ex2";
    }
}
