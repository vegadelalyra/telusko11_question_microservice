package com.vegadelalyra.question_service.dao;

import com.vegadelalyra.question_service.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDAO extends JpaRepository<Question, Integer> {
    List<Question> findByCategoryIgnoreCase(String category);

    @Query(value = "SELECT q.id FROM question q WHERE q.category=:category ORDER BY RANDOM() LIMIT :numQ", nativeQuery = true)
    List<Integer> findRandomQuestionsByCategory(String category, int numQ);

    @Query("SELECT CASE WHEN (q.answer = :response) THEN true ELSE false END FROM Question q WHERE q.id = :id")
    Boolean checkAnswer(@Param("id") Integer id, @Param("response") String response);
}
