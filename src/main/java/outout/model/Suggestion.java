package outout.model;

import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

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

    @Column
    private Date suggestedDate;

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

    public Date getSuggestedDate() {
        return suggestedDate;
    }

    public void setSuggestedDate(final Date suggestedDate) {
        this.suggestedDate = suggestedDate;
    }
}
