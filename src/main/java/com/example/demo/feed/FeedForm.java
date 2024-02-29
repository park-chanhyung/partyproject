package com.example.demo.feed;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FeedForm {
    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max=20)
    private String title;

    @NotEmpty(message = "내용을 작성해주세요.")
    private String content;

    @NotEmpty(message = "지역을 선택해주세요")
    private String area;


    private String address;


    private MultipartFile file;
}
