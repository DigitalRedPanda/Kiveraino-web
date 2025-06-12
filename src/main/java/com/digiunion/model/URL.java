package com.digiunion.model;

import io.jstach.jstache.JStache;

@JStache(path = "template/main.mustache")
public record URL(String url) {
}
