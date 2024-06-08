package com.vegadelalyra.question_service.dao;

import com.vegadelalyra.question_service.dao.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class QuestionDAOTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionDAO underTest;

    private Question question1;
    private Question question2;
    private Question question3;
    private Question question4;
    private Question question5;
    private Question question6;

    @BeforeEach
    void setUp() {
        // Initialize and persist common test data
        question1 = new Question();
        question1.setCategory("History");
        question1.setAnswer("Right");
        entityManager.persist(question1);

        question2 = new Question();
        question2.setCategory("Geography");
        question2.setAnswer("Wrong");
        entityManager.persist(question2);

        question3 = new Question();
        question3.setCategory("Science");
        entityManager.persist(question3);

        question4 = new Question();
        question4.setCategory("Philosophy");
        question4.setAnswer("Right");
        entityManager.persist(question4);

        question5 = new Question();
        question5.setCategory("Philosophy");
        question5.setAnswer("Wrong");
        entityManager.persist(question5);

        question6 = new Question();
        question6.setCategory("Philosophy");
        entityManager.persist(question6);

        entityManager.flush();  // Ensure the data is saved to the database
    }

    @Test
    void canFindByCategoryIgnoringCase() {
        // when
        List<Question> historyQuestions = underTest.findByCategoryIgnoreCase("history");
        List<Question> scienceQuestions = underTest.findByCategoryIgnoreCase("SCIENCE");
        List<Question> geographyQuestions = underTest.findByCategoryIgnoreCase("GeOgRaPhY");

        // then
        assertThat(historyQuestions).hasSize(1);
        assertThat(historyQuestions.get(0).getCategory()).isEqualTo("History");

        assertThat(scienceQuestions).hasSize(1);
        assertThat(scienceQuestions.get(0).getCategory()).isEqualTo("Science");

        assertThat(geographyQuestions).hasSize(1);
        assertThat(geographyQuestions.get(0).getCategory()).isEqualTo("Geography");
    }

    @Test
    void notFindNonExistingCategory() {
        // given
        String nonExistingCategory = "I DON'T EXIST";

        // when
        List<Question> nonExistingCategoryQuestions = underTest.findByCategoryIgnoreCase(nonExistingCategory);

        // then
        assertThat(nonExistingCategoryQuestions).isEmpty();
    }

    @Test
    void findRandomQuestionsByCategory() {
        // given
        String existingCategory = "Philosophy";
        int desiredQuestions = 3;

        List<Integer> existingIds = entityManager.getEntityManager()
                .createQuery("select q.id from Question q where q.category = :category", Integer.class)
                .setParameter("category", existingCategory)
                .getResultList();

        // when
        List<Integer> randomQuestionIds = underTest.findRandomQuestionsByCategory(existingCategory, desiredQuestions);

        // then
        assertThat(randomQuestionIds).containsExactlyInAnyOrderElementsOf(existingIds);
    }

    @Test
    void checkAnswer() {
        // given
        String rightAnswer = "Right";
        String wrongAnswer = "Wrong";

        // when
        Boolean passedQuestion = underTest.checkAnswer(question1.getId(), rightAnswer);
        Boolean failedQuestion = underTest.checkAnswer(question2.getId(), rightAnswer);

        // then
        assertThat(passedQuestion).isTrue();
        assertThat(failedQuestion).isFalse();
    }
}
