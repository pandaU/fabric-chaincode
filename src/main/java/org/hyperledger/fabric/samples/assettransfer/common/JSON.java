package org.hyperledger.fabric.samples.assettransfer.common;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public class JSON {

	private static final Gson gson = new Gson();

	public static String stringify(Object object) {
		if (object == null) {
			return "";
		}
		return gson.toJson(object);
	}

	public static byte[] payload(Object object) {
		return stringify(object).getBytes(StandardCharsets.UTF_8);
	}
}
