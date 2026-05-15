package com.note_app.noteservice.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextNode.class, name = "text"),
        @JsonSubTypes.Type(value = ChecklistNode.class, name = "checklist"),
        @JsonSubTypes.Type(value = ImageNode.class, name = "image")
})
public abstract class NoteContentNode {

    public abstract String getType();

    public abstract String preview();
}
