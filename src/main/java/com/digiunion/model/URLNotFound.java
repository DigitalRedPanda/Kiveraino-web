package com.digiunion.model;

import io.jstach.jstache.JStache;

@JStache(path = "template/404.mustache")
public record URLNotFound(String url) {
}
