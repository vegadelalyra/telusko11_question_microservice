package com.vegadelalyra.question_service.service;

import com.vegadelalyra.question_service.dao.QuestionDAO;
import com.vegadelalyra.question_service.model.Question;
import com.vegadelalyra.question_service.model.QuestionWrapper;
import com.vegadelalyra.question_service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    QuestionDAO questionDAO;

    public ResponseEntity<List<Question>> getAllQuestions() {
        return new ResponseEntity<>(questionDAO.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        return new ResponseEntity<>(questionDAO.findByCategoryIgnoreCase(category), HttpStatus.OK);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        questionDAO.save(question);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {

        List<Integer> randomQuestions = questionDAO.findRandomQuestionsByCategory(categoryName, numQuestions);

        return new ResponseEntity<>(randomQuestions, HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        List<QuestionWrapper> wrappedQuestions = new ArrayList<>();

        for (Integer questionId : questionIds) {
            Optional<Question> possibleQuestion = questionDAO.findById(questionId);

            if (!possibleQuestion.isPresent()) continue;

            Question question = possibleQuestion.get();
            QuestionWrapper wrappedQuestion = new QuestionWrapper(
                question.getId(),
                question.getTitle(),
                question.getOption1(),
                question.getOption2(),
                question.getOption3(),
                question.getOption4()
            );
            wrappedQuestions.add(wrappedQuestion);
        }

        return new ResponseEntity<>(wrappedQuestions, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(@RequestBody List<Response> responses) {
        int score = (int) responses.stream()
                .filter(response -> questionDAO.checkAnswer(response.getId(), response.getResponse()))
                .count();

        return new ResponseEntity<>(score, HttpStatus.OK);
    }
}
