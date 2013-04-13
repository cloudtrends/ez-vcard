package ezvcard.types;

import java.util.List;

import ezvcard.VCardVersion;
import ezvcard.io.CompatibilityMode;

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
 * This just says that the vCard is a vCard, it's not really necessary.
 * 
 * <p>
 * <b>Code sample</b>
 * </p>
 * 
 * <pre>
 * VCard vcard = new VCard();
 * ProfileType profile = new ProfileType();
 * vcard.setProfile(profile);
 * </pre>
 * 
 * <p>
 * <b>Property name:</b> <code>PROFILE</code>
 * </p>
 * <p>
 * <b>Supported versions:</b> <code>3.0</code>
 * </p>
 * @author Michael Angstadt
 */
public class ProfileType extends TextType {
	public static final String NAME = "PROFILE";

	public ProfileType() {
		super(NAME, "VCARD");
	}

	@Override
	protected void doMarshalText(StringBuilder sb, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		if ("VCARD".equalsIgnoreCase(value)) {
			//see RFC 2426 p.5
			warnings.add("The value of the " + NAME + " property should be \"VCARD\", but it is \"" + value + "\"");
		}
		super.doMarshalText(sb, version, warnings, compatibilityMode);
	}

	@Override
	protected void doUnmarshalText(String value, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		if ("VCARD".equalsIgnoreCase(value)) {
			//see RFC 2426 p.5
			warnings.add("The value of the " + ProfileType.NAME + " type should be \"VCARD\", but it is \"" + value + "\"");
		}
		super.doUnmarshalText(value, version, warnings, compatibilityMode);
	}

	@Override
	public VCardVersion[] getSupportedVersions() {
		return new VCardVersion[] { VCardVersion.V3_0 };
	}
}
