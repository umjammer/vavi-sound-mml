/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;


/**
 * resx -> properties.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-07-04 nsano initial version <br>
 */
public class ResxTest {

    @WebScraper(
            url = "classpath:Resources.ja.resx",
            value="/root/data")
    public static class Resx {
        @Target("/data/@name")
        String name;
        @Target("/data/value/text()")
        String value;
        @Override public String toString() {
            return name + "=" + value.replace("\n", "\\\n");
        }
    }

    /**
     *
     */
    public static void main(String[] args) throws Exception {
        WebScraper.Util.foreach(Resx.class, System.err::println);
    }

    @Test
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test() throws Exception {
        main(null);
    }
}
