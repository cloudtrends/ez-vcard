package ezvcard.types;

import java.util.List;
import java.util.Set;

import ezvcard.VCard;
import ezvcard.VCardSubTypes;
import ezvcard.VCardVersion;
import ezvcard.io.CompatibilityMode;
import ezvcard.io.EmbeddedVCardException;
import ezvcard.io.SkipMeException;
import ezvcard.parameters.ValueParameter;
import ezvcard.util.HCardUtils;
import ezvcard.util.VCardStringUtils;

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
 * An embedded vCard or URL containing the information of someone who represents
 * the person.
 * 
 * <pre>
 * //URL
 * VCard vcard = new VCard();
 * AgentType agent = new AgentType(&quot;http://mi5.gov.uk/007&quot;);
 * vcard.setAgent(agent);
 * 
 * //vCard
 * VCard vcard = new VCard();
 * VCard agentVcard = new VCard();
 * agentVcard.setFormattedName(new FormattedNameType(&quot;Agent 007&quot;));
 * AgentType agent = new AgentType(agentVcard);
 * vcard.setAgent(agent);
 * </pre>
 * 
 * <p>
 * vCard property name: AGENT
 * </p>
 * <p>
 * vCard versions: 2.1, 3.0
 * </p>
 * 
 * @author Michael Angstadt
 */
public class AgentType extends VCardType {
	public static final String NAME = "AGENT";

	private String url;
	private VCard vcard;

	public AgentType() {
		super(NAME);
	}

	/**
	 * @param url a URL pointing to the agent's information
	 */
	public AgentType(String url) {
		super(NAME);
		setUrl(url);
	}

	/**
	 * @param vcard a vCard containing the agent's information
	 */
	public AgentType(VCard vcard) {
		super(NAME);
		setVCard(vcard);
	}

	/**
	 * Gets the URL to the agent's information.
	 * @return the URL or null if not set
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL to the agent's information.
	 * @param url the URL
	 */
	public void setUrl(String url) {
		this.url = url;
		vcard = null;
	}

	/**
	 * Gets an embedded vCard with the agent's information.
	 * @return the vCard or null if not set
	 */
	public VCard getVCard() {
		return vcard;
	}

	/**
	 * Sets an embedded vCard with the agent's information.
	 * @param vcard the vCard
	 */
	public void setVCard(VCard vcard) {
		this.vcard = vcard;
		url = null;
	}

	@Override
	public VCardVersion[] getSupportedVersions() {
		return new VCardVersion[] { VCardVersion.V2_1, VCardVersion.V3_0 };
	}

	@Override
	protected void doMarshalSubTypes(VCardSubTypes copy, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode, VCard vcard) {
		if (url != null) {
			ValueParameter vp = (version == VCardVersion.V2_1) ? ValueParameter.URL : ValueParameter.URI;
			copy.setValue(vp);
		} else {
			copy.setValue(null);
		}
	}

	@Override
	protected void doMarshalValue(StringBuilder sb, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		if (url != null) {
			sb.append(url);
		} else if (vcard != null) {
			throw new EmbeddedVCardException(vcard);
		} else {
			throw new SkipMeException("Property has neither a URL nor an embedded vCard.");
		}
	}

	@Override
	protected void doUnmarshalValue(String value, VCardVersion version, List<String> warnings, CompatibilityMode compatibilityMode) {
		if (subTypes.getValue() != null) {
			setUrl(VCardStringUtils.unescape(value));
		} else {
			throw new EmbeddedVCardException(new Injector());
		}
	}

	@Override
	protected void doUnmarshalHtml(org.jsoup.nodes.Element element, List<String> warnings) {
		Set<String> classes = element.classNames();
		if (classes.contains("vcard")) {
			throw new EmbeddedVCardException(new Injector());
		} else {
			String href = HCardUtils.getAbsUrl(element, "href");
			if (href.length() > 0) {
				setUrl(HCardUtils.getElementValue(element));
			}
		}
	}

	private class Injector implements EmbeddedVCardException.InjectionCallback {
		public void injectVCard(VCard vcard) {
			setVCard(vcard);
		}
	}
}
