package ru.func.atlantgta.fraction;

import java.util.ArrayList;
import java.util.List;

public class PostUtil {

    private static List<Post> posts = new ArrayList<>();
    private static Post NONE = new PostBuilder()
            .parrent(FractionUtil.getNoneFraction())
            .name("NONE")
            .subName("Отсутствует")
            .roots("none").salary(0)
            .build();

    public static Post getPostByName(String name) {
        for (Post post : posts)
            if (post.getName().equals(name))
                return post;
        return null;
    }

    public static List<Post> getPosts() {
        return posts;
    }

    public static Post getNonePost() {
        return NONE;
    }
}
