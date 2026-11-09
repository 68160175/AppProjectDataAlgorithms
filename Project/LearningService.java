package Project;

import java.util.*;

public class LearningService {
    private final AppData data;
    // Queue: ลำดับคำถามของบทเรียนปัจจุบัน
    private final Queue<Word> quizQueue = new LinkedList<>();
    private Word currentWord;

    public LearningService(AppData data) {
        this.data = data;
    }

    public Set<String> getCategories() {
        return data.getCategories().keySet();
    }

    public List<Word> getWordsInCategory(String category) {
        ArrayList<Word> list = data.getCategories().get(category);
        return list == null ? Collections.emptyList() : new ArrayList<>(list);
    }

    public Word findWord(String english) {
        if (english == null) return null;
        return data.getWordMap().get(english.trim().toLowerCase(Locale.ROOT));
    }

    public void startLesson(String category) {
        quizQueue.clear();
        List<Word> lessonWords = getWordsInCategory(category);
        // สุ่มลำดับเพื่อให้แบบทดสอบไม่น่าเบื่อ
        Collections.shuffle(lessonWords);
        quizQueue.addAll(lessonWords);
        currentWord = null;
        data.log("Start lesson " + category);
    }

    public Word nextQuestion() {
        currentWord = quizQueue.poll();
        if (currentWord != null) {
            data.getHistory().push(currentWord);
        } else {
            data.lessonCompleted();
            data.log("Lesson Ended");
        }
        return currentWord;
    }

    public boolean hasMoreQuestions() {
        return !quizQueue.isEmpty();
    }

    public Word getCurrentWord() {
        return currentWord;
    }

    public boolean checkAnswer(String answer) {
        if (currentWord == null) return false;
        boolean correct = currentWord.getThai().trim()
                .equalsIgnoreCase(answer == null ? "" : answer.trim());

        data.recordAnswer(correct);
        if (correct) {
            data.log("Corrected: " + currentWord.getEnglish() + " (+10 XP)");
        } else {
            data.getReviewQueue().offer(
                    new AppData.ReviewItem(currentWord.getEnglish(), 1)
            );
            data.log("Incorrected: " + currentWord.getEnglish() + " added to review vocabulary");
        }
        return correct;
    }

    public List<Word> getAllWordsSorted() {
        ArrayList<Word> result = new ArrayList<>(data.getWords());
        result.sort(Comparator.comparing(Word::getEnglish, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public List<Word> getReviewWords() {
        ArrayList<Word> result = new ArrayList<>();
        HashSet<String> seen = new HashSet<>();
        for (AppData.ReviewItem item : data.getReviewQueue()) {
            if (seen.add(item.getEnglish())) {
                Word word = findWord(item.getEnglish());
                if (word != null) result.add(word);
            }
        }
        result.sort(Comparator.comparing(Word::getEnglish));
        return result;
    }

    public void removeFromReview(String english) {
        data.getReviewQueue().removeIf(item -> item.getEnglish().equals(english));
        data.log("Review: " + english);
    }

    public AppData getData() {
        return data;
    }
}