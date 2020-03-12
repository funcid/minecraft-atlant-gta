package ru.func.atlantgta;

import ru.func.atlantgta.fraction.Fraction;
import ru.func.atlantgta.fraction.Post;

public interface IPlayer {

    int getLevel();

    void setLevel(int level);

    int getAge();

    void setAge(int age);

    Fraction getFraction();

    void setFraction(Fraction fraction);

    Post getPost();

    void setPost(Post post);

    int getStars();

    void setStars(int starts);

    int getAmmunition();

    void setAmmunition(int ammunition);

    boolean isCard();

    boolean isTicket();

    void setCard(boolean card);

    void setTicket(boolean ticket);
}
