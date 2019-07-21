package ru.func.atlantgta.fraction;

public interface IPost {

    /**
     * @return parent fraction
     */
    Fraction getParrent();

    /**
     * @return name of this post
     */
    String getName();

    /**
     * @return name that used in profile methods...
     */
    String getSubName();

    /**
     * @return string of root list that have player with this post
     */
    String getRoots();

    /**
     * @return value of salary that player can get by hard work
     */
    int getSalary();

}
