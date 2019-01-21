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
import test.sales.gen.bean.Product;
import test.sales.gen.mask.ProductMask;

public class ProductStub {
  public static final String path = "api/Product/";
  public static final String attachPath = "api/Product_attachment/";
  public static final String pathKey = "{imid}";

  private static ProductStub service;

  public static ProductStub getInstance(Context context) {
    if (service == null) {
      service = new ProductStub(context);
    }
    return service;
  }

  private Api api;

  public ProductStub(Context context) {
    api = RetrofitFactory.getInstance(context).create(Api.class);
  }

  public Flowable<Response<Product>> get(Integer imid) {
    return get(imid, null);
  }

  public Flowable<Response<Product>> get(Integer imid, ProductMask mask) {
    Flowable<Response<Product>> flowable = null;
    try {
      flowable =
          api.get(
                  imid,
                  mask == null ? null : RetrofitFactory.getObjectMapper().writeValueAsString(mask))
              .subscribeOn(Schedulers.io());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return flowable;
  }

  public Flowable<Response<Product>> add(Product bean) {
    Flowable<Response<Product>> flowable = api.add(bean).subscribeOn(Schedulers.io());
    return flowable;
  }

  public Flowable<Response<Product>> update(Product bean) {
    return update(bean, null);
  }

  public Flowable<Response<Product>> update(Product bean, ProductMask mask) {
    Flowable<Response<Product>> flowable = null;
    try {
      flowable =
          api.update(
                  bean.getImid(),
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
                      .writeValueAsString(new ProductMask().setImid(true)))
              .subscribeOn(Schedulers.io());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return flowable;
  }

  public Flowable<Response<List<Product>>> query(FilterLogicExpr filter, RangeExpr range) {
    return query(filter, null, range, null);
  }

  public Flowable<Response<List<Product>>> query(
      FilterLogicExpr filter, OrderByListExpr orderBy, RangeExpr range) {
    return query(filter, orderBy, range, null);
  }

  public Flowable<Response<List<Product>>> query(
      FilterLogicExpr filter, RangeExpr range, ProductMask mask) {
    return query(filter, null, range, mask);
  }

  public Flowable<Response<List<Product>>> query(
      FilterLogicExpr filter, OrderByListExpr orderBy, RangeExpr range, ProductMask mask) {
    Flowable<Response<List<Product>>> flowable = null;
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

  public Flowable<Response<Void>> delete(Integer imid) {
    return api.delete(imid).subscribeOn(Schedulers.io());
  }

  public Flowable<Response<ResponseBody>> downloadAttachment(Integer imid, String name) {
    Flowable<Response<ResponseBody>> flowable =
        api.downloadAttachment(imid, name, "application/octet-stream").subscribeOn(Schedulers.io());
    return flowable;
  }

  public Flowable<Response<Void>> uploadAttachment(Integer imid, String name, byte[] bytes) {
    RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
    Flowable<Response<Void>> flowable =
        api.uploadAttachment(imid, name, body).subscribeOn(Schedulers.io());
    return flowable;
  }

  public Flowable<Response<Void>> deleteAttachment(Integer imid, String name) {
    Flowable<Response<Void>> flowable =
        api.deleteAttachment(imid, name).subscribeOn(Schedulers.io());
    return flowable;
  }

  public Flowable<Response<List<String>>> listAttachments(Integer imid) {
    Flowable<Response<List<String>>> flowable =
        api.listAttachments(imid).subscribeOn(Schedulers.io());
    return flowable;
  }

  public interface Api {
    @GET(path + pathKey)
    Flowable<Response<Product>> get(@Path("imid") Integer imid, @Query("mask") String mask);

    @POST(path)
    Flowable<Response<Product>> add(@Body Product bean);

    @PUT(path + pathKey)
    Flowable<Response<Product>> update(
        @Path("imid") Integer imid, @Body Product bean, @Query("mask") String mask);

    @DELETE(path + pathKey)
    Flowable<Response<Void>> delete(@Path("imid") Integer imid);

    @GET(path)
    Flowable<Response<List<Product>>> query(
        @Query("filter") String filter,
        @Query("orderBy") String orderBy,
        @Header("Range") String range,
        @Query("mask") String mask);

    @GET(path)
    Flowable<Response<Void>> count(
        @Query("filter") String filter, @Header("Range") String range, @Query("mask") String mask);

    @GET(attachPath + pathKey)
    Flowable<Response<ResponseBody>> downloadAttachment(
        @Path("imid") Integer imid, @Query("name") String name, @Header("Accept") String accept);

    @PUT(attachPath + pathKey)
    Flowable<Response<Void>> uploadAttachment(
        @Path("imid") Integer imid, @Query("name") String name, @Body RequestBody body);

    @DELETE(attachPath + pathKey)
    Flowable<Response<Void>> deleteAttachment(
        @Path("imid") Integer imid, @Query("name") String name);

    @GET(attachPath + pathKey)
    Flowable<Response<List<String>>> listAttachments(@Path("imid") Integer imid);
  }
}