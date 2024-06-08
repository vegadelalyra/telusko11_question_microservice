package com.vegadelalyra.question_service.dao.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ResponseDTO {
    private Integer id;
    private String response;

    public ResponseDTO(int i, String s) {
    }
}
