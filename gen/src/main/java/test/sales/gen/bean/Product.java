package test.sales.gen.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonInclude;
import test.sales.gen.DefaultDateFormatter;
import test.sales.gen.mask.ProductMask;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product implements Parcelable {
  public static final String TABLE = "F4101";
  public static final String VIEW = "F4101View";
  public static final String NAME = "PRODUCT";

  //region imid Product ID
  public static final String FIELD_IMID = "IMID";
  protected Integer imid = null;
  public static final int CONSTRAINT_IMID_LENGTH_INTEGER = 8;
  public static final int CONSTRAINT_IMID_MIN = -99999999;
  public static final int CONSTRAINT_IMID_MAX = 99999999;

  public Integer getImid() {
    return imid;
  }

  public Product setImid(Integer imid) {
    this.imid = imid;
    return this;
  }
  //endregion

  //region imname Product Name
  public static final String FIELD_IMNAME = "IMNAME";
  protected String imname = null;
  public static final int CONSTRAINT_IMNAME_LENGTH_STRING = 64;

  public String getImname() {
    return imname;
  }

  public Product setImname(String imname) {
    this.imname = imname;
    return this;
  }
  //endregion

  //region imprice Product Price
  public static final String FIELD_IMPRICE = "IMPRICE";
  public static final Double DEFAULT_IMPRICE = 0.00;
  protected Double imprice = DEFAULT_IMPRICE;
  public static final int CONSTRAINT_IMPRICE_LENGTH_INTEGER = 6;
  public static final int CONSTRAINT_IMPRICE_LENGTH_FRACTION = 2;

  public Double getImprice() {
    return imprice;
  }

  public Product setImprice(Double imprice) {
    this.imprice = imprice;
    return this;
  }
  //endregion

  //region imamount Total Sales Amount
  public static final String FIELD_IMAMOUNT = "IMAMOUNT";
  protected Double imamount = null;

  public Double getImamount() {
    return imamount;
  }

  public Product setImamount(Double imamount) {
    this.imamount = imamount;
    return this;
  }
  //endregion

  public Product(Integer imid, String imname, Double imprice, Double imamount) {
    if (imid != null) this.imid = imid;
    if (imname != null) this.imname = imname;
    if (imprice != null) this.imprice = imprice;
    if (imamount != null) this.imamount = imamount;
  }

  public Product() {
    this(null, null, null, null);
  }

  public Product(Parcel in) {
    this.imid = (Integer) in.readValue(Integer.class.getClassLoader());
    this.imname = (String) in.readValue(Integer.class.getClassLoader());
    this.imprice = (Double) in.readValue(Integer.class.getClassLoader());
    this.imamount = (Double) in.readValue(Integer.class.getClassLoader());
  }

  public Product clone() {
    return new Product(imid, imname, imprice, imamount);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Product) ? equals((Product) obj, new ProductMask().all(true)) : false;
  }

  public boolean equals(Product bean, ProductMask mask) {
    if (mask == null) mask = new ProductMask().all(true);
    if (mask.getImid()
        && !(getImid() == null && bean.getImid() == null
            || getImid() != null && bean.getImid() != null && getImid().equals(bean.getImid())))
      return false;
    if (mask.getImname()
        && !(getImname() == null && bean.getImname() == null
            || getImname() != null
                && bean.getImname() != null
                && getImname().equals(bean.getImname()))) return false;
    if (mask.getImprice()
        && !(getImprice() == null && bean.getImprice() == null
            || getImprice() != null
                && bean.getImprice() != null
                && getImprice().equals(bean.getImprice()))) return false;
    if (mask.getImamount()
        && !(getImamount() == null && bean.getImamount() == null
            || getImamount() != null
                && bean.getImamount() != null
                && getImamount().equals(bean.getImamount()))) return false;
    return true;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(imid);
    dest.writeValue(imname);
    dest.writeValue(imprice);
    dest.writeValue(imamount);
  }

  public static final Product.Creator<Product> CREATOR =
      new Creator<Product>() {
        @Override
        public Product[] newArray(int size) {
          return new Product[size];
        }

        @Override
        public Product createFromParcel(Parcel in) {
          return new Product(in);
        }
      };
}