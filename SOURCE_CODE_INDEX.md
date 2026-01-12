# Fixed Assets Module - Source Code Index

## 📁 Module Structure

This folder contains all source code for the Fixed Assets Management module, extracted from the main accounts-service application.

---

## 📊 **Files Summary**

| Category | Count | Description |
|----------|-------|-------------|
| **Controllers** | 13 | REST API endpoints |
| **Services** | 13 | Business logic layer |
| **Repositories** | 11 | Data access layer |
| **Entities** | 11 | Database models |
| **DTOs** | 26 | Data transfer objects |
| **Documentation** | 3 | README, COA, Postman |
| **TOTAL** | **77** | Complete module |

---

## 🎯 **Controllers** (13 files)

### Core Asset Management
- `FixedAssetController.java` - Main asset CRUD operations
- `FixedAssetClassController.java` - Asset classification
- `FixedAssetEntryController.java` - Asset activation
- `FixedAssetReportController.java` - Reports and analytics

### Depreciation
- `DepreciationController.java` - Depreciation calculation

### Lifecycle Operations
- `CapitalImprovementController.java` - Capital improvements
- `ConservationController.java` - Asset conservation
- `IndividualFixedAssetController.java` - Individual asset tracking
- `MonthlyUsageController.java` - Usage tracking
- `ParameterChangeController.java` - Parameter updates

### Disposal
- `SaleController.java` - Asset sales
- `SalePreparationController.java` - Sale preparation
- `WriteOffController.java` - Asset write-offs

---

## 💼 **Services** (13 files)

### Core Services
- `FixedAssetService.java` - Asset management logic
- `FixedAssetClassService.java` - Classification logic
- `FixedAssetEntryService.java` - Activation logic
- `FixedAssetReportService.java` - Reporting logic

### Depreciation
- `DepreciationService.java` - Depreciation calculations

### Lifecycle Services
- `CapitalImprovementService.java` - Improvement processing
- `ConservationService.java` - Conservation processing
- `IndividualFixedAssetService.java` - Individual asset logic
- `MonthlyUsageService.java` - Usage tracking logic
- `ParameterChangeService.java` - Parameter change logic

### Disposal Services
- `SaleService.java` - Sale processing
- `SalePreparationService.java` - Sale preparation logic
- `WriteOffService.java` - Write-off processing

---

## 🗄️ **Repositories** (11 files)

- `FixedAssetRepository.java`
- `FixedAssetClassRepository.java`
- `FixedAssetEntryRepository.java`
- `FixedAssetDepreciationRepository.java`
- `FixedAssetCapitalImprovementRepository.java`
- `FixedAssetConservationRepository.java`
- `FixedAssetMonthlyUsageRepository.java`
- `FixedAssetParameterChangeRepository.java`
- `FixedAssetSaleRepository.java`
- `FixedAssetSalePreparationRepository.java`
- `FixedAssetWriteOffRepository.java`

---

## 📦 **Entities** (11 files)

### Core Entities
- `FixedAsset.java` - Main asset entity
- `FixedAssetClass.java` - Asset classification
- `FixedAssetEntry.java` - Activation records

### Depreciation
- `FixedAssetDepreciation.java` - Depreciation records

### Lifecycle Entities
- `FixedAssetCapitalImprovement.java` - Improvement records
- `FixedAssetConservation.java` - Conservation records
- `FixedAssetMonthlyUsage.java` - Usage records
- `FixedAssetParameterChange.java` - Parameter change records

### Disposal Entities
- `FixedAssetSale.java` - Sale records
- `FixedAssetSalePreparation.java` - Sale preparation records
- `FixedAssetWriteOff.java` - Write-off records

---

## 📝 **DTOs** (26 files)

### Request DTOs
- `FixedAssetRequest.java`
- `FixedAssetClassRequest.java`
- `ConservationRequest.java`
- `IndividualFixedAssetRequest.java`
- `MonthlyUsageRequest.java`
- `ParameterChangeRequest.java`
- `FixedAssetSaleRequest.java`
- `FixedAssetWriteOffRequest.java`
- `SalePreparationRequest.java`
- `BatchDepreciationRequest.java`

### Response DTOs
- `FixedAssetResponse.java`
- `FixedAssetClassResponse.java`
- `ConservationResponse.java`
- `IndividualFixedAssetResponse.java`
- `MonthlyUsageResponse.java`
- `ParameterChangeResponse.java`
- `FixedAssetSaleResponse.java`
- `FixedAssetWriteOffResponse.java`
- `SalePreparationResponse.java`
- `BatchDepreciationResponse.java`
- `DepreciationItemResponse.java`

### Report DTOs
- `FixedAssetSummaryDTO.java`
- `DepreciationScheduleDTO.java`
- `DepreciationStatementDTO.java`
- `AssetDocumentExpirationDTO.java`

### Helper DTOs
- `CapitalImprovementInventoryItemDTO.java`

---

## 📚 **Documentation Files**

1. **README.md** - Complete lifecycle guide
2. **CHART_OF_ACCOUNTS.md** - GL account structure
3. **Fixed-Assets-Postman-Collection.json** - API collection
4. **SOURCE_CODE_INDEX.md** - This file

---

## 🔗 **Dependencies**

### External Dependencies
The Fixed Assets module depends on:
- Chart of Accounts (for GL accounts)
- Journal Entries (for automatic posting)
- Companies (for buyers/sellers)
- Items (for inventory items in improvements)

### Shared Enums
Located in main project:
- `AssetStatus`
- `DepreciationMethod`
- `DisposalType`
- `ParameterChangeType`
- `DocumentType`

---

## 🚀 **Usage**

### Integration with Main Application

These files are **copies** of the source code for reference and documentation purposes. The actual running code remains in the main `src/main/java` directory.

To use this module:
1. Review the documentation (README.md, CHART_OF_ACCOUNTS.md)
2. Import the Postman collection
3. Follow the 8-stage lifecycle workflow
4. Use the API endpoints from the main application

---

## 📂 **Folder Structure**

```
fixed-assets-module/
├── README.md                              # Complete lifecycle guide
├── CHART_OF_ACCOUNTS.md                   # GL account structure
├── Fixed-Assets-Postman-Collection.json   # API collection
├── SOURCE_CODE_INDEX.md                   # This file
│
└── src/main/java/com/example/accounts/
    ├── controller/                        # 13 REST controllers
    ├── service/                           # 13 business services
    ├── repository/                        # 11 data repositories
    ├── entity/                            # 11 JPA entities
    └── dto/                               # 26 data transfer objects
```

---

## 🔍 **Quick Reference**

### API Base Paths
- `/api/fixed-assets` - Main assets
- `/api/fixed-asset-classes` - Classifications
- `/api/fixed-asset-entries` - Activations
- `/api/depreciation` - Depreciation
- `/api/capital-improvements` - Improvements
- `/api/conservations` - Conservation
- `/api/individual-fixed-assets` - Individual tracking
- `/api/monthly-usage` - Usage tracking
- `/api/parameter-changes` - Parameter updates
- `/api/sales` - Sales
- `/api/sale-preparations` - Sale prep
- `/api/write-offs` - Write-offs
- `/api/fixed-assets/reports` - Reports

### Key Workflows
1. **Acquisition**: Create Asset → Register Invoice
2. **Activation**: Create Entry → Post Entry
3. **Depreciation**: Calculate Monthly
4. **Improvement**: Create Capital Improvement
5. **Disposal**: Prepare → Sale/Write-off

---

## 📞 **Support**

For questions or issues:
- Review README.md for detailed documentation
- Check CHART_OF_ACCOUNTS.md for GL setup
- Use Postman collection for API testing
- Refer to Swagger UI: http://localhost:8080/swagger-ui.html

---

**Last Updated:** 2026-01-06  
**Version:** 1.0  
**Total Files:** 77 (74 source + 3 documentation)
