//package com.mobile2.uts_elsid;
//
//import android.app.Application;
//
//import com.midtrans.sdk.corekit.core.MidtransSDK;
//import com.midtrans.sdk.corekit.internal.config.Config;
//
//public class MyApplication extends Application {
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        // Config Midtrans
//        Config config = new ConfigBuilder()
//                .setClientKey("YOUR_CLIENT_KEY") // Ganti dengan client key dari Midtrans
//                .setContext(this)
//                .setMerchantBaseUrl("https://midtrans-server.herokuapp.com") // URL server Anda
//                .enableLog(true)
//                .build();
//
//        MidtransSDK.getInstance().setConfig(config);
//    }
//}