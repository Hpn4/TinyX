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

  /**
   * Decodes a BSON date into a ZonedDateTime.
   *
   * @param bsonReader The BSON reader used to read the date.
   * @param decoderContext The context for the decoder.
   * @return The Zoned Date Time.
   */
  @Override
  public ZonedDateTime decode(BsonReader bsonReader, DecoderContext decoderContext) {
    return Instant.ofEpochMilli(bsonReader.readDateTime()).atZone(ZoneId.systemDefault());
  }

  /**
   * Encodes a ZonedDateTime into BSON format.
   *
   * @param bsonWriter The BSON writer used to write the date.
   * @param zonedDateTime The ZonedDateTime to encode.
   * @param encoderContext The context for the encoder.
   */
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
