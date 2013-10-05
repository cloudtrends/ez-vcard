package ezvcard.property;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ezvcard.parameter.SoundType;

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
 * A sound to attach to the vCard, such as a pronunciation of the person's name.
 * 
 * <p>
 * <b>Adding a sound</b>
 * </p>
 * 
 * <pre class="brush:java">
 * VCard vcard = new VCard();
 * 
 * //URL
 * Sound sound = new Sound("http://www.mywebsite.com/myname.ogg", SoundType.OGG);
 * vcard.addSound(sound);
 * 
 * //binary data
 * byte data[] = ...
 * sound = new Sound(data, SoundType.OGG);
 * vcard.addSound(sound);
 * 
 * //if "SoundType" does not have the pre-defined constant that you need, then create a new instance
 * //arg 1: the value of the 2.1/3.0 TYPE parameter
 * //arg 2: the value to use for the 4.0 MEDIATYPE parameter and for 4.0 data URIs
 * //arg 3: the file extension of the data type (optional)
 * SoundType param = new SoundType("wav", "audio/wav", "wav");
 * sound = new Sound("http://www.mywebsite.com/myname.wav", SoundType.WAV);
 * vcard.addSound(sound);
 * </pre>
 * 
 * <p>
 * <b>Getting the sounds</b>
 * </p>
 * 
 * <pre class="brush:java">
 * VCard vcard = ...
 * 
 * int fileCount = 0;
 * for (Sound sound : vcard.getSounds()){
 *   //the sound will have either a URL or a binary data
 *   if (sound.getData() == null){
 *     System.out.println("Sound URL: " + sound.getUrl());
 *   } else {
 *     SoundType type = sound.getContentType();
 *     
 *     if (type == null) {
 *       //the vCard may not have any content type data associated with the sound
 *       System.out.println("Saving a sound file...");
 *     } else {
 *       System.out.println("Saving a \"" + type.getMediaType() + "\" file...");
 *     }
 *     
 *     String folder;
 *     if (type == SoundType.OGG){ //it is safe to use "==" instead of "equals()"
 *       folder = "ogg-files";
 *     } else {
 *       folder = "sound-files";
 *     }
 *     
 *     byte data[] = sound.getData();
 *     String filename = "sound" + fileCount;
 *     if (type != null && type.getExtension() != null){
 *     	filename += "." + type.getExtension();
 *     }
 *     OutputStream out = new FileOutputStream(new File(folder, filename));
 *     out.write(data);
 *     out.close();
 *     fileCount++;
 *   }
 * }
 * </pre>
 * 
 * <p>
 * <b>Property name:</b> {@code SOUND}
 * </p>
 * <p>
 * <b>Supported versions:</b> {@code 2.1, 3.0, 4.0}
 * </p>
 * @author Michael Angstadt
 */
public class Sound extends BinaryProperty<SoundType> {
	/**
	 * Creates a sound property.
	 * @param url the URL to the sound file
	 * @param type the content type (e.g. OGG)
	 */
	public Sound(String url, SoundType type) {
		super(url, type);
	}

	/**
	 * Creates a sound property.
	 * @param data the binary data of the sound file
	 * @param type the content type (e.g. OGG)
	 */
	public Sound(byte[] data, SoundType type) {
		super(data, type);
	}

	/**
	 * Creates a sound property.
	 * @param in an input stream to the binary data (will be closed)
	 * @param type the content type (e.g. OGG)
	 * @throws IOException if there's a problem reading from the input stream
	 */
	public Sound(InputStream in, SoundType type) throws IOException {
		super(in, type);
	}

	/**
	 * Creates a sound property.
	 * @param file the sound file
	 * @param type the content type (e.g. OGG)
	 * @throws IOException if there's a problem reading from the file
	 */
	public Sound(File file, SoundType type) throws IOException {
		super(file, type);
	}

	@Override
	public String getLanguage() {
		return super.getLanguage();
	}

	@Override
	public void setLanguage(String language) {
		super.setLanguage(language);
	}
}
