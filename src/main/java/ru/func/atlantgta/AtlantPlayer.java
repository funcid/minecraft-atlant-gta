package ru.func.atlantgta;

import ru.func.atlantgta.fraction.Fraction;
import ru.func.atlantgta.fraction.FractionUtil;
import ru.func.atlantgta.fraction.Post;

public class AtlantPlayer implements IPlayer {

    private int level;
    private int age;
    private Fraction fraction;
    private Post post;
    private int stars;
    private int ammunition;
    private boolean ticket, card;

    public AtlantPlayer(int level, int age, Fraction fraction, Post post, int starts, int ammunition, boolean ticket, boolean card) {

        this.level = level;
        this.age = age;
        if (fraction != null)
            this.fraction = fraction;
        else
            this.fraction = FractionUtil.getNoneFraction();
        this.post = post;
        this.stars = starts;
        this.ammunition = ammunition;
        this.ticket = ticket;
        this.card = card;
    }

    public void setCard(boolean card) {
        this.card = card;
    }

    public void setTicket(boolean ticket) {
        this.ticket = ticket;
    }

    @Override
    public boolean hasCard() {
        return card;
    }

    @Override
    public boolean hasTicket() {
        return ticket;
    }

    @Override
    public int getAmmunition() {
        return ammunition;
    }

    @Override
    public void setAmmunition(int ammunition) {
        this.ammunition = ammunition;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public Fraction getFraction() {
        return fraction;
    }

    @Override
    public void setFraction(Fraction fraction) {
        this.fraction = fraction;
    }

    @Override
    public Post getPost() {
        return post;
    }

    @Override
    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public int getStars() {
        return stars;
    }

    @Override
    public void setStars(int starts) {
        this.stars = starts;
    }
}
