package test.sales.gen.stub;

import android.content.Context;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
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
import test.sales.gen.bean.Employee;
import test.sales.gen.mask.EmployeeMask;

public class EmployeeStub {
    public static final String path = "api/Employee/";
    public static final String attachPath = "api/Employee_attachment/";
    public static final String pathKey = "{emid}";

    private static EmployeeStub service;

    @NonNull//TODO
    public static EmployeeStub getInstance(@NonNull Context context) {//TODO
        if (service == null) {
            service = new EmployeeStub(context);
        }
        return service;
    }

    private Api api;

    private EmployeeStub(Context context) {//TODO
        api = RetrofitFactory.getInstance(context).create(Api.class);
    }

    @NonNull//TODO
    public Flowable<Response<Employee>> get(Integer emid) {
        return get(emid, null);
    }

    @NonNull//TODO
    public Flowable<Response<Employee>> get(Integer emid, EmployeeMask mask) {
        Flowable<Response<Employee>> flowable = null;
        try {
            flowable =
                    api.get(
                            emid,
                            mask == null ? null : RetrofitFactory.getObjectMapper().writeValueAsString(mask))
                            .subscribeOn(Schedulers.io());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return flowable;
    }

    @NonNull//TODO
    public Flowable<Response<Employee>> add(Employee bean) {
        Flowable<Response<Employee>> flowable =
                api.add(bean.getEmid(), bean).subscribeOn(Schedulers.io());
        return flowable;
    }

    @NonNull//TODO
    public Flowable<Response<Employee>> update(Employee bean) {
        return update(bean, null);
    }

    @NonNull//TODO
    public Flowable<Response<Employee>> update(Employee bean, EmployeeMask mask) {
        Flowable<Response<Employee>> flowable = null;
        try {
            flowable =
                    api.update(
                            bean.getEmid(),
                            bean,
                            mask == null ? null : RetrofitFactory.getObjectMapper().writeValueAsString(mask))
                            .subscribeOn(Schedulers.io());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return flowable;
    }

    @NonNull//TODO
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
                                    .writeValueAsString(new EmployeeMask().setEmid(true)))
                            .subscribeOn(Schedulers.io());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return flowable;
    }

    @NonNull//TODO
    public Flowable<Response<List<Employee>>> query(FilterLogicExpr filter, RangeExpr range) {
        return query(filter, null, range, null);
    }

    @NonNull//TODO
    public Flowable<Response<List<Employee>>> query(
            FilterLogicExpr filter, OrderByListExpr orderBy, RangeExpr range) {
        return query(filter, orderBy, range, null);
    }

    @NonNull//TODO
    public Flowable<Response<List<Employee>>> query(
            FilterLogicExpr filter, RangeExpr range, EmployeeMask mask) {
        return query(filter, null, range, mask);
    }

    @NonNull//TODO
    public Flowable<Response<List<Employee>>> query(
            FilterLogicExpr filter, OrderByListExpr orderBy, RangeExpr range, EmployeeMask mask) {
        Flowable<Response<List<Employee>>> flowable = null;
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

    @NonNull//TODO
    public Flowable<Response<Void>> delete(Integer emid) {
        return api.delete(emid).subscribeOn(Schedulers.io());
    }

    @NonNull//TODO
    public Flowable<Response<ResponseBody>> downloadAttachment(Integer emid, String name) {
        Flowable<Response<ResponseBody>> flowable =
                api.downloadAttachment(emid, name, "application/octet-stream").subscribeOn(Schedulers.io());
        return flowable;
    }

    @NonNull//TODO
    public Flowable<Response<Void>> uploadAttachment(Integer emid, String name, byte[] bytes) {
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
        Flowable<Response<Void>> flowable =
                api.uploadAttachment(emid, name, body).subscribeOn(Schedulers.io());
        return flowable;
    }

    @NonNull//TODO
    public Flowable<Response<Void>> deleteAttachment(Integer emid, String name) {
        Flowable<Response<Void>> flowable =
                api.deleteAttachment(emid, name).subscribeOn(Schedulers.io());
        return flowable;
    }

    @NonNull//TODO
    public Flowable<Response<List<String>>> listAttachments(Integer emid) {
        Flowable<Response<List<String>>> flowable =
                api.listAttachments(emid).subscribeOn(Schedulers.io());
        return flowable;
    }

    public interface Api {
        @GET(path + pathKey)
        Flowable<Response<Employee>> get(@Path("emid") Integer emid, @Query("mask") String mask);

        @POST(path + pathKey)
        Flowable<Response<Employee>> add(@Path("emid") Integer emid, @Body Employee bean);

        @PUT(path + pathKey)
        Flowable<Response<Employee>> update(
                @Path("emid") Integer emid, @Body Employee bean, @Query("mask") String mask);

        @DELETE(path + pathKey)
        Flowable<Response<Void>> delete(@Path("emid") Integer emid);

        @GET(path)
        Flowable<Response<List<Employee>>> query(
                @Query("filter") String filter,
                @Query("orderBy") String orderBy,
                @Header("Range") String range,
                @Query("mask") String mask);

        @GET(path)
        Flowable<Response<Void>> count(
                @Query("filter") String filter, @Header("Range") String range, @Query("mask") String mask);

        @GET(attachPath + pathKey)
        Flowable<Response<ResponseBody>> downloadAttachment(
                @Path("emid") Integer emid, @Query("name") String name, @Header("Accept") String accept);

        @PUT(attachPath + pathKey)
        Flowable<Response<Void>> uploadAttachment(
                @Path("emid") Integer emid, @Query("name") String name, @Body RequestBody body);

        @DELETE(attachPath + pathKey)
        Flowable<Response<Void>> deleteAttachment(
                @Path("emid") Integer emid, @Query("name") String name);

        @GET(attachPath + pathKey)
        Flowable<Response<List<String>>> listAttachments(@Path("emid") Integer emid);
    }
}