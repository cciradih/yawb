package org.eu.cciradih.yawb.interceptor.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeminiTransfer implements Serializable {
    private List<Content> contents;
    private List<Candidate> candidates;

    @Data
    public static class Content {
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }

    @Data
    public static class Candidate {
        private Content content;
    }
}
