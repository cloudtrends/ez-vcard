package ezvcard.util;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

/*
 Copyright (c) 2013, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those
 of the authors and should not be interpreted as representing official policies, 
 either expressed or implied, of the FreeBSD Project.
 */

/**
 * <p>
 * Represents a data URI.
 * </p>
 * <p>
 * Example: <code>data:image/jpeg;base64,&lt;base64-encoded text&gt;</code>
 * </p>
 * @author Michael Angstadt
 */
public class DataUri {
	protected static final Pattern regex = Pattern.compile("^data:(.*?);base64,(.*)", Pattern.CASE_INSENSITIVE);
	protected byte[] data;
	protected String contentType;

	/**
	 * @param data the binary data
	 * @param contentType the content type (e.g. "image/jpeg")
	 */
	public DataUri(String contentType, byte[] data) {
		this.contentType = contentType;
		this.data = data;
	}

	/**
	 * @param uri the data URI to parse
	 * @throws IllegalArgumentException if the given URI is not a valid data URI
	 */
	public DataUri(String uri) {
		Matcher m = regex.matcher(uri);
		if (m.find()) {
			contentType = m.group(1);
			data = Base64.decodeBase64(m.group(2));
		} else {
			throw new IllegalArgumentException("Invalid data URI: " + uri);
		}
	}

	/**
	 * Gets the binary data.
	 * @return the binary data or null if not set
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Sets the binary data.
	 * @param data the binary data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * Sets the content type.
	 * @return the content type (e.g. "image/jpeg")
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type.
	 * @param contentType the content type (e.g. "image/jpeg")
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Creates a {@link URI} object from this data URI.
	 * @return the {@link URI} object
	 */
	public URI toUri() {
		return URI.create(toString());
	}

	@Override
	public String toString() {
		return "data:" + contentType + ";base64," + Base64.encodeBase64String(data);
	}
}
