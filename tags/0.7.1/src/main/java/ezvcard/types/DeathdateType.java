package ezvcard.types;

import java.util.Date;

import ezvcard.VCardSubTypes;
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
 * Defines the person's time of death.
 * 
 * <p>
 * <b>Setting the time of death</b>
 * </p>
 * 
 * <pre>
 * VCard vcard = new VCard();
 * 
 * //complete date
 * Calendar c = Calendar.getInstance();
 * c.set(Calendar.YEAR, 1954);
 * c.set(Calendar.MONTH, Calendar.JUNE);
 * c.set(Calendar.DAY_OF_MONTH, 7);
 * DeathdateType deathdate = new DeathdateType();
 * deathdate.setDate(c.getTime(), false);
 * vcard.setDeathdate(deathdate);
 * 
 * //reduced accuracy date
 * deathdate = new DeathdateType();
 * deathdate.setReducedAccuracyDate(&quot;--0607&quot;); //June 7
 * vcard.setDeathdate(deathdate);
 * 
 * //plain text value
 * deathdate = new DeathdateType();
 * deathdate.setText(&quot;circa 1954&quot;);
 * vcard.setDeathdate(deathdate);
 * </pre>
 * 
 * <p>
 * <b>Getting the time of death</b>
 * </p>
 * 
 * <pre>
 * VCard vcard = ...
 * DeathdateType deathdate = vcard.getDeathdate();
 * if (deathdate != null){
 *   if (deathdate.getDate() != null){
 *     System.out.println(deathdate.getDate());
 *   } else if (deathdate.getReducedAccuracyDate() != null){
 *     System.out.println(deathdate.getReducedAccuracyDate());
 *   } else if (deathdate.getText() != null){
 *     System.out.println(deathdate.getText());
 *   }
 * }
 * </pre>
 * 
 * <p>
 * vCard property name: DEATHDATE
 * </p>
 * <p>
 * vCard versions: 4.0
 * </p>
 * @author Michael Angstadt
 * @see <a href="http://tools.ietf.org/html/rfc6474">RFC 6474</a>
 */
public class DeathdateType extends DateOrTimeType {
	public static final String NAME = "DEATHDATE";

	public DeathdateType() {
		super(NAME);
	}

	/**
	 * @param date the time of death
	 */
	public DeathdateType(Date date) {
		super(NAME, date);
	}

	@Override
	public VCardVersion[] getSupportedVersions() {
		return new VCardVersion[] { VCardVersion.V4_0 };
	}

	/**
	 * Gets the LANGUAGE parameter.
	 * @return the language or null if not set
	 * @see VCardSubTypes#getLanguage
	 */
	public String getLanguage() {
		return subTypes.getLanguage();
	}

	/**
	 * Sets the LANGUAGE parameter.
	 * @param language the language or null to remove
	 * @see VCardSubTypes#setLanguage
	 */
	public void setLanguage(String language) {
		subTypes.setLanguage(language);
	}
}
