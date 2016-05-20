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

import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import net.markenwerk.commons.nulls.NullReader;
import net.markenwerk.commons.nulls.NullWriter;

/**
 * {@link AbstractTextFetcher} is a sensible base implementation of
 * {@link TextFetcher}.
 * 
 * <p>
 * Implementers must only implement a single simplified method that copies all
 * characters from an {@link Readable} to an {@link Appendable}:
 * {@link AbstractTextFetcher#doCopy(Reader, Writer, TextFetchProgressListener)}.
 * 
 * 
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public abstract class AbstractTextFetcher implements TextFetcher {

	private static final Readable NULL_READER = new NullReader();

	private static final Appendable NULL_WRITER = new NullWriter();

	private static final TextFetchProgressListener NULL_LISTENER = new IdleTextFetchProgressListener();

	@Override
	public final char[] fetch(Readable in) throws TextFetchException {
		return fetch(in, null, false);
	}

	@Override
	public final char[] fetch(Readable in, boolean close) throws TextFetchException {
		return fetch(in, null, close);
	}

	@Override
	public final char[] fetch(Readable in, TextFetchProgressListener listener) throws TextFetchException {
		return fetch(in, listener, false);
	}

	@Override
	public final char[] fetch(Readable in, TextFetchProgressListener listener, boolean close) throws TextFetchException {
		CharArrayWriter out = new CharArrayWriter();
		copy(in, out, listener, close, true);
		return out.toCharArray();
	}

	@Override
	public final String read(Readable in) throws TextFetchException {
		return new String(fetch(in, null, false));
	}

	@Override
	public final String read(Readable in, boolean close) throws TextFetchException {
		return new String(fetch(in, null, close));
	}

	@Override
	public final String read(Readable in, TextFetchProgressListener listener) throws TextFetchException {
		return new String(fetch(in, listener, false));
	}

	@Override
	public final String read(Readable in, TextFetchProgressListener listener, boolean close) throws TextFetchException {
		return new String(fetch(in, listener, close));
	}

	@Override
	public final void copy(Readable in, Appendable out) throws TextFetchException {
		copy(in, out, false, false);
	}

	@Override
	public final void copy(Readable in, Appendable out, boolean closeIn, boolean closeOut) throws TextFetchException {
		copy(in, out, null, closeIn, closeOut);
	}

	@Override
	public final void copy(Readable in, Appendable out, TextFetchProgressListener listener) throws TextFetchException {
		copy(in, out, listener, false, false);
	}

	@Override
	public final void copy(Readable in, Appendable out, TextFetchProgressListener listener, boolean closeIn,
			boolean closeOut) throws TextFetchException {
		if (null == in) {
			in = NULL_READER;
		}
		if (null == out) {
			out = NULL_WRITER;
		}
		if (null == listener) {
			listener = NULL_LISTENER;
		}
		doCopy(in, out, listener, closeIn, closeOut);
	}

	private void doCopy(Readable in, Appendable out, TextFetchProgressListener listener, boolean closeIn,
			boolean closeOut) throws TextFetchException {
		doCopy(makeReader(in), makeWriter(out), listener, closeIn, closeOut);
	}

	private void doCopy(Reader in, Writer out, TextFetchProgressListener listener, boolean closeIn, boolean closeOut)
			throws TextFetchException {
		try {
			doCopy(in, out, listener);
		} finally {
			if (closeIn) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (closeOut) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

	}

	private Reader makeReader(final Readable in) {
		if (in instanceof Reader) {
			return (Reader) in;
		} else {
			return new Reader() {
				@Override
				public int read(char[] buffer, int offset, int length) throws IOException {
					return in.read(CharBuffer.wrap(buffer, offset, length));
				}

				@Override
				public void close() throws IOException {
					if (in instanceof Closeable) {
						((Closeable) in).close();
					}
				}
			};
		}
	}

	private Writer makeWriter(final Appendable out) {
		if (out instanceof Writer) {
			return (Writer) out;
		} else {
			return new Writer() {

				@Override
				public void write(char[] buffer, int offset, int length) throws IOException {
					out.append(CharBuffer.wrap(buffer, offset, length));
				}

				@Override
				public void flush() throws IOException {
					if (out instanceof Flushable) {
						((Flushable) out).flush();
					}
				}

				@Override
				public void close() throws IOException {
					if (out instanceof Closeable) {
						((Closeable) out).close();
					}
				}
			};
		}
	}

	/**
	 * Copies the content of a given {@link Readable} into a given
	 * {@link Appendable}.
	 * 
	 * <p>
	 * It is guaranteed that neither the given {@link Readable} nor the given
	 * {@link Appendable} nor the given {@link TextFetchProgressListener} is
	 * {@literal null}.
	 * 
	 * <p>
	 * Implementers must not close either of the given streams.
	 * 
	 * @param in
	 *            The {@link Readable} to read from.
	 * @param out
	 *            The {@link Appendable} to write to.
	 * @param listener
	 *            The {@link TextFetchProgressListener} to report to.
	 * @throws TextFetchException
	 *             If anything went wrong while reading from the given
	 *             {@link Readable} or writing to the given {@link Appendable}.
	 */
	protected abstract void doCopy(Reader in, Writer out, TextFetchProgressListener listener) throws TextFetchException;

}
