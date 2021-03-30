package com.ikeyit.common.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

import java.util.Base64;

public class IdUtils {
	public static String uuid() {
		UUID uuid = UUID.randomUUID();
	    byte[] bytes = ByteBuffer.allocate(16).putLong(0, uuid.getLeastSignificantBits()).putLong(8, uuid.getMostSignificantBits()).array();
	    String withPadding = Base64.getUrlEncoder().encodeToString(bytes);
		return withPadding.substring(0, withPadding.length() - 2);
	}
}
