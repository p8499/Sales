package test.sales.gen.mask;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import test.sales.gen.Mask;

@JsonSerialize(using = EmployeeMask.Serializer.class)
public class EmployeeMask implements Mask<EmployeeMask> {
  protected boolean emid = false;

  public boolean getEmid() {
    return emid;
  }

  public EmployeeMask setEmid(boolean emid) {
    this.emid = emid;
    return this;
  }

  protected boolean emstatus = false;

  public boolean getEmstatus() {
    return emstatus;
  }

  public EmployeeMask setEmstatus(boolean emstatus) {
    this.emstatus = emstatus;
    return this;
  }

  protected boolean emgender = false;

  public boolean getEmgender() {
    return emgender;
  }

  public EmployeeMask setEmgender(boolean emgender) {
    this.emgender = emgender;
    return this;
  }

  protected boolean emname = false;

  public boolean getEmname() {
    return emname;
  }

  public EmployeeMask setEmname(boolean emname) {
    this.emname = emname;
    return this;
  }

  protected boolean emamount = false;

  public boolean getEmamount() {
    return emamount;
  }

  public EmployeeMask setEmamount(boolean emamount) {
    this.emamount = emamount;
    return this;
  }

  public EmployeeMask(
      boolean emid, boolean emstatus, boolean emgender, boolean emname, boolean emamount) {
    this.emid = emid;
    this.emstatus = emstatus;
    this.emgender = emgender;
    this.emname = emname;
    this.emamount = emamount;
  }

  public EmployeeMask() {}

  @Override
  public EmployeeMask all(boolean b) {
    this.emid = b;
    this.emstatus = b;
    this.emgender = b;
    this.emname = b;
    this.emamount = b;
    return this;
  }

  @Override
  public EmployeeMask keys(boolean b) {
    this.emid = b;
    return this;
  }

  @Override
  public EmployeeMask attributes(boolean b) {
    this.emstatus = b;
    this.emgender = b;
    this.emname = b;
    return this;
  }

  @Override
  public EmployeeMask physicals(boolean b) {
    this.emid = b;
    this.emstatus = b;
    this.emgender = b;
    this.emname = b;
    return this;
  }

  @Override
  public EmployeeMask virtuals(boolean b) {
    this.emamount = b;
    return this;
  }

  @Override
  public boolean get(String p) {
    if (p.equals("emid")) return emid;
    else if (p.equals("emstatus")) return emstatus;
    else if (p.equals("emgender")) return emgender;
    else if (p.equals("emname")) return emname;
    else if (p.equals("emamount")) return emamount;
    return false;
  }

  @Override
  public EmployeeMask set(String p, boolean b) {
    if (p.equals("emid")) this.emid = b;
    else if (p.equals("emstatus")) this.emstatus = b;
    else if (p.equals("emgender")) this.emgender = b;
    else if (p.equals("emname")) this.emname = b;
    else if (p.equals("emamount")) this.emamount = b;
    return this;
  }

  public static class Serializer extends JsonSerializer<EmployeeMask> {
    @Override
    public void serialize(EmployeeMask value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException, JsonProcessingException {
      gen.writeStartObject();
      if (value.getEmid()) {
        gen.writeFieldName("emid");
        gen.writeBoolean(value.getEmid());
      }
      if (value.getEmstatus()) {
        gen.writeFieldName("emstatus");
        gen.writeBoolean(value.getEmstatus());
      }
      if (value.getEmgender()) {
        gen.writeFieldName("emgender");
        gen.writeBoolean(value.getEmgender());
      }
      if (value.getEmname()) {
        gen.writeFieldName("emname");
        gen.writeBoolean(value.getEmname());
      }
      if (value.getEmamount()) {
        gen.writeFieldName("emamount");
        gen.writeBoolean(value.getEmamount());
      }
      gen.writeEndObject();
    }
  }
}