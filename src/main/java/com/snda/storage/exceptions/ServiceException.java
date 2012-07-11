package com.snda.storage.exceptions;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import com.google.common.collect.Maps;
import com.snda.storage.service.StorageService;

/**
 * Exception for use by {@link StorageService} and related utilities.
 * This exception can hold useful additional information about errors that occur
 * when communicating with a service.
 * 
 * @author snda
 *
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String xmlMessage = null;
	
	// Fields from error messages.
    private String errorCode = null;
    private String errorMessage = null;
    private String errorRequestId = null;
    private String errorResource = null;
    private String errorHostId = null;
	
	private Map<String, String> responseHeaders = null;

    private int responseCode = -1;
    private String responseStatus = null;
    private String responseDate = null;
    private String requestVerb = null;
    private String requestPath = null;
    private String requestHost = null;
    
    /**
     * Create a service exception that includes the XML error document returned by service.
     *
     * @param message the detail message.
     * @param xmlMessage the service HTTP response XML message
     */
    public ServiceException(String message, String xmlMessage) {
        this(message, xmlMessage, null);
    }
    
    /**
     * Create a service exception that includes a specific message, an optional XML error
     * document returned by service, and an optional underlying cause exception.
     *
     * @param message the detail message.
     * @param xmlMessage the service HTTP response XML message
     * @param cause the cause
     */
    public ServiceException(String message, String xmlMessage, Throwable cause) {
        super(message, cause);
        if (xmlMessage != null) {
            parseXmlMessage(xmlMessage);
        }
    }

    /**
     * Create a service exception without any useful information.
     */
	public ServiceException() {
		super();
	}

	/**
     * Create a service exception that includes a specific message and an
     * optional underlying cause exception.
     *
     * @param message the detail message.
     * @param cause the cause
     */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Create a service exception that includes a specific message.
     *
     * @param message the detail message.
     */
	public ServiceException(String message) {
		super(message);
	}

	/**
     * Create a service exception that includes an underlying cause exception.
     *
     * @param cause the cause
     */
	public ServiceException(Throwable cause) {
		super(cause);
	}
	
	private String findXmlElementText(String xmlMessage, String elementName) {
        Pattern pattern = Pattern.compile(".*<" + elementName + ">(.*)</" + elementName + ">.*");
        Matcher matcher = pattern.matcher(xmlMessage);
        if (matcher.matches() && matcher.groupCount() == 1) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
	
	private void parseXmlMessage(String xmlMessage) {
        xmlMessage = xmlMessage.replaceAll("\n", "");
        this.xmlMessage = xmlMessage;

        this.errorCode = findXmlElementText(xmlMessage, "Code");
        this.errorMessage = findXmlElementText(xmlMessage, "Message");
        this.errorRequestId = findXmlElementText(xmlMessage, "RequestId");
        this.errorHostId = findXmlElementText(xmlMessage, "HostId");
        this.errorResource = findXmlElementText(xmlMessage, "Resource");
    }

	/**
     * @return The HTTP Response Code returned by the service, if an HTTP response is available.
     * For example: 401, 404, 500
     */
	public int getResponseCode() {
        return responseCode;
    }
	
	/**
     * Set the exception's HTTP response code; for internal use only.
     * @param responseCode the HTTP response code
     */
	public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
	
	/**
     * @return
     * The HTTP Status message returned by the service, if an HTTP response is available.
     * For example: "Forbidden", "Not Found", "Internal Server Error"
     */
	public String getResponseStatus() {
        return responseStatus;
    }
	
	/**
     * Set the exception's HTTP response status; for internal use only.
     * @param responseStatus the HTTP response status
     */
	public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }
	
	/**
     * @return
     * The exception's HTTP response date, if any.
     */
	public String getResponseDate() {
        return responseDate;
    }
	
	/**
     * Set the exception's HTTP response date; for internal use only.
     * @param responseDate HTTP response date
     */
	public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }
	
	/**
     * @return The HTTP Verb used in the request, if available.
     * For example: "GET", "PUT", "DELETE", "HEAD"
     */
	public String getRequestVerb() {
        return requestVerb;
    }
	
	/**
     * Set the exception's HTTP request verb; for internal use only.
     * @param requestVerb HTTP request method
     */
	public void setRequestVerb(String requestVerb) {
        this.requestVerb = requestVerb;
    }
	
	/**
     * @return
     * the exception's HTTP request path; if any.
     */
	public String getRequestPath() {
        return requestPath;
    }
	
	/**
     * Set the exception's HTTP request path; for internal use only.
     * @param requestPath HTTP request path
     */
	public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }
	
	/**
     * @return
     * the exception's HTTP request hostname; if any.
     */
	public String getRequestHost() {
        return requestHost;
    }
	
	/**
     * Set the exception's HTTP request hostname; for internal use only.
     * @param requestHost HTTP request hostname
     */
	public void setRequestHost(String requestHost) {
        this.requestHost = requestHost;
    }
	
	/**
     * @return
     * the exception's HTTP response headers, if any.
     */
	public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

	/**
     * Set the exception's HTTP response headers; for internal use only.
     * @param responseHeaders HTTP response headers
     */
    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    /**
     * Set the exception's HTTP response headers; for internal use only.
     * @param headers HTTP response headers
     */
	public void setResponseHeaders(Header[] headers) {
		responseHeaders = Maps.newHashMap();
		for (Header header : headers) {
			responseHeaders.put(header.getName(), header.getValue());
		}
	}
	
	/**
     * @return The service-specific Error Code returned by the service, if a response is available.
     * For example "AccessDenied", "InternalError"
     * Null otherwise.
     */
	public String getErrorCode() {
        return this.errorCode;
    }

	/**
     * Set the exception's error code; for internal use only.
     * @param code HTTP response code
     */
	public void setErrorCode(String code) {
        this.errorCode = code;
    }

	/**
     * @return The service-specific Error Message returned by the service, if a response is available.
     * For example: "Access Denied", "We encountered an internal error. Please try again."
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Set the exception's error message; for internal use only.
     * @param message service specific error message
     */
    public void setErrorMessage(String message) {
        this.errorMessage= message;
    }
    
    /**
     * @return The Error Host ID returned by the service, if a response is available.
     * Null otherwise.
     */
    public String getErrorHostId() {
        return errorHostId;
    }

    /**
     * Set the exception's host ID; for internal use only.
     * @param hostId
     */
    public void setErrorHostId(String hostId) {
        this.errorHostId = hostId;
    }

    /**
     * @return The Error Request ID returned by the service, if a response is available.
     * Null otherwise.
     */
    public String getErrorRequestId() {
        return errorRequestId;
    }
    
    /**
     * Set the exception's request ID; for internal use only.
     * @param requestId error request ID
     */
    public void setErrorRequestId(String requestId) {
        this.errorRequestId = requestId;
    }
    
    /**
     * @return The Error Resource returned by the service, if a response is available.
     * For example: "/mybucket/myobject".
     */
    public String getErrorResource() {
    	return errorResource;
    }
    
    /**
     * Set the exception's resource; for internal use only.
     * @param errorResource error resource
     */
    public void setErrorResource(String errorResource) {
		this.errorResource = errorResource;
	}

    /**
     * @return The XML Error message returned by the service, if a response is available.
     * Null otherwise.
     */
	public String getXmlMessage() {
        return xmlMessage;
    }
	
}
