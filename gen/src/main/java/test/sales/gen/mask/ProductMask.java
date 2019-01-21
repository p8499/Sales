package test.sales.gen.mask;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import test.sales.gen.Mask;

@JsonSerialize(using = ProductMask.Serializer.class)
public class ProductMask implements Mask<ProductMask> {
  protected boolean imid = false;

  public boolean getImid() {
    return imid;
  }

  public ProductMask setImid(boolean imid) {
    this.imid = imid;
    return this;
  }

  protected boolean imname = false;

  public boolean getImname() {
    return imname;
  }

  public ProductMask setImname(boolean imname) {
    this.imname = imname;
    return this;
  }

  protected boolean imprice = false;

  public boolean getImprice() {
    return imprice;
  }

  public ProductMask setImprice(boolean imprice) {
    this.imprice = imprice;
    return this;
  }

  protected boolean imamount = false;

  public boolean getImamount() {
    return imamount;
  }

  public ProductMask setImamount(boolean imamount) {
    this.imamount = imamount;
    return this;
  }

  public ProductMask(boolean imid, boolean imname, boolean imprice, boolean imamount) {
    this.imid = imid;
    this.imname = imname;
    this.imprice = imprice;
    this.imamount = imamount;
  }

  public ProductMask() {}

  @Override
  public ProductMask all(boolean b) {
    this.imid = b;
    this.imname = b;
    this.imprice = b;
    this.imamount = b;
    return this;
  }

  @Override
  public ProductMask keys(boolean b) {
    this.imid = b;
    return this;
  }

  @Override
  public ProductMask attributes(boolean b) {
    this.imname = b;
    this.imprice = b;
    return this;
  }

  @Override
  public ProductMask physicals(boolean b) {
    this.imid = b;
    this.imname = b;
    this.imprice = b;
    return this;
  }

  @Override
  public ProductMask virtuals(boolean b) {
    this.imamount = b;
    return this;
  }

  @Override
  public boolean get(String p) {
    if (p.equals("imid")) return imid;
    else if (p.equals("imname")) return imname;
    else if (p.equals("imprice")) return imprice;
    else if (p.equals("imamount")) return imamount;
    return false;
  }

  @Override
  public ProductMask set(String p, boolean b) {
    if (p.equals("imid")) this.imid = b;
    else if (p.equals("imname")) this.imname = b;
    else if (p.equals("imprice")) this.imprice = b;
    else if (p.equals("imamount")) this.imamount = b;
    return this;
  }

  public static class Serializer extends JsonSerializer<ProductMask> {
    @Override
    public void serialize(ProductMask value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException, JsonProcessingException {
      gen.writeStartObject();
      if (value.getImid()) {
        gen.writeFieldName("imid");
        gen.writeBoolean(value.getImid());
      }
      if (value.getImname()) {
        gen.writeFieldName("imname");
        gen.writeBoolean(value.getImname());
      }
      if (value.getImprice()) {
        gen.writeFieldName("imprice");
        gen.writeBoolean(value.getImprice());
      }
      if (value.getImamount()) {
        gen.writeFieldName("imamount");
        gen.writeBoolean(value.getImamount());
      }
      gen.writeEndObject();
    }
  }
}