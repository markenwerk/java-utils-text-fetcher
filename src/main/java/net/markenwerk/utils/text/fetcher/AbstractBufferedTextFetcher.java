/*
 * Copyright (c) 2016 Torsten Krause, Markenwerk GmbH
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.markenwerk.utils.text.fetcher;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * {@link AbstractBufferedTextFetcher} is a sensible base implementation of
 * {@link TextFetcher} that uses a {@code char[]} as buffer, to while
 * copying all chars from an {@link Reader} to an {@link Writer} by sequentially
 * reading from the {@link Reader} into the buffer and then writing from the
 * buffer to the {@link Writer}.
 * 
 * <p>
 * If
 * {@link AbstractBufferedTextFetcher#doCopy(Reader, Writer, TextFetchProgressListener)}
 * is called with a {@link Reader} and a {@link Writer}, which should be the
 * most common case, the {@code char[]} buffer is used directly, otherwise a
 * {@link CharBuffer} is wrapped around the {@code char[]} buffer.
 * 
 * <p>
 * Implementers must only implement the methods
 * {@link AbstractBufferedTextFetcher#obtainBuffer()} and
 * {@link AbstractBufferedTextFetcher#returnBuffer(char[])} that manage a
 * {@code char[]} to be used as a buffer in
 * {@link AbstractBufferedTextFetcher#doCopy(Reader, Writer, TextFetchProgressListener)}.
 * 
 * <p>
 * Implementers may also override
 * {@link AbstractBufferedTextFetcher#returnBuffer(char[])}, which is
 * called after
 * {@link AbstractBufferedTextFetcher#doCopy(Reader, Writer, TextFetchProgressListener)}
 * has finished using it.
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public abstract class AbstractBufferedTextFetcher extends AbstractTextFetcher {

	/**
	 * The default buffer size of 1024 characters.
	 */
	protected static final int DEFAULT_BUFEFR_SIZE = 1024;

	/**
	 * Safely creates a new {@literal char[]} to be used as a buffer.
	 * 
	 * @param bufferSize
	 *            The size of the {@literal char[]} to be created. Defaults to
	 *            the
	 *            {@link AbstractBufferedTextFetcher#DEFAULT_BUFEFR_SIZE
	 *            default} buffer size, if the given buffer size is not
	 *            positive.
	 * @return The new {@literal char[]}.
	 */
	protected static final char[] createBuffer(int bufferSize) {
		return new char[bufferSize > 0 ? bufferSize : DEFAULT_BUFEFR_SIZE];
	}

	@Override
	protected final void doCopy(Reader in, Writer out, TextFetchProgressListener listener) throws TextFetchException {
		char[] buffer = obtainBuffer();
		listener.onStarted();
		long total = 0;
		try {
			int length = in.read(buffer);
			while (length != -1) {
				total += length;
				out.write(buffer, 0, length);
				listener.onProgress(total);
				length = in.read(buffer);
			}
			out.flush();
			listener.onProgress(total);
			listener.onSuccedded(total);
		} catch (IOException e) {
			throw createException(listener, total, e);
		} finally {
			listener.onFinished();
			returnBuffer(buffer);
		}
	}

	private TextFetchException createException(TextFetchProgressListener listener, long total, IOException exception) {
		TextFetchException fetchException = new TextFetchException("Fetch failed after " + total + " "
				+ (1 == total ? "char has" : "chars have") + " been copied successully.", exception);
		listener.onFailed(fetchException, total);
		return fetchException;
	}

	/**
	 * Called by
	 * {@link AbstractBufferedTextFetcher#doCopy(Reader, Writer, TextFetchProgressListener)}
	 * to obtain a {@code char[]} to be used as a buffer.
	 * 
	 * <p>
	 * Every {@code char[]} that is returned by this method will be passed as an
	 * argument of {@link AbstractBufferedTextFetcher#returnBuffer(char[])}
	 * after
	 * {@link AbstractBufferedTextFetcher#doCopy(Reader, Writer, TextFetchProgressListener)}
	 * has finished using it.
	 * 
	 * 
	 * @return The a {@code char[]} to be used as a buffer.
	 */
	protected abstract char[] obtainBuffer();

	/**
	 * Called by
	 * {@link AbstractBufferedTextFetcher#doCopy(Reader, Writer, TextFetchProgressListener)}
	 * to return a {@code char[]} that has previously been obtained from
	 * {@link AbstractBufferedTextFetcher#obtainBuffer()}.
	 * 
	 * @param buffer
	 *            The {@code char[]} to be returned.
	 */
	protected abstract void returnBuffer(char[] buffer);

}
