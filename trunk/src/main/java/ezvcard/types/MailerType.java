package ezvcard.types;

import ezvcard.VCardVersion;

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
 * The email client that the person uses.
 * 
 * <pre>
 * VCard vcard = new VCard();
 * MailerType mailer = new MailerType(&quot;Thunderbird&quot;);
 * vcard.setMailer(mailer);
 * </pre>
 * 
 * <p>
 * <b>Property name:</b> <code>MAILER</code>
 * </p>
 * <p>
 * <b>Supported versions:</b> <code>2.1, 3.0</code>
 * </p>
 * @author Michael Angstadt
 */
public class MailerType extends TextType {
	public static final String NAME = "MAILER";

	public MailerType() {
		this(null);
	}

	/**
	 * @param emailClient the name of the email client (e.g. "Thunderbird")
	 */
	public MailerType(String emailClient) {
		super(NAME, emailClient);
	}

	@Override
	public VCardVersion[] getSupportedVersions() {
		return new VCardVersion[] { VCardVersion.V2_1, VCardVersion.V3_0 };
	}
}
