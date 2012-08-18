package ezvcard.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.CompatibilityMode;
import ezvcard.parameters.ValueParameter;

/*
 Copyright (c) 2012, Michael Angstadt
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
 * @author Michael Angstadt
 */
public class GeoTypeTest {
	@Test
	public void marshal() throws Exception {
		VCardVersion version;
		List<String> warnings = new ArrayList<String>();
		CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
		String expected, actual;
		GeoType t = new GeoType(-12.34, 56.78777);
		VCardSubTypes subTypes;

		//2.1
		version = VCardVersion.V2_1;
		expected = "-12.34;56.7878";
		actual = t.marshalValue(version, warnings, compatibilityMode);
		subTypes = t.marshalSubTypes(version, warnings, compatibilityMode, null);
		assertEquals(expected, actual);
		assertNull(subTypes.getValue());

		//3.0
		version = VCardVersion.V3_0;
		expected = "-12.34;56.7878";
		actual = t.marshalValue(version, warnings, compatibilityMode);
		subTypes = t.marshalSubTypes(version, warnings, compatibilityMode, null);
		assertEquals(expected, actual);
		assertNull(subTypes.getValue());

		//4.0
		version = VCardVersion.V4_0;
		expected = "geo:-12.34,56.7878";
		actual = t.marshalValue(version, warnings, compatibilityMode);
		subTypes = t.marshalSubTypes(version, warnings, compatibilityMode, null);
		assertEquals(expected, actual);
		assertEquals(ValueParameter.URI, subTypes.getValue());
	}

	@Test
	public void unmarshal() throws Exception {
		VCardVersion version;
		List<String> warnings = new ArrayList<String>();
		CompatibilityMode compatibilityMode = CompatibilityMode.RFC;
		VCardSubTypes subTypes = new VCardSubTypes();
		GeoType t;

		//2.1
		version = VCardVersion.V2_1;
		t = new GeoType();
		t.unmarshalValue(subTypes, "-12.34;56.7878", version, warnings, compatibilityMode);
		assertEquals(-12.34, t.getLatitude(), 0.00001);
		assertEquals(56.7878, t.getLongitude(), 0.00001);

		//3.0
		version = VCardVersion.V3_0;
		t = new GeoType();
		t.unmarshalValue(subTypes, "-12.34;56.7878", version, warnings, compatibilityMode);
		assertEquals(-12.34, t.getLatitude(), 0.00001);
		assertEquals(56.7878, t.getLongitude(), 0.00001);

		//4.0
		version = VCardVersion.V4_0;
		t = new GeoType();
		t.unmarshalValue(subTypes, "geo:-12.34,56.7878", version, warnings, compatibilityMode);
		assertEquals(-12.34, t.getLatitude(), 0.00001);
		assertEquals(56.7878, t.getLongitude(), 0.00001);

		//bad value
		warnings.clear();
		t = new GeoType();
		t.unmarshalValue(subTypes, "not a;number", version, warnings, compatibilityMode);
		assertEquals(1, warnings.size());

		//bad value
		warnings.clear();
		t = new GeoType();
		t.unmarshalValue(subTypes, "bad-value", version, warnings, compatibilityMode);
		assertEquals(1, warnings.size());
	}
}
