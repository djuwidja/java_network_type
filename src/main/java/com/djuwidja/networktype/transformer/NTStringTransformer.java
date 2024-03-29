package com.djuwidja.networktype.transformer;

import com.djuwidja.networktype.NTObject;
import com.djuwidja.networktype.NTString;
/**
 * Transform a {@link String} into {@link NTString}.
 * @author kennethdjuwidja
 * @since 1.0.0
 * @version 1.0.0
 */
class NTStringTransformer implements NTTransformer {
	@Override
	public NTObject transform(Object data) throws IllegalAccessException {
		String value = (String) data;
        return new NTString(value);
	}
}
