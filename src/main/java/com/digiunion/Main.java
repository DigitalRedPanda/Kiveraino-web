package com.digiunion;

import io.activej.eventloop.Eventloop;
import io.activej.http.HttpServer;

public final class Main {

	public static void main(String[] args) {
		Eventloop eventloop = Eventloop.create();
		var httpServer = HttpServer.builder(eventloop, request -> null).build();

	}

}
