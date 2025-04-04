package com.tinyx.codec;

import java.time.ZonedDateTime;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class ZonedDateTimeCodecProvider implements CodecProvider {
  @Override
  public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
    if (aClass == ZonedDateTime.class) {
      return (Codec<T>) new ZonedDateTimeCodec();
    }

    return null;
  }
}
