package com.digiunion.model;

import io.jstach.jstache.JStache;


@JStache(path = "template/token.mustache")
public record Credentials(String code, String state){}
