package org.olafneumann.regex.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class RegexGeneratorTest {

	@Test
	public void testRecognizeString() throws Exception {
		final String input = "2020-03-12T12:34:56.123 WARN  [org.olafneumann.test.Test]: This is a simple line";
		final RegexGenerator generator = new RegexGenerator(Configuration.get());

		final Collection<?> actual = generator.recognize(input);

		actual.forEach(System.out::println);

		assertThat(actual).size().isEqualTo(10);
	}

}
