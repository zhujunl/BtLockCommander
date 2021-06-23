package com.miaxis.btlockcommanderdemo.model.net;

import com.miaxis.btlockcommanderdemo.model.entity.DownDataListDto;
import com.miaxis.btlockcommanderdemo.model.entity.NbCmdDto;
import com.miaxis.btlockcommanderdemo.model.entity.NbUpdateFirmwareDto;
import com.miaxis.btlockcommanderdemo.model.entity.PersonDto;
import com.miaxis.btlockcommanderdemo.model.entity.ReplyData;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface NetLockNet {

    @FormUrlEncoded
    @POST("netlock/api/nb/v2/Bluetooth/getPerson")
    Observable<ResponseEntity<List<PersonDto>>> getPerson(@Header("hostCertificate") String hostCertificate,
                                                          @Field("personIds") String personIds);

    @FormUrlEncoded
    @POST("netlock/api/nb/v2/Bluetooth/getBindData")
    Observable<ResponseEntity<List<NbCmdDto>>> downNbCmdDto(@Header("hostCertificate") String hostCertificate,
                                                            @Field("serialNumber") String serialNumber);

    @FormUrlEncoded
    @POST("netlock/api/nb/v2/Bluetooth/handleBindReply")
    Observable<ResponseEntity> passThrough(@Header("hostCertificate") String hostCertificate,
                                           @Field("serialNumber") String serialNumber,
                                           @Field("packageData") String packageData);

    @FormUrlEncoded
    @POST("netlock/api/nb/v2/Bluetooth/getUpdate")
    Observable<ResponseEntity<NbUpdateFirmwareDto>> getUpdate(@Header("hostCertificate") String hostCertificate,
                                                              @Field("serialNumber") String serialNumber,
                                                              @Field("currentVersion") String currentVersion);

    @FormUrlEncoded
    @POST("netlock/bluetooth/login")
    Observable<ResponseEntity<String>> login(@Field("username") String username,
                                             @Field("password") String password);

    @FormUrlEncoded
    @POST("netlock/api/nb/v2/Bluetooth/checkAuth")
    Observable<ResponseEntity> checkAuth(@Header("hostCertificate") String hostCertificate,
                                         @Field("serialNumber") String serialNumber);

    @FormUrlEncoded
    @POST("netlock/api/app/bluetooth/down-data/get")
    Observable<ResponseEntity<DownDataListDto>> getDownData(@Header("hostCertificate") String hostCertificate,
                                                            @Field("serialNumber") String serialNumber);

    @FormUrlEncoded
    @POST("netlock/api/app/bluetooth/up-data/commit")
    Observable<ResponseEntity<ReplyData>> sendUpData(@Header("hostCertificate") String hostCertificate,
                                                     @Field("serialNumber") String serialNumber,
                                                     @Field("upData") String upData,
                                                     @Field("upTime") long upTime);

}
