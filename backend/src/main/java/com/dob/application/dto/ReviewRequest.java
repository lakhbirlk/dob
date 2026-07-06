package com.dob.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for company review actions (approve/reject with optional comment)")
public record ReviewRequest(

    @Schema(description = "Comment/reason for rejection (required for reject, optional for approve)")
    String comment

) {}
