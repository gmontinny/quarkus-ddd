package br.com.application.dto.response;

public record Link(String rel, String href, String method) {
    public static Link self(String href) {
        return new Link("self", href, "GET");
    }
    public static Link of(String rel, String href, String method) {
        return new Link(rel, href, method);
    }
}
