package ru.func.atlantgta.fraction;

import java.util.ArrayList;
import java.util.List;

public class PostUtil {

    private static List<Post> posts = new ArrayList<>();
    private static Post NONE = new Post(FractionUtil.getNoneFraction(), "NONE", "Отсутствует", "none", 0);

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
