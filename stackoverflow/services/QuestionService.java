package stackoverflow.services;

import stackoverflow.exceptions.QuestionNotFoundException;
import stackoverflow.models.Question;
import stackoverflow.models.Tag;
import stackoverflow.models.User;
import stackoverflow.repositories.QuestionRepository;
import stackoverflow.repositories.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for question management operations.
 */
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;

    public QuestionService(QuestionRepository questionRepository, TagRepository tagRepository) {
        this.questionRepository = questionRepository;
        this.tagRepository = tagRepository;
    }

    public Question createQuestion(User author, String title, String content, List<String> tagNames) {
        List<Tag> tags = tagNames.stream()
                .map(tagRepository::getOrCreate)
                .collect(Collectors.toList());

        Question question = new Question(title, content, author, tags);
        question = questionRepository.save(question);
        author.addQuestion(question);
        return question;
    }

    public Question getQuestionById(String questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByTag(String tagName) {
        return questionRepository.findByTag(tagName);
    }

    public List<Question> getQuestionsByAuthor(User author) {
        return questionRepository.findByAuthor(author);
    }

    public List<Question> searchByKeyword(String keyword) {
        return questionRepository.searchByKeyword(keyword);
    }

    public List<Question> searchByMultipleTags(List<String> tagNames) {
        return questionRepository.findByTags(tagNames);
    }

    public void incrementViewCount(Question question) {
        question.incrementViewCount();
        questionRepository.save(question);
    }

    public long getQuestionCount() {
        return questionRepository.count();
    }
}



