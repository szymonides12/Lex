package agh.cs.lab;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextPart {

    protected final String name;    //np ROZDZIAL III
    protected final String title;   //np. ŹRÓDŁA PRAWA
    protected final List<String> content;
    protected final int number;

    protected TextPartType textPartType;

    protected LinkedHashMap<String, TextPart> children;

    public LinkedHashMap<String, TextPart> getAllChildren() {
        return new LinkedHashMap<>(children);
    }

    public TextPartType getTextPartType() {
        return textPartType;
    }


    public TextPart(TextPartType textPartType, StringBuilder content) {
        this.textPartType = textPartType;

        TextParser textParser = new TextParser(this.textPartType, content);
        this.children = textParser.getAllChildren();
        this.name = textParser.getName();
        this.title = textParser.getTitle();
        this.content = textParser.getContent();
        this.number = textParser.getNumber();
    }

    public List<String> getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public StringBuilder GetFullElement() {
        StringBuilder elem = new StringBuilder();

        elem.append(name);
        if (textPartType == TextPartType.Chapter || textPartType == TextPartType.Section || textPartType == TextPartType.Article) {
            elem.append('\n');
        }
        if (title.length() > 0) {
            elem.append(title);
            elem.append("\n");
        }

        if (textPartType == TextPartType.Chapter || textPartType == TextPartType.Section || textPartType == TextPartType.Title)
            elem.append("\n");

        if (content.size() > 0) {
            content.stream().forEachOrdered(s -> elem.append(s + "\n"));
        }

        if (textPartType == TextPartType.Title)
            elem.append("\n");

        children.values().stream().forEachOrdered(c -> elem.append(c.GetFullElement()));
        // if (textPartType.isAbove(TextPartType.Paragraph))
        //     elem.append("\n");

        elem.append("\n");
        if (textPartType.isBelow(TextPartType.Paragraph))
            elem.deleteCharAt(elem.length()-1);

        return elem;
    }

    //Source: https://stackoverflow.com/questions/32656888/recursive-use-of-stream-flatmap
    public List<TextPart> getAllOfType(TextPartType textPartType) {
        Stream<TextPart> stream = this.flatten();
        List<TextPart> list = stream.collect(Collectors.toList());
        return list.stream().filter(x -> x.textPartType == textPartType).collect(Collectors.toList());
    }

    public List<TextPart> getAllAboveEqualsType(TextPartType textPartType) {
        Stream<TextPart> stream = this.flatten();
        List<TextPart> list = stream.collect(Collectors.toList());
        return list.stream().filter(x -> !x.textPartType.isBelow(textPartType)).collect(Collectors.toList());
    }


    private Stream flatten() {
        return Stream.concat(
                Stream.of(this),
                children.values().stream().flatMap(x -> x.flatten())
        );
    }
}
