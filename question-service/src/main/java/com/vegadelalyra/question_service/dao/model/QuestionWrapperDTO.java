package com.vegadelalyra.question_service.dao.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionWrapperDTO {
    private Integer id;
    private String title;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
}
