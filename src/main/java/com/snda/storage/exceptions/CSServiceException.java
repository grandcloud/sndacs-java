package com.snda.storage.exceptions;


/**
 * Exception for use by <code>CSService</code>s and related utilities.
 * This exception can hold useful additional information about errors that occur
 * when communicating with Cloud Storage.
 *
 */
public class CSServiceException extends ServiceException {

	private static final long serialVersionUID = 1L;

	public CSServiceException() {
		super();
	}

	public CSServiceException(String message, String xmlMessage, Throwable cause) {
		super(message, xmlMessage, cause);
	}

	/**
     * Constructor that includes the XML error document returned by Cloud Storage.
     * @param message
     * @param xmlMessage
     */
	public CSServiceException(String message, String xmlMessage) {
		super(message, xmlMessage);
	}

	public CSServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public CSServiceException(String message) {
		super(message);
	}

	public CSServiceException(Throwable cause) {
		super(cause);
	}
	
	/**
     * Wrap a ServiceException as an CSServiceException.
     * @param exception
     */
	public CSServiceException(ServiceException exception) {
		super(exception.getMessage(), exception.getXmlMessage(), exception.getCause());
		
		this.setResponseHeaders(exception.getResponseHeaders());
        this.setResponseCode(exception.getResponseCode());
        this.setResponseStatus(exception.getResponseStatus());
        this.setResponseDate(exception.getResponseDate());
        this.setRequestVerb(exception.getRequestVerb());
        this.setRequestPath(exception.getRequestPath());
        this.setRequestHost(exception.getRequestHost());
	}
	
	/**
     * @return The service-specific Error Code returned by Cloud Storage, if an Cloud Storage response is available.
     * For example "AccessDenied", "InternalError"
     * Null otherwise.
     */
	public String getCSErrorCode() {
		return this.getErrorCode();
	}
	
	/**
     * @return The service-specific Error Message returned by Cloud Storage, if an Cloud Storage response is available.
     * For example: "Access Denied", "We encountered an internal error. Please try again."
     */
	public String getCSErrorMessage() {
		return this.getErrorMessage();
	}
	
	/**
     * @return The Error Request ID returned by Cloud Storage, if an Cloud Storage response is available.
     * Null otherwise.
     */
	public String getCSErrorRequestId() {
		return this.getErrorRequestId();
	}

}
