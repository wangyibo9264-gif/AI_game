package com.rumortown.report;

import com.rumortown.common.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions/{sessionId}/reports")
public class TruthReportController {
    private final TruthReportService truthReportService;

    public TruthReportController(TruthReportService truthReportService) {
        this.truthReportService = truthReportService;
    }

    @PostMapping
    public ApiResponse<TruthReportResultDto> submitReport(
            @PathVariable Long sessionId,
            @RequestBody SubmitTruthReportRequest request
    ) {
        return ApiResponse.data(truthReportService.submitReport(sessionId, request));
    }
}