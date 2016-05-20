/*
 * Copyright (c) 2015, 2016 Torsten Krause, Markenwerk GmbH
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

import java.io.Reader;

/**
 * @author Torsten Krause (tk at markenwerk dot net)
 * @since 1.0.0
 */
public interface TextFetchProgressListener {

	/**
	 * Indicates that the process of fetching characters from a {@link Reader}
	 * has started.
	 */
	public void onStarted();

	/**
	 * Indicates that the process of fetching characters from a {@link Reader}
	 * has progressed.
	 * 
	 * <p>
	 * The progress will only be reported if the {@link TextFetcher} is capable
	 * of monitoring the progress.
	 * 
	 * @param charactersFetched
	 *            Total amount of characters fetched so far.
	 */
	public void onProgress(long charactersFetched);

	/**
	 * Indicates that the process of fetching characters from a {@link Reader}
	 * has succeeded.
	 * 
	 * @param charactersFetched
	 *            Total total amount of characters fetched or {@literal null},
	 *            if the {@link TextFetcher} is not capable of monitoring the
	 *            progress.
	 */
	public void onSuccedded(Long charactersFetched);

	/**
	 * Indicates that the process of fetching characters from a {@link Reader}
	 * has failed.
	 * 
	 * @param exception
	 *            The {@link TextFetchException} that caused the process to
	 *            fail. This is the same {@link TextFetchException} that is
	 *            thrown in the failing method of {@link TextFetcher} that this
	 *            {@link TextFetchProgressListener} has been given to.
	 * 
	 * @param charactersFetched
	 *            Total total amount of characters fetched before the process
	 *            failed or {@literal null}, if the {@link TextFetcher} is not
	 *            capable of monitoring the progress.
	 */
	public void onFailed(TextFetchException exception, Long charactersFetched);

	/**
	 * Indicates that the process of fetching characters from a {@link Reader}
	 * has finished.
	 * 
	 * <p>
	 * This method will be called after either
	 * {@link TextFetchProgressListener#onSuccedded(Long)} or
	 * {@link TextFetchProgressListener#onFailed(TextFetchException, Long)} has
	 * been called.
	 */
	public void onFinished();

}
