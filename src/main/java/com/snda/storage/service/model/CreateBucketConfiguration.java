package com.snda.storage.service.model;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.jamesmurty.utils.XMLBuilder;
/**
 * Represents the location configuraton to be applied to a newly
 * created Bucket.
 *
 */
public class CreateBucketConfiguration {
    private String location = null;

    public CreateBucketConfiguration() {
    }

    public CreateBucketConfiguration(String location) {
        this.location = location;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return
     * An XML representation of the object suitable for use as an input to the REST/HTTP interface.
     *
     * @throws FactoryConfigurationError
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public String toXml() throws ParserConfigurationException,
        FactoryConfigurationError, TransformerException
    {
        XMLBuilder builder = XMLBuilder.create("CreateBucketConfiguration")
            .elem("LocationConstraint").text(location);
        return builder.asString();
    }

}
