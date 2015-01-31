package ivan.jdbcExample;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;  // problem: id is null for new user and not null for existing user
    // seems that NewUser and PersistedUser are distinct classes
    @Column(name = "text")
    private String text;

    @Column(name = "user_id")
    private Integer userID;


    public Message(final String text, final Integer userID) {
        this.id = 0;
        this.text = text;
        this.userID = userID;
    }

    @Deprecated
    Message() {}

    public Integer id() {
        return id;
    }

    public String text() {
        return text;
    }

    public void text(final String text) {
        this.text = text;
    }

    public Integer userID() {
        return userID;
    }

    public void setUserID(final Integer userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final Message thatMessage = (Message) that;
        if (!userID.equals(thatMessage.userID)) return false;
        if (text != null ? !text.equals(thatMessage.text) : thatMessage.text != null) return false;
        if (id != null ? !id.equals(thatMessage.id) : thatMessage.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (userID != null ? userID.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, userID=%d, text='%s'}",
                getClass().getSimpleName(), id, userID, text);
    }
}