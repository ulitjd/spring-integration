/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.adapter.stream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.integration.message.Message;
import org.springframework.integration.message.MessagingException;
import org.springframework.integration.message.MessageTarget;

/**
 * A target that writes a byte array to an {@link OutputStream}.
 * 
 * @author Mark Fisher
 */
public class ByteStreamTarget implements MessageTarget {

	private final Log logger = LogFactory.getLog(this.getClass());

	private final BufferedOutputStream stream;


	public ByteStreamTarget(OutputStream stream) {
		this(stream, -1);
	}

	public ByteStreamTarget(OutputStream stream, int bufferSize) {
		if (bufferSize > 0) {
			this.stream = new BufferedOutputStream(stream, bufferSize);
		}
		else {
			this.stream = new BufferedOutputStream(stream);
		}
	}


	public boolean send(Message message) {
		Object payload = message.getPayload();
		if (payload == null) {
			if (logger.isWarnEnabled()) {
				logger.warn(this.getClass().getSimpleName() + " received null object");
			}
			return false;
		}
		try {
			if (payload instanceof String) {
				this.stream.write(((String) payload).getBytes());
			}
			else if (payload instanceof byte[]){
				this.stream.write((byte[]) payload);
			}
			else {
				throw new MessagingException(this.getClass().getSimpleName() +
						" only supports byte array and String-based messages");
			}
			this.stream.flush();
			return true;
		}
		catch (IOException e) {
			throw new MessagingException("IO failure occurred in target", e);
		}
	}

}
