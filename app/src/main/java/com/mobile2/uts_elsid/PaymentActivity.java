//package com.mobile2.uts_elsid;
//
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.midtrans.sdk.corekit.models.snap.TransactionResult;
//
//import es.dmoral.toasty.Toasty;
//
//public class PaymentActivity extends AppCompatActivity implements PaymentUIFlowBuilder.PaymentResultListener {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_payment);
//
//        // Mulai alur pembayaran
//        new PaymentUIFlowBuilder()
//                .setContext(this)
//                .setTransactionFinishedCallback(this)
//                .build();
//    }
//
//    @Override
//    public void onTransactionFinished(TransactionResult result) {
//        // Handle hasil transaksi
//        if (result.getStatus().equals(TransactionResult.STATUS_SUCCESS)) {
//            Toasty.success(this, "Pembayaran Berhasil", Toasty.LENGTH_SHORT).show();
//        } else {
//            Toasty.error(this, "Pembayaran Gagal: " + result.getStatus(), Toasty.LENGTH_SHORT).show();
//        }
//        finish();
//    }
//}