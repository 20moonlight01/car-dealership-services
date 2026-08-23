package ru.glebova.presentation.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TryUpdateTestDriveRequestStateRequest.TryCancelTestDriveRequest.class, name = "CANCEL"),
        @JsonSubTypes.Type(value = TryUpdateTestDriveRequestStateRequest.TryFinishTestDriveRequest.class, name = "FINISH")})
public abstract class TryUpdateTestDriveRequestStateRequest {
    public static class TryCancelTestDriveRequest extends TryUpdateTestDriveRequestStateRequest { }

    public static class TryFinishTestDriveRequest extends TryUpdateTestDriveRequestStateRequest { }
}
