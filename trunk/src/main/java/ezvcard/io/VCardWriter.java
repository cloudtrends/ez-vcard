package ezvcard.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ezvcard.VCard;
import ezvcard.VCardException;
import ezvcard.VCardVersion;
import ezvcard.parameters.TypeParameter;
import ezvcard.types.TextType;
import ezvcard.types.VCardType;

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
 * Converts vCards to string representations.
 * @author Michael Angstadt
 */
public class VCardWriter implements Closeable {
	private CompatibilityMode compatibilityMode = CompatibilityMode.RFC2426;
	private VCardVersion targetVersion = VCardVersion.V3_0;
	private ParameterTypeStyle parameterTypeStyle = ParameterTypeStyle.PARAMETER_VALUE_LIST;
	private String newline = "\r\n";
	private boolean addGenerator = true;
	private List<String> warnings = new ArrayList<String>();
	private final Writer writer;

	public VCardWriter(Writer writer) {
		this(writer, VCardVersion.V3_0);
	}

	public VCardWriter(Writer writer, VCardVersion targetVersion) {
		this(writer, targetVersion, FoldingScheme.MIME_DIR);
	}

	public VCardWriter(Writer writer, VCardVersion targetVersion, FoldingScheme foldingScheme) {
		this(writer, targetVersion, foldingScheme, "\r\n");
	}

	public VCardWriter(Writer writer, VCardVersion targetVersion, FoldingScheme foldingScheme, String newline) {
		if (foldingScheme == null) {
			this.writer = writer;
		} else {
			this.writer = new FoldedLineWriter(writer, foldingScheme.getMaxChars(), foldingScheme.getIndent(), newline);
		}
		this.targetVersion = targetVersion;
		this.newline = newline;
	}

	public void setCompatibilityMode(CompatibilityMode compatibilityMode) {
		this.compatibilityMode = compatibilityMode;
	}

	public void setTargetVersion(VCardVersion targetVersion) {
		this.targetVersion = targetVersion;
	}

	public void setParameterTypeStyle(ParameterTypeStyle parameterTypeStyle) {
		this.parameterTypeStyle = parameterTypeStyle;
	}

	/**
	 * Sets whether or not to add a "X-GENERATOR" type to the vCard, saying that
	 * it was generated by this library.
	 * @param addGenerator true to add this custom type, false not to (defaults
	 * to true)
	 */
	public void setAddGenerator(boolean addGenerator) {
		this.addGenerator = addGenerator;
	}

	public List<String> getWarnings() {
		return new ArrayList<String>(warnings);
	}

	public void write(VCard vcard) throws VCardException, IOException {
		warnings.clear();

		if (targetVersion == VCardVersion.V2_1 || targetVersion == VCardVersion.V3_0) {
			if (vcard.getStructuredName() == null) {
				warnings.add("vCard version " + targetVersion + " requires that a structured name be defined.");
			}
		}

		if (targetVersion == VCardVersion.V3_0) {
			if (vcard.getFormattedName() == null) {
				warnings.add("vCard version " + targetVersion + " requires that a formatted name be defined.");
			}
		}

		List<VCardType> types = new ArrayList<VCardType>();
		types.add(new TextType("BEGIN", "vcard"));
		types.add(new TextType("VERSION", targetVersion.getVersion()));

		//use reflection to get all VCardType fields in the VCard class
		//the order that the Types are in doesn't matter (except for BEGIN, END, and VERSION)
		for (Field f : vcard.getClass().getDeclaredFields()) {
			try {
				f.setAccessible(true);
				Object value = f.get(vcard);
				if (value instanceof VCardType) {
					VCardType type = (VCardType) value;
					types.add(type);
				} else if (value instanceof Collection) {
					Collection<?> collection = (Collection<?>) value;
					for (Object obj : collection) {
						if (obj instanceof VCardType) {
							VCardType type = (VCardType) obj;
							types.add(type);
						}
					}
				}
			} catch (IllegalArgumentException e) {
				//shouldn't be thrown because we're passing the correct object into Field.get()
			} catch (IllegalAccessException e) {
				//shouldn't be thrown because we're calling Field.setAccessible(true)
			}
		}

		//add custom types
		for (VCardType customType : vcard.getCustomTypes().values()) {
			types.add(customType);
		}

		//add a custom type saying it was generated by EZ vCard
		if (addGenerator) {
			types.add(new TextType("X-GENERATOR", "EZ vCard v0.1 http://code.google.com/p/ez-vcard"));
		}

		types.add(new TextType("END", "vcard"));

		for (VCardType type : types) {
			//marshal the type
			//in addition to generating the value, this also populates the type's Sub Type's
			String value;
			try {
				List<String> warnings = new ArrayList<String>();
				value = type.marshalValue(targetVersion, warnings, compatibilityMode);
			} finally {
				this.warnings.addAll(warnings);
			}
			if (value == null) {
				warnings.add(type.getTypeName() + " type has requested that it not be marshalled.");
				continue;
			}

			StringBuilder sb = new StringBuilder();

			//write the group
			if (type.getGroup() != null) {
				sb.append(type.getGroup());
				sb.append('.');
			}

			//write the type name
			sb.append(type.getTypeName());

			//write the Sub Types
			for (String subTypeName : type.getSubTypes().getNames()) {
				Set<String> subTypeValues = type.getSubTypes().get(subTypeName);
				if (!subTypeValues.isEmpty()) {
					//TODO put quotes around values that have special chars

					if (TypeParameter.NAME.equalsIgnoreCase(subTypeName)) {
						//handle the TYPE sub type

						if (targetVersion == VCardVersion.V3_0 || targetVersion == VCardVersion.V4_0) {
							//in v3.0, the TYPE Sub Type can look like this:
							//ADR;TYPE=home,work: ...
							//or like this:
							//ADR;TYPE=home;TYPE=work: ...
							switch (parameterTypeStyle) {
							case PARAMETER_VALUE_LIST:
								sb.append(";").append(subTypeName).append("=");
								for (String subTypeValue : subTypeValues) {
									sb.append(subTypeValue).append(',');
								}
								sb.deleteCharAt(sb.length() - 1); //chomp last comma
								break;
							case PARAMETER_LIST:
								for (String subTypeValue : subTypeValues) {
									sb.append(';').append(subTypeName).append('=');
									sb.append(subTypeValue);
								}
								break;
							}
						} else {
							//in v2.1, the TYPE Sub Type looks like this:
							//ADR;HOME;WORK: ...
							for (String subTypeValue : subTypeValues) {
								sb.append(';').append(subTypeValue);
							}
						}
					} else {
						switch (parameterTypeStyle) {
						case PARAMETER_VALUE_LIST:
							sb.append(";").append(subTypeName).append("=");
							for (String subTypeValue : subTypeValues) {
								sb.append(subTypeValue).append(',');
							}
							sb.deleteCharAt(sb.length() - 1); //chomp last comma
							break;
						case PARAMETER_LIST:
							for (String subTypeValue : subTypeValues) {
								sb.append(';').append(subTypeName).append('=');
								sb.append(subTypeValue);
							}
							break;
						}
					}
				}
			}

			sb.append(": ");
			
			//write the value
			sb.append(value);

			writer.write(sb.toString());
			writer.write(newline);
		}
	}

	public void close() throws IOException {
		writer.close();
	}
}
