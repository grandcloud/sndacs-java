package com.snda.storage.httpclient;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.snda.storage.Location;
import com.snda.storage.StorageClass;
import com.snda.storage.xml.BucketLoggingStatus;
import com.snda.storage.xml.BucketSummary;
import com.snda.storage.xml.CommonPrefix;
import com.snda.storage.xml.CompleteMultipartUpload;
import com.snda.storage.xml.CompleteMultipartUploadResult;
import com.snda.storage.xml.CopyObjectResult;
import com.snda.storage.xml.CopyPartResult;
import com.snda.storage.xml.CreateBucketConfiguration;
import com.snda.storage.xml.InitiateMultipartUploadResult;
import com.snda.storage.xml.ListAllMyBucketsResult;
import com.snda.storage.xml.ListBucketResult;
import com.snda.storage.xml.ListMultipartUploadsResult;
import com.snda.storage.xml.ListPartsResult;
import com.snda.storage.xml.LocationConstraint;
import com.snda.storage.xml.Logging;
import com.snda.storage.xml.ObjectSummary;
import com.snda.storage.xml.Part;
import com.snda.storage.xml.PartSummary;
import com.snda.storage.xml.UploadSummary;
import com.snda.storage.xml.XMLEntity;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class XMLResolverTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLResolverTest.class);
	
	private XMLResolver xmlResolver;

	@Test
	public void testMarshallListAllMyBucketsResult() throws Exception {
		BucketSummary books = new BucketSummary("books",
				null,
				new DateTime(2011, 4, 29, 16, 45, 9, 0, DateTimeZone.UTC));
		
		BucketSummary devices = new BucketSummary("devices",
				Location.HUABEI_1,
				new DateTime(2011, 4, 29, 16, 41, 58, 0,DateTimeZone.UTC));
		
		ListAllMyBucketsResult listAllMyBucketsResult = new ListAllMyBucketsResult(Arrays.asList(
				books, 
				devices));
		assertXMLEquals(contentOf("list_all_my_buckets_result.xml"), marshall(listAllMyBucketsResult));
	}
	
	@Test
	public void testMarshallListBucketResult() throws Exception {
		ListBucketResult listBucketResult = new ListBucketResult().
				withBucketName("books").
				withPrefix("A").
				withMarker("Alex").
				withMaxKeys(1000).
				withTruncated(false).
				withDelimiter("/").
				withObjectSummary(new ObjectSummary().
						withKey("english").
						withLastModified(new DateTime(2009, 10, 12, 17, 50, 30, 0, DateTimeZone.UTC)).
						withSize(1234L).
						withEntityTag("\"828ef3fdfa96f00ad9f27c383fc9ac7f\"")).
				withObjectSummary(new ObjectSummary().
						withKey("chinese").
						withLastModified(new DateTime(2009, 10, 12, 19, 00, 35, 0, DateTimeZone.UTC)).
						withSize(45234L).
						withEntityTag("\"881f7881ac1bc144a2672e45babb8839\"").
						withStorageClass(StorageClass.REDUCED_REDUNDANCY)).
				withCommonPrefix(new CommonPrefix("photos/")).
				withCommonPrefix(new CommonPrefix("books/"));
		assertXMLEquals(contentOf("list_bucket_result.xml"), marshall(listBucketResult));
	}
	
	@Test
	public void testUnmarshallLocationConstraint() throws Exception {
		LocationConstraint actual = unmarshall(LocationConstraint.class, 
				contentOf("get_bucket_location.xml"));
		assertEquals(new LocationConstraint(Location.HUADONG_1), actual);
	}
	
	@Test
	public void testUnmarshallLocationConstraintWithDefault() throws Exception {
		LocationConstraint actual = unmarshall(LocationConstraint.class, 
				contentOf("get_bucket_location_with_default.xml"));
		assertEquals(new LocationConstraint(), actual);
	}
	
	@Test
	public void testUnmarshallCreateBucketConfiguration() throws Exception {
		CreateBucketConfiguration actual = unmarshall(CreateBucketConfiguration.class, 
				contentOf("create_bucket_configuration.xml"));
		assertEquals(new CreateBucketConfiguration(Location.HUADONG_1), actual);
	}
	
	@Test
	public void testBucketLoggingStatus() throws Exception {
		BucketLoggingStatus actual = unmarshall(BucketLoggingStatus.class, 
				contentOf("bucket_logging_status.xml"));
		BucketLoggingStatus bucketLoggingStatus = new BucketLoggingStatus(new Logging("mybucketlogs", "mybucket-access_log-/"));
		assertEquals(bucketLoggingStatus, actual);
		
		assertXMLEquals(contentOf("bucket_logging_status.xml"), marshall(bucketLoggingStatus));
	}
	
	@Test
	public void testMarshallListPartsResult() throws Exception {
		ListPartsResult listPartsResult = new ListPartsResult().
				withBucket("mybucket").
				withKey("anykey").
				withUploadId("XXBsb2FkIElEIGZvciBlbHZpbmcncyVcdS1tb3ZpZS5tMnRzEEEwbG9hZA").
				withPartNumberMarker(5).
				withNextPartNumberMarker(10).
				withMaxParts(10).
				withTruncated(true).
				withPartSummary(new PartSummary().
						withPartNumber(1).
						withEntityTag("\"828ef3fdfa96f00ad9f27c383fc9ac7f\"").
						withSize(1111L).
						withLastModified(new DateTime(2012, 10, 12, 19, 00, 35, 0, DateTimeZone.UTC))).
				withPartSummary(new PartSummary().
						withPartNumber(2).
						withEntityTag("\"881f7881ac1bc144a2672e45babb8839\"").
						withSize(231L).
						withLastModified(new DateTime(2012, 10, 12, 19, 01, 35, 0, DateTimeZone.UTC)));
		
		assertXMLEquals(contentOf("list_parts_result.xml"), marshall(listPartsResult));
	}
	
	@Test
	public void testListMultipartUploadsResult() throws Exception {
		ListMultipartUploadsResult listMultipartUploadsResult = new ListMultipartUploadsResult().
				withBucket("mybucket").
				withDelimiter("/").
				withPrefix("my").
				withKeyMarker("books").
				withUploadIdMarker("ccccc").
				withNextKeyMarker("bash").
				withNextUploadIdMarker("ddddd").
				withMaxUploads(100).
				withTruncated(false).
				withUploadSummary(new UploadSummary().
						withKey("key1").
						withUploadId("ccccccccc").
						withInitiated(new DateTime(2012, 10, 1, 19, 00, 35, 0, DateTimeZone.UTC))).
				withUploadSummary(new UploadSummary().
						withKey("key2").
						withUploadId("ddddddddd").
						withInitiated(new DateTime(2012, 10, 2, 19, 00, 35, 0, DateTimeZone.UTC))).
				withCommonPrefix(new CommonPrefix("photos/")).
				withCommonPrefix(new CommonPrefix("books/"));
		
		assertXMLEquals(contentOf("list_multipart_uploads_result.xml"), marshall(listMultipartUploadsResult));
	}
	
	@Test
	public void testMarshalInitiateMultipartUploadResult() throws Exception {
		InitiateMultipartUploadResult initiateMultipartUploadResult = new InitiateMultipartUploadResult().
				withBucket("mybucket").
				withKey("anykey").
				withUploadId("XXBsb2FkIElEIGZvciBlbHZpbmcncyVcdS1tb3ZpZS5tMnRzEEEwbG9hZA");
		
		assertXMLEquals(contentOf("initiate_multipart_upload_result.xml"), marshall(initiateMultipartUploadResult));
	}
	
	@Test
	public void testMarshalCompleteMultipartUploadResult() throws Exception {
		CompleteMultipartUploadResult completeMultipartUploadResult = new CompleteMultipartUploadResult().
				withLocation("http://storage.grandcloud.cn/mybucket/mykey").
				withBucket("mybucket").
				withKey("mykey").
				withEntityTag("\"828ef3fdfa96f00ad9f27c383fc9ac7f\"");
		
		assertXMLEquals(contentOf("complete_multipart_upload_result.xml"), marshall(completeMultipartUploadResult));
	}
	
	@Test
	public void testMarshalCopyObjectResult() throws Exception {
		CopyObjectResult copyObjectResult = new CopyObjectResult(
				"\"881f7881ac1bc144a2672e45babb8839\"",
				new DateTime(2009, 10, 12, 19, 00, 35, 0, DateTimeZone.UTC));
		assertXMLEquals(contentOf("copy_object_result.xml"), marshall(copyObjectResult));
	}
	
	@Test
	public void testMarshalCopyPartResult() throws Exception {
		CopyPartResult copyPartResult = new CopyPartResult(
				"\"881f4672839e41ac1bc1ba784a28bb85\"",
				new DateTime(2009, 12, 12, 19, 00, 35, 0, DateTimeZone.UTC));
		assertXMLEquals(contentOf("copy_part_result.xml"), marshall(copyPartResult));
	}
	
	@Test
	public void testUnmarshalCompleteMultipartUpload() throws Exception {
		CompleteMultipartUpload actual = unmarshall(CompleteMultipartUpload.class, 
				contentOf("complete_multipart_upload.xml"));
		
		CompleteMultipartUpload completeMultipartUpload = new CompleteMultipartUpload(
				new Part(1, "\"828ef3fdfa96f00ad9f27c383fc9ac7f\""),
				new Part(2, "\"881f7881ac1bc144a2672e45babb8839\""));
		assertEquals(completeMultipartUpload, actual);
	}
	
	private static String contentOf(String file) throws IOException {
		return Resources.toString(XMLEntity.class.getResource(file), Charsets.UTF_8);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T unmarshall(Class<?> type, String content) throws JAXBException {
		return (T) xmlResolver.read((Class<Object>) type, new ByteArrayInputStream(content.getBytes(Charsets.UTF_8)));
	}
	
	private String marshall(Object object) throws JAXBException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		xmlResolver.write(object, outputStream);
		return new String(outputStream.toByteArray(), Charsets.UTF_8);
	}
	
	private static String compact(String xml) {
		return xml
			.replaceAll("\r\n", "")
			.replaceAll("  ", "")
			.replaceAll("\n", "")
			.replaceAll("\t", "");
	}
	
	private static void assertXMLEquals(String expected, String actual) {
		String compactedExpected = compact(expected);
		String compactedActual = compact(actual);
		LOGGER.info("Expected -> " + compactedExpected);
		LOGGER.info("Actual   -> " + compactedActual);
		assertEquals(compactedExpected, compactedActual);
	}
	
	@Before
	public void setUp() {
		xmlResolver = new XMLResolver();
	}
}
