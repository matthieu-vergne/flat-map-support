package fr.vergne.flatmap;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class FlatMapSupportTest {

	private class X {
		private final List<X> children;
		private final String content;

		public X(String content) {
			this.children = null;
			this.content = content;
		}

		public X(X child1, X child2) {
			this.children = Arrays.asList(child1, child2);
			this.content = null;
		}

		public List<X> getChildren() {
			return children;
		}

		@Override
		public String toString() {
			if (children != null) {
				return children.stream().map((x) -> x.toString()).reduce((s1, s2) -> s1 + "+" + s2).get();
			} else {
				return content;
			}
		}
	}

	@Test
	public void testRecursiveFlatMapReturnsCorrectStream() {
		X leaf1 = new X("a");
		X leaf2 = new X("b");
		X leaf3 = new X("c");
		X leaf4 = new X("d");

		X parent1 = new X(leaf1, leaf2);
		X parent2 = new X(leaf3, leaf4);

		X root = new X(parent1, parent2);

		Stream<X> stream = Stream.of(root);
		Predicate<X> flattenCriteria = (x) -> x.getChildren() != null;
		Function<X, Stream<X>> flattenFunction = (x) -> x.getChildren().stream();
		Stream<X> flattenStream = FlatMapSupport.recursiveFlatMap(stream, flattenCriteria, flattenFunction);

		List<X> actual = flattenStream.collect(Collectors.toList());
		List<X> expected = Arrays.asList(leaf1, leaf2, leaf3, leaf4);

		assertEquals(expected, actual);
	}

}
