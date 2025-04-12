package com.tinyx.codec;

import java.time.ZonedDateTime;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class ZonedDateTimeCodecProvider implements CodecProvider {

  /**
   * Returns a codec for encoding and decoding instances of the specified class.
   *
   * @param <T> The type of the object to be encoded or decoded.
   * @param aClass The class of the object to be handled.
   * @param codecRegistry The codec registry to retrieve the codec from.
   * @return A codec for the specified class, or null if no codec is available.
   */
  @Override
  public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
    if (aClass == ZonedDateTime.class) {
      return (Codec<T>) new ZonedDateTimeCodec();
    }

    return null;
  }
}
