package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update a member's profile details")
public record UpdateMemberRequest(

    @Schema(description = "Full name", example = "Rahul Sharma Updated")
    String fullName,

    @Schema(description = "Phone number", example = "9999999999")
    String phone,

    @Schema(description = "PAN card number", example = "ABCDE1234F")
    String pan,

    @Schema(description = "Whether the account is active", example = "true")
    Boolean active
) {}
