package com.vegadelalyra.question_service.service;

import com.vegadelalyra.question_service.dao.QuestionDAO;
import com.vegadelalyra.question_service.dao.model.Question;
import com.vegadelalyra.question_service.dao.model.QuestionWrapperDTO;
import com.vegadelalyra.question_service.dao.model.ResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org. assertj. core. api. Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionServiceTest {

    @Mock
    QuestionDAO questionDAO;

    @InjectMocks
    QuestionService underTest;

    private List<Question> testQuestions;

    @BeforeEach()
    void setUp() {
        // Create some test questions
        Question question1 = new Question();
        question1.setId(1);
        question1.setTitle("Question 1");

        Question question2 = new Question();
        question2.setId(2);
        question2.setTitle("Question 2");

        testQuestions = List.of(question1, question2);
    }


    @Test
    void getAllQuestions() {
        // given
        // Mock the behaviour of the questionDAO to return testQuestions
        when(questionDAO.findAll()).thenReturn(testQuestions);

        // when
        // Call the getAllQuestions method of the questionService
        ResponseEntity<List<Question>> responseEntity = underTest.getAllQuestions();

        // then
        // Verify that the response entity has the expected status code
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Verify that the response entity contains the expected list of questions
        assertThat(responseEntity.getBody()).isEqualTo(testQuestions);
    }

    @Test
    void getQuestionsByCategory() {
        // given
        String existingCategory = "Existing";
        testQuestions.getFirst().setCategory(existingCategory);

        List<Question> questionsFilteredByCategory =
                testQuestions.stream().filter(question ->
                        question.getCategory() != null &&
                        question.getCategory()
                        .equalsIgnoreCase(existingCategory))
                        .toList();

        when(questionDAO.findByCategoryIgnoreCase(existingCategory))
                .thenReturn(questionsFilteredByCategory);

        // when
        ResponseEntity<List<Question>> responseEntity = underTest
                .getQuestionsByCategory(existingCategory);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(questionsFilteredByCategory);
    }

    @Test
    void addQuestion() {
        // given
        Question newQuestion = new Question();
        newQuestion.setCategory("miau");
        newQuestion.setDifficulty("miau");
        newQuestion.setAnswer("WOW");
        newQuestion.setTitle("miau");
        newQuestion.setOption1("MIAU");
        newQuestion.setOption2("WOW");
        newQuestion.setOption3("MIAU");
        newQuestion.setOption4("MIAU");
        when(questionDAO.save(newQuestion)).thenReturn(any());

        // when
        ResponseEntity<String> responseEntity = underTest.addQuestion(newQuestion);

        // then
        verify(questionDAO, times(1)).save(any(Question.class));
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo("success");
    }

    @Test
    void getQuestionsForQuiz() {
        // given
        String existingCategory = "Scatash";
        Integer numOfQuestions = 5;
        List<Integer> expectedQuestionIds = List.of(1,2,3,4,5);

        Question question = testQuestions.getFirst();
        question.setCategory(existingCategory);

        when(questionDAO.findRandomQuestionsByCategory(anyString(), anyInt()))
                .thenReturn(expectedQuestionIds);

        // when
        ResponseEntity<List<Integer>> responseEntity =
                underTest.getQuestionsForQuiz(existingCategory, numOfQuestions);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(expectedQuestionIds);
    }

    @Test
    void getQuestionsFromId() {
        // given
        List<Integer> questionIds = Arrays.asList(1, 2, 3);
        List<Question> questions = Arrays.asList(
                new Question(1, "Title 1", "Option 1", "Option 2", "Option 3", "Option 4", "Answer 1", "Easy", "Category 1"),
                new Question(2, "Title 2", "Option 1", "Option 2", "Option 3", "Option 4", "Answer 2", "Medium", "Category 2"),
                new Question(3, "Title 3", "Option 1", "Option 2", "Option 3", "Option 4", "Answer 3", "Hard", "Category 3")
        );

        when(questionDAO.findById(anyInt())).thenAnswer(invocationOnMock -> {
            Integer questionId = invocationOnMock.getArgument(0);
            Optional<Question> questionOptional = questions.stream()
                    .filter(question -> question.getId().equals(questionId)).findFirst();
            return questionOptional.map(Optional::of).orElse(Optional.empty());
        });

        // when
        ResponseEntity<List<QuestionWrapperDTO>> responseEntity =
                underTest.getQuestionsFromId(questionIds);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<QuestionWrapperDTO> wrappedQuestions = responseEntity.getBody();
        assertNotNull(wrappedQuestions);
        assertEquals(3, wrappedQuestions.size());

        // Compare IDs after sorting
        List<Integer> wrappedQuestionIds = wrappedQuestions.stream()
                .map(QuestionWrapperDTO::getId)
                .sorted() // Sort the IDs
                .collect(Collectors.toList());
        Collections.sort(questionIds); // Sort the original IDs
        assertThat(wrappedQuestionIds).containsExactlyElementsOf(questionIds);
    }

    @Test
    void getQuestionsFromId_ContinueToNextIteration() {
        // Given
        List<Integer> questionIds = Arrays.asList(1, 2, 3);
        List<Question> questions = Arrays.asList(
                new Question(),
                new Question(),
                new Question()
        );

        // Stubbing the findById method to return empty Optional for all questionIds
        when(questionDAO.findById(anyInt())).thenReturn(Optional.empty());

        // When
        ResponseEntity<List<QuestionWrapperDTO>> responseEntity = underTest.getQuestionsFromId(questionIds);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().isEmpty());
    }


    @Test
    void getScore() {
        // Given
        List<ResponseDTO> responses = Arrays.asList(
                new ResponseDTO(1, "Answer 1"),
                new ResponseDTO(2, "Answer 2")
        );

        // Stubbing the DAO to return true for the first response and false for the second
        when(questionDAO.checkAnswer(1, "Answer 1")).thenReturn(true);
        when(questionDAO.checkAnswer(2, "Answer 2")).thenReturn(false);

        // Initialize the score variable
        int score = 0;

        // When
        for (ResponseDTO response : responses) {
            if (questionDAO.checkAnswer(response.getId(), response.getResponse())) {
                score++;
            }
        }

        // Then
        ResponseEntity<Integer> responseEntity = underTest.getScore(responses);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(score, responseEntity.getBody());
    }

}