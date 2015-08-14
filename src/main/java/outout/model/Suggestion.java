package outout.model;

import javax.persistence.*;

@Entity
public class Suggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "suggestion_generator")
    @SequenceGenerator(name = "suggestion_generator", sequenceName = "suggestion_seq")
    private Long id;

    @Column
    private String suggestedBy;

    @Column
    private String suggestion;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSuggestedBy() {
        return suggestedBy;
    }

    public void setSuggestedBy(final String suggestedBy) {
        this.suggestedBy = suggestedBy;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(final String suggestion) {
        this.suggestion = suggestion;
    }
}
