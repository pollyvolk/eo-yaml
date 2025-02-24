/**
 * Copyright (c) 2016-2023, Mihai Emil Andronache
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.amihaiemil.eoyaml;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * Unit tests for {@link RtYamlPrinter}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 4.3.2
 */
public final class RtYamlPrinterTest {

    /**
     * The provided Writer should be closed after the print is done.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void writerIsBeingClosed() throws Exception {
        final Scalar scalar = new PlainStringScalar("scalar");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final YamlPrinter printer = new RtYamlPrinter(
            new OutputStreamWriter(baos)
        );
        printer.print(scalar);
        MatcherAssert.assertThat(
            baos.toString(),
            Matchers.equalTo(
                "---"
               + System.lineSeparator()
               + "scalar"
               + System.lineSeparator()
               + "..."
            )
        );
        try {
            printer.print(scalar);
            Assert.fail("IOException was expected!");
        } catch (final IOException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.equalTo("Stream closed")
            );
        }
    }

    /**
     * {@link RtYamlPrinter.Escaped} escapes values when encounter special
     * characters or when there are quotations inside and ignores when the
     * values are already escaped with
     * <code>"</code> or <code>'</code>.
     */
    @Test
    public void escapesWhenEncounterSpecialChars(){
        MatcherAssert.assertThat(new RtYamlPrinter
                .Escaped(new PlainStringScalar("Some value?")).value(),
            Matchers.equalTo("\"Some value?\""));
        MatcherAssert.assertThat(new RtYamlPrinter
                .Escaped(new PlainStringScalar("Some value-")).value(),
            Matchers.equalTo("\"Some value-\""));
        MatcherAssert.assertThat(new RtYamlPrinter
                .Escaped(new PlainStringScalar("Some value#")).value(),
            Matchers.equalTo("\"Some value#\""));
        MatcherAssert.assertThat(new RtYamlPrinter
                .Escaped(new PlainStringScalar("'Some value'")).value(),
            Matchers.equalTo("'Some value'"));
        MatcherAssert.assertThat(new RtYamlPrinter
                .Escaped(new PlainStringScalar("Some \"value\"|"))
                .value(),
            Matchers.equalTo("'Some \"value\"|'"));
        MatcherAssert.assertThat(new RtYamlPrinter
                .Escaped(new PlainStringScalar("\"Some value\"")).value(),
            Matchers.equalTo("\"Some value\""));
    }
}
