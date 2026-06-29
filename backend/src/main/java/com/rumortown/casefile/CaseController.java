package com.rumortown.casefile;

import com.rumortown.common.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cases")
public class CaseController {
    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public ApiResponse<List<CaseSummaryDto>> listCases() {
        return ApiResponse.data(caseService.listCases());
    }

    @GetMapping("/{caseId}")
    public ApiResponse<CaseDetailDto> getCaseDetail(@PathVariable Long caseId) {
        return ApiResponse.data(caseService.getCaseDetail(caseId));
    }
}
