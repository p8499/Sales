package test.sales.gen.mask;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import test.sales.gen.Mask;

@JsonSerialize(using = RecordMask.Serializer.class)
public class RecordMask implements Mask<RecordMask> {
  protected boolean reid = false;

  public boolean getReid() {
    return reid;
  }

  public RecordMask setReid(boolean reid) {
    this.reid = reid;
    return this;
  }

  protected boolean reimid = false;

  public boolean getReimid() {
    return reimid;
  }

  public RecordMask setReimid(boolean reimid) {
    this.reimid = reimid;
    return this;
  }

  protected boolean reemid = false;

  public boolean getReemid() {
    return reemid;
  }

  public RecordMask setReemid(boolean reemid) {
    this.reemid = reemid;
    return this;
  }

  protected boolean reqty = false;

  public boolean getReqty() {
    return reqty;
  }

  public RecordMask setReqty(boolean reqty) {
    this.reqty = reqty;
    return this;
  }

  public RecordMask(boolean reid, boolean reimid, boolean reemid, boolean reqty) {
    this.reid = reid;
    this.reimid = reimid;
    this.reemid = reemid;
    this.reqty = reqty;
  }

  public RecordMask() {}

  @Override
  public RecordMask all(boolean b) {
    this.reid = b;
    this.reimid = b;
    this.reemid = b;
    this.reqty = b;
    return this;
  }

  @Override
  public RecordMask keys(boolean b) {
    this.reid = b;
    return this;
  }

  @Override
  public RecordMask attributes(boolean b) {
    this.reimid = b;
    this.reemid = b;
    this.reqty = b;
    return this;
  }

  @Override
  public RecordMask physicals(boolean b) {
    this.reid = b;
    this.reimid = b;
    this.reemid = b;
    this.reqty = b;
    return this;
  }

  @Override
  public RecordMask virtuals(boolean b) {
    return this;
  }

  @Override
  public boolean get(String p) {
    if (p.equals("reid")) return reid;
    else if (p.equals("reimid")) return reimid;
    else if (p.equals("reemid")) return reemid;
    else if (p.equals("reqty")) return reqty;
    return false;
  }

  @Override
  public RecordMask set(String p, boolean b) {
    if (p.equals("reid")) this.reid = b;
    else if (p.equals("reimid")) this.reimid = b;
    else if (p.equals("reemid")) this.reemid = b;
    else if (p.equals("reqty")) this.reqty = b;
    return this;
  }

  public static class Serializer extends JsonSerializer<RecordMask> {
    @Override
    public void serialize(RecordMask value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException, JsonProcessingException {
      gen.writeStartObject();
      if (value.getReid()) {
        gen.writeFieldName("reid");
        gen.writeBoolean(value.getReid());
      }
      if (value.getReimid()) {
        gen.writeFieldName("reimid");
        gen.writeBoolean(value.getReimid());
      }
      if (value.getReemid()) {
        gen.writeFieldName("reemid");
        gen.writeBoolean(value.getReemid());
      }
      if (value.getReqty()) {
        gen.writeFieldName("reqty");
        gen.writeBoolean(value.getReqty());
      }
      gen.writeEndObject();
    }
  }
}