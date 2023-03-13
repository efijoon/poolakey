package ir.cafebazaar.poolakey.billing.consume

import android.content.Context
import android.os.RemoteException
import com.android.vending.billing.IInAppBillingService
import ir.cafebazaar.poolakey.billing.BillingFunction
import ir.cafebazaar.poolakey.callback.ConsumeCallback
import ir.cafebazaar.poolakey.constant.BazaarIntent
import ir.cafebazaar.poolakey.constant.Billing
import ir.cafebazaar.poolakey.exception.ConsumeFailedException
import ir.cafebazaar.poolakey.takeIf
import ir.cafebazaar.poolakey.thread.PoolakeyThread

internal class ConsumeFunction(
    private val mainThread: PoolakeyThread<() -> Unit>,
    private val context: Context
) : BillingFunction<ConsumeFunctionRequest> {

    override fun function(
        billingService: IInAppBillingService,
        request: ConsumeFunctionRequest
    ): Unit = with(request) {
        try {
            billingService.consumePurchase(Billing.IN_APP_BILLING_VERSION, context.packageName, purchaseToken)
                .takeIf(
                    thisIsTrue = { it == BazaarIntent.RESPONSE_RESULT_OK },
                    andIfNot = {
                        mainThread.execute {
                            ConsumeCallback().apply(callback)
                                .consumeFailed
                                .invoke(ConsumeFailedException())
                        }
                    }
                )
                ?.also {
                    mainThread.execute {
                        ConsumeCallback().apply(callback).consumeSucceed.invoke()
                    }
                }
        } catch (e: RemoteException) {
            mainThread.execute {
                ConsumeCallback().apply(callback).consumeFailed.invoke(e)
            }
        }
    }

}