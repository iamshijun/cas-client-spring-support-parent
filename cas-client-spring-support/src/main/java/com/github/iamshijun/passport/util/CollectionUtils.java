package com.github.iamshijun.passport.util;

import java.util.Collection;

/**
 * @author aimysaber@gmail.com
 *
 */
public class CollectionUtils {
	
	public static boolean isNotEmpty(Collection<?> coll) {
		return !isEmpty(coll);
	}
	public static boolean isEmpty(Collection<?> coll) {
		return (coll == null || coll.isEmpty());
	}
}
