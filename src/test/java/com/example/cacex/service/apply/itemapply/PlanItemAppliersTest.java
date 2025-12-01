package com.example.cacex.service.apply.itemapply;

import com.example.cacex.model.Action;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.PlanItem;
import com.example.cacex.service.apply.apiservice.AborApiService;
import com.example.cacex.service.apply.apiservice.AborConfigurationApiService;
import com.example.cacex.service.apply.apiservice.AccountApiService;
import com.example.cacex.service.apply.apiservice.ChartOfAccountsApiService;
import com.example.cacex.service.apply.apiservice.DerivedPortfolioApiService;
import com.example.cacex.service.apply.apiservice.PlanItemActionService;
import com.example.cacex.service.apply.apiservice.PortfolioGroupApiService;
import com.example.cacex.service.apply.apiservice.PostingModuleApiService;
import com.example.cacex.service.apply.apiservice.SideDefinitionApiService;
import com.example.cacex.service.apply.apiservice.TransactionTypeApiService;
import com.finbourne.lusid.model.AborConfigurationRequest;
import com.finbourne.lusid.model.AborRequest;
import com.finbourne.lusid.model.Account;
import com.finbourne.lusid.model.ChartOfAccountsRequest;
import com.finbourne.lusid.model.CreateDerivedTransactionPortfolioRequest;
import com.finbourne.lusid.model.CreatePortfolioGroupRequest;
import com.finbourne.lusid.model.PostingModuleRequest;
import com.example.cacex.model.SideFile;
import com.example.cacex.model.TransactionFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PlanItemAppliersTest {

    private record ApplierCase<T>(FileCategory category,
                                  PlanItemApplier applier,
                                  PlanItemActionService<T> service,
                                  T payload) {
    }

    static Stream<ApplierCase<?>> applierCases() {
        AborApiService aborService = mock(AborApiService.class);
        AborConfigurationApiService aborConfigService = mock(AborConfigurationApiService.class);
        AccountApiService accountService = mock(AccountApiService.class);
        ChartOfAccountsApiService chartService = mock(ChartOfAccountsApiService.class);
        DerivedPortfolioApiService derivedService = mock(DerivedPortfolioApiService.class);
        PortfolioGroupApiService groupService = mock(PortfolioGroupApiService.class);
        PostingModuleApiService postingService = mock(PostingModuleApiService.class);
        SideDefinitionApiService sideService = mock(SideDefinitionApiService.class);
        TransactionTypeApiService txnService = mock(TransactionTypeApiService.class);

        return Stream.of(
                new ApplierCase<>(FileCategory.ABOR, new AborPlanItemApplier(aborService), aborService, new AborRequest()),
                new ApplierCase<>(FileCategory.ABOR_CONFIGURATION, new AborConfigurationPlanItemApplier(aborConfigService),
                        aborConfigService, new AborConfigurationRequest()),
                new ApplierCase<>(FileCategory.ACCOUNT, new AccountPlanItemApplier(accountService),
                        accountService, new Account()),
                new ApplierCase<>(FileCategory.CHART_OF_ACCOUNTS, new ChartOfAccountsPlanItemApplier(chartService),
                        chartService, new ChartOfAccountsRequest()),
                new ApplierCase<>(FileCategory.DERIVED_PORTFOLIO, new DerivedPortfolioPlanItemApplier(derivedService),
                        derivedService, new CreateDerivedTransactionPortfolioRequest()),
                new ApplierCase<>(FileCategory.PORTFOLIO_GROUP, new PortfolioGroupPlanItemApplier(groupService),
                        groupService, new CreatePortfolioGroupRequest()),
                new ApplierCase<>(FileCategory.POSTING_RULE, new PostingModulePlanItemApplier(postingService),
                        postingService, new PostingModuleRequest()),
                new ApplierCase<>(FileCategory.SIDE, new SidePlanItemApplier(sideService),
                        sideService, new SideFile()),
                new ApplierCase<>(FileCategory.TRANSACTION, new TransactionPlanItemApplier(txnService),
                        txnService, new TransactionFile())
        );
    }

    @ParameterizedTest
    @MethodSource("applierCases")
    @SuppressWarnings("unchecked")
    void delegatesCreateToUnderlyingService(ApplierCase<?> testCase) {
        PlanItem item = new PlanItem(Action.NEW, testCase.category(), "Scope", "Key", "src", testCase.payload());

        testCase.applier().apply(item);

        PlanItemActionService<Object> service = (PlanItemActionService<Object>) testCase.service();
        verify(service).create("Scope", "Key", testCase.payload());
        assertEquals(testCase.category(), testCase.applier().supportedCategory());
    }
}
