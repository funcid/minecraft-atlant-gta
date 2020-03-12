package ru.func.atlantgta;

import lombok.Getter;
import lombok.Setter;
import ru.func.atlantgta.fraction.Fraction;
import ru.func.atlantgta.fraction.FractionUtil;
import ru.func.atlantgta.fraction.Post;

@Setter
@Getter
public class AtlantPlayer implements IPlayer {

    private int level;
    private int age;
    private Fraction fraction;
    private Post post;
    private int stars;
    private int ammunition;
    private boolean ticket, card;

    public AtlantPlayer(int level, int age, Fraction fraction, Post post, int star, int ammunition, boolean ticket, boolean card) {

        this.level = level;
        this.age = age;
        if (fraction != null)
            this.fraction = fraction;
        else
            this.fraction = FractionUtil.getNoneFraction();
        this.post = post;
        this.stars = star;
        this.ammunition = ammunition;
        this.ticket = ticket;
        this.card = card;
    }
}
