package Project;

import java.io.Serializable;

public class Word implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String category;
    private final String english;
    private final String thai;
    private final String pronounce;

    public Word(String category, String english, String thai, String pronounce) {
        this.category = category;
        this.english = english;
        this.thai = thai;
        this.pronounce = pronounce;
    }

    public String getCategory() { return category; }
    public String getEnglish() { return english; }
    public String getThai() { return thai; }
    public String getPronounce() { return pronounce; }

    @Override
    public String toString() {
        return english + " — " + thai;
    }
}