package ru.func.atlantgta.fraction;

public class PostBuilder {

    private Fraction parrent;
    private String name;
    private String subName;
    private String roots;
    private int salary;

    public PostBuilder parrent(Fraction parrent) {
        this.parrent = parrent;
        return this;
    }

    public PostBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PostBuilder subName(String subName) {
        this.subName = subName;
        return this;
    }

    public PostBuilder roots(String roots) {
        this.roots = roots;
        return this;
    }

    public PostBuilder salary(int salary) {
        this.salary = salary;
        return this;
    }

    public Post build() {
        return new Post(this);
    }

    protected Fraction getParrent() {
        return parrent;
    }

    protected String getName() {
        return name;
    }

    protected String getSubName() {
        return subName;
    }

    protected String getRoots() {
        return roots;
    }

    protected int getSalary() {
        return salary;
    }
}
