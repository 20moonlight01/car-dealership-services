package ru.glebova.presentation.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TryUpdateOrderStateRequest.TryApproveOrderRequest.class, name = "APPROVE"),
        @JsonSubTypes.Type(value = TryUpdateOrderStateRequest.TryMarkOrderAwaitingPayRequest.class, name = "MARK_AWAITING_PAY"),
        @JsonSubTypes.Type(value = TryUpdateOrderStateRequest.TryCancelOrderRequest.class, name = "CANCEL"),
        @JsonSubTypes.Type(value = TryUpdateOrderStateRequest.TryMarkOrderDeliveringRequest.class, name = "MARK_DELIVERING"),
        @JsonSubTypes.Type(value = TryUpdateOrderStateRequest.TryFinishOrderRequest.class, name = "FINISH"),
        @JsonSubTypes.Type(value = TryUpdateOrderStateRequest.TryPayForOrderRequest.class, name = "PAY"),
        @JsonSubTypes.Type(value = TryUpdateOrderStateRequest.TryMarkOrderReadyRequest.class, name = "MARK_READY")})
public abstract class TryUpdateOrderStateRequest {
    public static class TryApproveOrderRequest extends TryUpdateOrderStateRequest { }

    public static class TryMarkOrderAwaitingPayRequest extends TryUpdateOrderStateRequest { }

    public static class TryCancelOrderRequest extends TryUpdateOrderStateRequest { }

    public static class TryMarkOrderDeliveringRequest extends TryUpdateOrderStateRequest { }

    public static class TryFinishOrderRequest extends TryUpdateOrderStateRequest { }

    public static class TryPayForOrderRequest extends TryUpdateOrderStateRequest {
        public TryPayForOrderRequest(float payment) {
            this.payment = payment;
        }

        public float getPayment() {
            return payment;
        }

        @NotNull
        @Positive
        private final float payment;
    }

    public static class TryMarkOrderReadyRequest extends TryUpdateOrderStateRequest { }
}
