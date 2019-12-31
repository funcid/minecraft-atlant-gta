package ru.func.atlantgta.fraction;

public class Post extends PostBuilder implements IPost {

    private Fraction parrent;
    private String name;
    private String subName;
    private String roots;
    private int salary;

    Post(final PostBuilder postBuilder) {
        this.parrent = postBuilder.getParent();
        this.name = postBuilder.getName();
        this.subName = postBuilder.getSubName();
        this.roots = postBuilder.getRoots();
        this.salary = postBuilder.getSalary();
    }

    @Override
    public Fraction getParent() {
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
