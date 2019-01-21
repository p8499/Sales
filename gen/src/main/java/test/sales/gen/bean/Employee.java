package test.sales.gen.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonInclude;
import test.sales.gen.DefaultDateFormatter;
import test.sales.gen.mask.EmployeeMask;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee implements Parcelable {
  public static final String TABLE = "F0101";
  public static final String VIEW = "F0101View";
  public static final String NAME = "EMPLOYEE";

  //region emid Employee ID
  public static final String FIELD_EMID = "EMID";
  protected Integer emid = null;
  public static final int CONSTRAINT_EMID_LENGTH_INTEGER = 4;
  public static final int CONSTRAINT_EMID_MIN = -9999;
  public static final int CONSTRAINT_EMID_MAX = 9999;

  public Integer getEmid() {
    return emid;
  }

  public Employee setEmid(Integer emid) {
    this.emid = emid;
    return this;
  }
  //endregion

  //region emstatus Employee Status
  public static final String FIELD_EMSTATUS = "EMSTATUS";
  public static final Integer EMSTATUS_VALID = 0;
  public static final Integer EMSTATUS_INVALID = 1;
  public static final Integer DEFAULT_EMSTATUS = 0;
  protected Integer emstatus = DEFAULT_EMSTATUS;
  public static final int CONSTRAINT_EMSTATUS_LENGTH_INTEGER = 1;
  public static final int CONSTRAINT_EMSTATUS_MIN = -9;
  public static final int CONSTRAINT_EMSTATUS_MAX = 9;

  public Integer getEmstatus() {
    return emstatus;
  }

  public Employee setEmstatus(Integer emstatus) {
    this.emstatus = emstatus;
    return this;
  }
  //endregion

  //region emgender Employee Gender
  public static final String FIELD_EMGENDER = "EMGENDER";
  public static final String EMGENDER_MALE = "M";
  public static final String EMGENDER_FEMALE = "F";
  protected String emgender = null;
  public static final int CONSTRAINT_EMGENDER_LENGTH_STRING = 1;

  public String getEmgender() {
    return emgender;
  }

  public Employee setEmgender(String emgender) {
    this.emgender = emgender;
    return this;
  }
  //endregion

  //region emname Employee Name
  public static final String FIELD_EMNAME = "EMNAME";
  protected String emname = null;
  public static final int CONSTRAINT_EMNAME_LENGTH_STRING = 64;

  public String getEmname() {
    return emname;
  }

  public Employee setEmname(String emname) {
    this.emname = emname;
    return this;
  }
  //endregion

  //region emamount Total Sales Amount
  public static final String FIELD_EMAMOUNT = "EMAMOUNT";
  protected Double emamount = null;

  public Double getEmamount() {
    return emamount;
  }

  public Employee setEmamount(Double emamount) {
    this.emamount = emamount;
    return this;
  }
  //endregion

  public Employee(Integer emid, Integer emstatus, String emgender, String emname, Double emamount) {
    if (emid != null) this.emid = emid;
    if (emstatus != null) this.emstatus = emstatus;
    if (emgender != null) this.emgender = emgender;
    if (emname != null) this.emname = emname;
    if (emamount != null) this.emamount = emamount;
  }

  public Employee() {
    this(null, null, null, null, null);
  }

  public Employee(Parcel in) {
    this.emid = (Integer) in.readValue(Integer.class.getClassLoader());
    this.emstatus = (Integer) in.readValue(Integer.class.getClassLoader());
    this.emgender = (String) in.readValue(Integer.class.getClassLoader());
    this.emname = (String) in.readValue(Integer.class.getClassLoader());
    this.emamount = (Double) in.readValue(Integer.class.getClassLoader());
  }

  public Employee clone() {
    return new Employee(emid, emstatus, emgender, emname, emamount);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Employee) ? equals((Employee) obj, new EmployeeMask().all(true)) : false;
  }

  public boolean equals(Employee bean, EmployeeMask mask) {
    if (mask == null) mask = new EmployeeMask().all(true);
    if (mask.getEmid()
        && !(getEmid() == null && bean.getEmid() == null
            || getEmid() != null && bean.getEmid() != null && getEmid().equals(bean.getEmid())))
      return false;
    if (mask.getEmstatus()
        && !(getEmstatus() == null && bean.getEmstatus() == null
            || getEmstatus() != null
                && bean.getEmstatus() != null
                && getEmstatus().equals(bean.getEmstatus()))) return false;
    if (mask.getEmgender()
        && !(getEmgender() == null && bean.getEmgender() == null
            || getEmgender() != null
                && bean.getEmgender() != null
                && getEmgender().equals(bean.getEmgender()))) return false;
    if (mask.getEmname()
        && !(getEmname() == null && bean.getEmname() == null
            || getEmname() != null
                && bean.getEmname() != null
                && getEmname().equals(bean.getEmname()))) return false;
    if (mask.getEmamount()
        && !(getEmamount() == null && bean.getEmamount() == null
            || getEmamount() != null
                && bean.getEmamount() != null
                && getEmamount().equals(bean.getEmamount()))) return false;
    return true;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(emid);
    dest.writeValue(emstatus);
    dest.writeValue(emgender);
    dest.writeValue(emname);
    dest.writeValue(emamount);
  }

  public static final Employee.Creator<Employee> CREATOR =
      new Creator<Employee>() {
        @Override
        public Employee[] newArray(int size) {
          return new Employee[size];
        }

        @Override
        public Employee createFromParcel(Parcel in) {
          return new Employee(in);
        }
      };
}