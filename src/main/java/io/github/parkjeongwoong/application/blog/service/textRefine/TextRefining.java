package io.github.parkjeongwoong.application.blog.service.textRefine;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class TextRefining {

    public static String preprocessingContent(String content) {
        return content.replaceAll("#","")
                .replaceAll("\n"," ")
                .replaceAll("\t", " ")
                .replaceAll("\\*", "")
                .replaceAll("---", "")
                .replaceAll("`", "")
                .replaceAll("<u>", "")
                .replaceAll("</u>", "")
                .replaceAll("\\|", "")
                .replaceAll(":", "")
                .replaceAll("<br>", "")
                .replaceAll("!\\[(.*?)]\\((.*?)\\)","[이미지]")
                .replaceAll("]\\((.*?)\\)","]")
                .trim();
    }

    public static String postprocessingContent(String content) {
        return content.replaceAll("!\\[(.*?)\\.\\.\\.@\\$_%!\\^","[이미지]...")
                .replaceAll("]\\((.*?)\\.\\.\\.@\\$_%!\\^","]...")
                .replaceAll("!\\[(.*?)]\\((.*?)\\)","[이미지]")
                .replaceAll("]\\((.*?)\\)","]")
                .replace("@$_%!^","")
                .trim();
    }

    public static String refineWord(String word) {
        return word.toLowerCase(Locale.ROOT).replaceAll("[^\\uAC00-\\uD7A30-9a-zA-Z\\\\s]", "").trim();
    }

}
