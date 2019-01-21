package test.sales.gen.stub;

import android.content.Context;
import java.util.List;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import com.fasterxml.jackson.core.JsonProcessingException;
import test.sales.gen.FilterLogicExpr;
import test.sales.gen.OrderByListExpr;
import test.sales.gen.RangeExpr;
import test.sales.gen.RetrofitFactory;
import test.sales.gen.bean.Record;
import test.sales.gen.mask.RecordMask;

public class RecordStub {
  public static final String path = "api/Record/";
  public static final String attachPath = "api/Record_attachment/";
  public static final String pathKey = "{reid}";

  private static RecordStub service;

  public static RecordStub getInstance(Context context) {
    if (service == null) {
      service = new RecordStub(context);
    }
    return service;
  }

  private Api api;

  public RecordStub(Context context) {
    api = RetrofitFactory.getInstance(context).create(Api.class);
  }

  public Flowable<Response<Record>> get(Integer reid) {
    return get(reid, null);
  }

  public Flowable<Response<Record>> get(Integer reid, RecordMask mask) {
    Flowable<Response<Record>> flowable = null;
    try {
      flowable =
          api.get(
                  reid,
                  mask == null ? null : RetrofitFactory.getObjectMapper().writeValueAsString(mask))
              .subscribeOn(Schedulers.io());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return flowable;
  }

  public Flowable<Response<Record>> add(Record bean) {
    Flowable<Response<Record>> flowable = api.add(bean).subscribeOn(Schedulers.io());
    return flowable;
  }

  public Flowable<Response<Record>> update(Record bean) {
    return update(bean, null);
  }

  public Flowable<Response<Record>> update(Record bean, RecordMask mask) {
    Flowable<Response<Record>> flowable = null;
    try {
      flowable =
          api.update(
                  bean.getReid(),
                  bean,
                  mask == null ? null : RetrofitFactory.getObjectMapper().writeValueAsString(mask))
              .subscribeOn(Schedulers.io());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return flowable;
  }

  public Flowable<Response<Void>> count(FilterLogicExpr filter) {
    Flowable<Response<Void>> flowable = null;
    try {
      flowable =
          api.count(
                  filter == null
                      ? null
                      : RetrofitFactory.getObjectMapper().writeValueAsString(filter),
                  new RangeExpr("items", 0L, -1L).toString(),
                  RetrofitFactory.getObjectMapper()
                      .writeValueAsString(new RecordMask().setReid(true)))
              .subscribeOn(Schedulers.io());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return flowable;
  }

  public Flowable<Response<List<Record>>> query(FilterLogicExpr filter, RangeExpr range) {
    return query(filter, null, range, null);
  }

  public Flowable<Response<List<Record>>> query(
      FilterLogicExpr filter, OrderByListExpr orderBy, RangeExpr range) {
    return query(filter, orderBy, range, null);
  }

  public Flowable<Response<List<Record>>> query(
      FilterLogicExpr filter, RangeExpr range, RecordMask mask) {
    return query(filter, null, range, mask);
  }

  public Flowable<Response<List<Record>>> query(
      FilterLogicExpr filter, OrderByListExpr orderBy, RangeExpr range, RecordMask mask) {
    Flowable<Response<List<Record>>> flowable = null;
    try {
      flowable =
          api.query(
                  filter == null
                      ? null
                      : RetrofitFactory.getObjectMapper().writeValueAsString(filter),
                  orderBy == null ? null : orderBy.toQuery(),
                  range == null ? null : range.toString(),
                  mask == null ? null : RetrofitFactory.getObjectMapper().writeValueAsString(mask))
              .subscribeOn(Schedulers.io());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return flowable;
  }

  public Flowable<Response<Void>> delete(Integer reid) {
    return api.delete(reid).subscribeOn(Schedulers.io());
  }

  public Flowable<Response<ResponseBody>> downloadAttachment(Integer reid, String name) {
    Flowable<Response<ResponseBody>> flowable =
        api.downloadAttachment(reid, name, "application/octet-stream").subscribeOn(Schedulers.io());
    return flowable;
  }

  public Flowable<Response<Void>> uploadAttachment(Integer reid, String name, byte[] bytes) {
    RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
    Flowable<Response<Void>> flowable =
        api.uploadAttachment(reid, name, body).subscribeOn(Schedulers.io());
    return flowable;
  }

  public Flowable<Response<Void>> deleteAttachment(Integer reid, String name) {
    Flowable<Response<Void>> flowable =
        api.deleteAttachment(reid, name).subscribeOn(Schedulers.io());
    return flowable;
  }

  public Flowable<Response<List<String>>> listAttachments(Integer reid) {
    Flowable<Response<List<String>>> flowable =
        api.listAttachments(reid).subscribeOn(Schedulers.io());
    return flowable;
  }

  public interface Api {
    @GET(path + pathKey)
    Flowable<Response<Record>> get(@Path("reid") Integer reid, @Query("mask") String mask);

    @POST(path)
    Flowable<Response<Record>> add(@Body Record bean);

    @PUT(path + pathKey)
    Flowable<Response<Record>> update(
        @Path("reid") Integer reid, @Body Record bean, @Query("mask") String mask);

    @DELETE(path + pathKey)
    Flowable<Response<Void>> delete(@Path("reid") Integer reid);

    @GET(path)
    Flowable<Response<List<Record>>> query(
        @Query("filter") String filter,
        @Query("orderBy") String orderBy,
        @Header("Range") String range,
        @Query("mask") String mask);

    @GET(path)
    Flowable<Response<Void>> count(
        @Query("filter") String filter, @Header("Range") String range, @Query("mask") String mask);

    @GET(attachPath + pathKey)
    Flowable<Response<ResponseBody>> downloadAttachment(
        @Path("reid") Integer reid, @Query("name") String name, @Header("Accept") String accept);

    @PUT(attachPath + pathKey)
    Flowable<Response<Void>> uploadAttachment(
        @Path("reid") Integer reid, @Query("name") String name, @Body RequestBody body);

    @DELETE(attachPath + pathKey)
    Flowable<Response<Void>> deleteAttachment(
        @Path("reid") Integer reid, @Query("name") String name);

    @GET(attachPath + pathKey)
    Flowable<Response<List<String>>> listAttachments(@Path("reid") Integer reid);
  }
}