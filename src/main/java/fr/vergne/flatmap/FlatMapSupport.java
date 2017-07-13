package fr.vergne.flatmap;

import java.io.File;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FlatMapSupport {

	/**
	 * This method allows to apply {@link Stream#flatMap(Function)} in a recursive
	 * way. In other words, when an element of a given type can be translated as a
	 * {@link Stream} of sub-elements of the same type, one should wonder whether or
	 * not the sub-elements could also be flatten. For example, a {@link File} which
	 * represents a directory can be considered as a {@link Stream} of {@link File}
	 * which represents its content, but each {@link File} in it might also be a
	 * directory that we should flatten. By using this method, one can specify that
	 * such recursive flattening should be applied, as long as the provided criteria
	 * is met (in our example, as long as we find a directory).
	 * 
	 * @param stream
	 *            the {@link Stream} to use as a source
	 * @param flattenCriteria
	 *            the {@link Predicate} telling whether or not an element needs to
	 *            be flatten
	 * @param flatten
	 *            the {@link Function} which flatten an element into another
	 *            {@link Stream}
	 * @return a {@link Stream} recursively flattening the source {@link Stream}
	 */
	public static <T> Stream<T> recursiveFlatMap(Stream<T> stream, Predicate<T> flattenCriteria,
			Function<T, Stream<T>> flatten) {
		return stream.flatMap((x) -> {
			if (flattenCriteria.test(x)) {
				return recursiveFlatMap(flatten.apply(x), flattenCriteria, flatten);
			} else {
				return Stream.of(x);
			}
		});
	}

	/**
	 * This method is equivalent to
	 * {@link #recursiveFlatMap(Stream, Predicate, Function)}, excepted that it
	 * takes an element to flatten instead of a {@link Stream}. For instance, to
	 * flatten recursively a directory into all the files and sub-files it contains,
	 * one can use this method to provide directly the {@link File} representing the
	 * root directory instead of the {@link Stream} of {@link File} instances which
	 * corresponds to it.
	 */
	public static <T> Stream<T> recursiveFlatMapFromRoot(T root, Predicate<T> flattenCriteria,
			Function<T, Stream<T>> flatten) {
		return recursiveFlatMap(Stream.of(root), flattenCriteria, flatten);
	}

}
