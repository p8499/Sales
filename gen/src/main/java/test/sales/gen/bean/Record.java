package test.sales.gen.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonInclude;
import test.sales.gen.DefaultDateFormatter;
import test.sales.gen.mask.RecordMask;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Record implements Parcelable {
  public static final String TABLE = "F4211";
  public static final String VIEW = "F4211View";
  public static final String NAME = "RECORD";

  //region reid Record ID
  public static final String FIELD_REID = "REID";
  protected Integer reid = null;
  public static final int CONSTRAINT_REID_LENGTH_INTEGER = 8;
  public static final int CONSTRAINT_REID_MIN = -99999999;
  public static final int CONSTRAINT_REID_MAX = 99999999;

  public Integer getReid() {
    return reid;
  }

  public Record setReid(Integer reid) {
    this.reid = reid;
    return this;
  }
  //endregion

  //region reimid Product ID
  public static final String FIELD_REIMID = "REIMID";
  protected Integer reimid = null;
  public static final int CONSTRAINT_REIMID_LENGTH_INTEGER = 8;
  public static final int CONSTRAINT_REIMID_MIN = -99999999;
  public static final int CONSTRAINT_REIMID_MAX = 99999999;

  public Integer getReimid() {
    return reimid;
  }

  public Record setReimid(Integer reimid) {
    this.reimid = reimid;
    return this;
  }
  //endregion

  //region reemid Employee ID
  public static final String FIELD_REEMID = "REEMID";
  protected Integer reemid = null;
  public static final int CONSTRAINT_REEMID_LENGTH_INTEGER = 4;
  public static final int CONSTRAINT_REEMID_MIN = -9999;
  public static final int CONSTRAINT_REEMID_MAX = 9999;

  public Integer getReemid() {
    return reemid;
  }

  public Record setReemid(Integer reemid) {
    this.reemid = reemid;
    return this;
  }
  //endregion

  //region reqty Quantity
  public static final String FIELD_REQTY = "REQTY";
  public static final Integer DEFAULT_REQTY = 1;
  protected Integer reqty = DEFAULT_REQTY;
  public static final int CONSTRAINT_REQTY_LENGTH_INTEGER = 4;
  public static final int CONSTRAINT_REQTY_MIN = -9999;
  public static final int CONSTRAINT_REQTY_MAX = 9999;

  public Integer getReqty() {
    return reqty;
  }

  public Record setReqty(Integer reqty) {
    this.reqty = reqty;
    return this;
  }
  //endregion

  public Record(Integer reid, Integer reimid, Integer reemid, Integer reqty) {
    if (reid != null) this.reid = reid;
    if (reimid != null) this.reimid = reimid;
    if (reemid != null) this.reemid = reemid;
    if (reqty != null) this.reqty = reqty;
  }

  public Record() {
    this(null, null, null, null);
  }

  public Record(Parcel in) {
    this.reid = (Integer) in.readValue(Integer.class.getClassLoader());
    this.reimid = (Integer) in.readValue(Integer.class.getClassLoader());
    this.reemid = (Integer) in.readValue(Integer.class.getClassLoader());
    this.reqty = (Integer) in.readValue(Integer.class.getClassLoader());
  }

  public Record clone() {
    return new Record(reid, reimid, reemid, reqty);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Record) ? equals((Record) obj, new RecordMask().all(true)) : false;
  }

  public boolean equals(Record bean, RecordMask mask) {
    if (mask == null) mask = new RecordMask().all(true);
    if (mask.getReid()
        && !(getReid() == null && bean.getReid() == null
            || getReid() != null && bean.getReid() != null && getReid().equals(bean.getReid())))
      return false;
    if (mask.getReimid()
        && !(getReimid() == null && bean.getReimid() == null
            || getReimid() != null
                && bean.getReimid() != null
                && getReimid().equals(bean.getReimid()))) return false;
    if (mask.getReemid()
        && !(getReemid() == null && bean.getReemid() == null
            || getReemid() != null
                && bean.getReemid() != null
                && getReemid().equals(bean.getReemid()))) return false;
    if (mask.getReqty()
        && !(getReqty() == null && bean.getReqty() == null
            || getReqty() != null && bean.getReqty() != null && getReqty().equals(bean.getReqty())))
      return false;
    return true;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(reid);
    dest.writeValue(reimid);
    dest.writeValue(reemid);
    dest.writeValue(reqty);
  }

  public static final Record.Creator<Record> CREATOR =
      new Creator<Record>() {
        @Override
        public Record[] newArray(int size) {
          return new Record[size];
        }

        @Override
        public Record createFromParcel(Parcel in) {
          return new Record(in);
        }
      };
}