package ru.func.atlantgta.fraction;

public class Post implements IPost {

    private Fraction parrent;
    private String name;
    private String subName;
    private String roots;
    private int salary;

    public Post(Fraction parrent, String name, String subName, String roots, int salary) {
        this.parrent = parrent;
        this.name = name;
        this.subName = subName;
        this.roots = roots;
        this.salary = salary;
    }

    @Override
    public Fraction getParrent() {
        return parrent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSubName() {
        return subName;
    }

    @Override
    public String getRoots() {
        return roots;
    }

    @Override
    public int getSalary() {
        return salary;
    }

}
