package com.vegadelalyra.question_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vegadelalyra.question_service.dao.model.Question;
import com.vegadelalyra.question_service.dao.model.QuestionWrapperDTO;
import com.vegadelalyra.question_service.dao.model.ResponseDTO;
import com.vegadelalyra.question_service.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllQuestions() throws Exception {
        List<Question> questions = new ArrayList<>();
        // Populate questions list as needed

        Mockito.when(questionService.getAllQuestions())
                .thenReturn(ResponseEntity.ok(questions));

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(questions)));
    }

    @Test
    void getQuestionsByCategory() throws Exception {
        String category = "Test Category";
        List<Question> questions = new ArrayList<>();
        // Populate questions list as needed

        Mockito.when(questionService.getQuestionsByCategory(category))
                .thenReturn(ResponseEntity.ok(questions));

        mockMvc.perform(MockMvcRequestBuilders.get("/category/{category}", category))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(questions)));
    }

    @Test
    void getQuestionsForQuiz() throws Exception {
        String categoryName = "Test Category";
        int numQuestions = 5;
        List<Integer> questionIds = new ArrayList<>();
        // Populate questionIds list as needed

        Mockito.when(questionService.getQuestionsForQuiz(categoryName, numQuestions))
                .thenReturn(ResponseEntity.ok(questionIds));

        mockMvc.perform(MockMvcRequestBuilders.get("/quiz")
                        .param("categoryName", categoryName)
                        .param("numQuestions", String.valueOf(numQuestions)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(questionIds)));
    }

    @Test
    void addQuestion() throws Exception {
        Question question = new Question();
        // Populate question object as needed

        Mockito.when(questionService.addQuestion(question))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("success"));

        mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void getQuestionsFromId() throws Exception {
        List<Integer> questionIds = new ArrayList<>();
        // Populate questionIds list as needed
        List<QuestionWrapperDTO> wrappedQuestions = new ArrayList<>();
        // Populate wrappedQuestions list as needed

        Mockito.when(questionService.getQuestionsFromId(questionIds))
                .thenReturn(ResponseEntity.ok(wrappedQuestions));

        mockMvc.perform(MockMvcRequestBuilders.post("/quiz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionIds)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(wrappedQuestions)));
    }

    @Test
    void getScore() throws Exception {
        List<ResponseDTO> responses = new ArrayList<>();
        // Populate responses list as needed

        Mockito.when(questionService.getScore(responses))
                .thenReturn(ResponseEntity.ok(5)); // Assuming score is 5

        mockMvc.perform(MockMvcRequestBuilders.post("/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responses)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("5"));
    }
}
