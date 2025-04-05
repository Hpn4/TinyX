package com.tinyx.codec;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class ZonedDateTimeCodec implements Codec<ZonedDateTime> {
  @Override
  public ZonedDateTime decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return Instant.ofEpochMilli(bsonReader.readDateTime()).atZone(ZoneId.systemDefault());
  }

  @Override
  public void encode(
      BsonWriter bsonWriter, ZonedDateTime zonedDateTime, EncoderContext encoderContext) {
    bsonWriter.writeDateTime(zonedDateTime.toInstant().toEpochMilli());
  }

  @Override
  public Class<ZonedDateTime> getEncoderClass() {
    return ZonedDateTime.class;
  }
}
