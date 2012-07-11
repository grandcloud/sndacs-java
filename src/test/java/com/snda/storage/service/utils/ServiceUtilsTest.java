package com.snda.storage.service.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.base.Charsets;

public class ServiceUtilsTest {
	private static final String date = "2006-02-03T16:45:09.000Z";

	@Test
	public void testParseIso8601Date() throws ParseException {
		DateTime dateTime = new DateTime();
		// ignore milliseconds
		assertEquals(DateTime.parse(date).getMillis(), ServiceUtils
				.parseIso8601Date(date).getTime());
		assertEquals(ServiceUtils.parseIso8601Date(dateTime.toString())
				.getTime() / 1000, dateTime.getMillis() / 1000);
	}

	@Test
	public void testFormatIso8601Date() {
		DateTime dateTime = DateTime.parse(date);
		assertEquals(dateTime.toString(),
				ServiceUtils.formatIso8601Date(dateTime.toDate()));
	}

	@Test
	public void testParseRfc822Date() throws ParseException {
		String tempdate = "Sun, 01 Mar 2009 12:00:00 GMT";
		assertEquals(new Date(tempdate).getTime(), ServiceUtils
				.parseRfc822Date(tempdate).getTime());
	}

	@Test
	public void testFormatRfc822Date() {
		String tempdate = "Sun, 01 Mar 2009 12:00:00 GMT";
		assertEquals(tempdate,
				ServiceUtils.formatRfc822Date(new Date(tempdate)));
	}

	@Test
	public void testSignWithHmacSha1() {
		String secretKey = "uV3F3YluFJax1cknvbcGwgjvx4QpvB+leU8dUj2o";
		String canonicalString = "GET\n" + "\n" + "\n"
				+ "Tue, 27 Mar 2007 19:36:42 +0000\n"
				+ "/johnsmith/photos/puppy.jpg";
		assertEquals("xXjDGYUmKxnwqr5KXNPGldn5LbA=",
				ServiceUtils.signWithHmacSha1(secretKey, canonicalString));

		canonicalString = "DELETE\n" + "\n" + "\n" + "\n"
				+ "x-amz-date:Tue, 27 Mar 2007 21:20:26 +0000\n"
				+ "/johnsmith/photos/puppy.jpg";
		assertEquals("k3nL7gH3+PadhTEVn5Ip83xlYzk=",
				ServiceUtils.signWithHmacSha1(secretKey, canonicalString));

		canonicalString = "PUT\n" + "\n" + "image/jpeg\n"
				+ "Tue, 27 Mar 2007 21:15:45 +0000\n"
				+ "/johnsmith/photos/puppy.jpg";
		assertEquals("hcicpDDvL9SsO6AkvxqmIWkmOuQ=",
				ServiceUtils.signWithHmacSha1(secretKey, canonicalString));

		canonicalString = "GET\n" + "\n" + "\n"
				+ "Wed, 28 Mar 2007 01:29:59 +0000\n" + "/";
		assertEquals("Db+gepJSUbZKwpx1FR0DLtEYoZA=",
				ServiceUtils.signWithHmacSha1(secretKey, canonicalString));

		canonicalString = "GET\n" + "\n" + "\n"
				+ "Tue, 27 Mar 2007 19:42:41 +0000\n" + "/johnsmith/";
		assertEquals("jsRt/rhG+Vtp88HrYL706QhE4w4=",
				ServiceUtils.signWithHmacSha1(secretKey, canonicalString));

		canonicalString = "PUT\n" + "4gJE4saaMU4BqNR0kLY+lw==\n"
				+ "application/x-download\n"
				+ "Tue, 27 Mar 2007 21:06:08 +0000\n"
				+ "x-amz-acl:public-read\n"
				+ "x-amz-meta-checksumalgorithm:crc32\n"
				+ "x-amz-meta-filechecksum:0x02661779\n"
				+ "x-amz-meta-reviewedby:" + "joe@johnsmith.net,jane@johns"
				+ "mith.net\n" + "/static.johnsmith.net/db-backup." + "dat.gz";
		assertEquals("C0FlOtU8Ylb9KDTpZqYkZPX91iI=",
				ServiceUtils.signWithHmacSha1(secretKey, canonicalString));

		canonicalString = "GET\n" + "\n" + "\n"
				+ "Wed, 28 Mar 2007 01:49:49 +0000\n" + "/diction"
				+ "ary/fran%C3%A7ais/pr%c3%a9f%c3%a8re";
		assertEquals("dxhSBHoI6eVSPcXJqEghlUzZMnY=",
				ServiceUtils.signWithHmacSha1(secretKey, canonicalString));
	}

	@Test
	public void testToHex() {
		assertEquals("746573742064617461", ServiceUtils.toHex("test data".getBytes()));
	}

	@Test
	public void testFromHex() {
		assertEquals("test data", new String(ServiceUtils.fromHex("746573742064617461"), Charsets.UTF_8));
	}

	@Test
	public void testToBase64() {
		assertEquals("dGVzdCBkYXRh", ServiceUtils.toBase64("test data".getBytes()));
	}

	@Test
	public void testFromBase64() {
		assertEquals("test data", new String(ServiceUtils.fromBase64("dGVzdCBkYXRh"), Charsets.UTF_8));
	}

	@Test
	public void testComputeMD5HashInputStream() throws NoSuchAlgorithmException, IOException {
		InputStream input = new FileInputStream("src/test/resources/bucket_list.xml");
		assertEquals("pY/XgFfhSvmZTsxd20tmqg==", Base64.encodeBase64String(ServiceUtils.computeMD5Hash(input)));
	}

}
