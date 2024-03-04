package com.skr.vendor.constants;

public final class Constants {

    private Constants() {
        throw new IllegalStateException("Constant Class can not be instantiate");
    }

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";


    public static final String REST_BINDING = "/es/api/v1";
    public static final String UNAUTHENTICATED = "/unauthenticated";
    public static final String AUTHENTICATED = "/authenticated";
    public static final String VENDORS = "/vendors";
    public static final String VENDORS_VID = "/vendors/{vid}";
}
