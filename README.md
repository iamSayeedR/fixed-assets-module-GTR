# 🏢 Fixed Assets Module - Complete Lifecycle Guide

## Overview
The Fixed Assets module provides comprehensive lifecycle management for all organizational assets, from planning through disposal, with full accounting integration.

---

## 📋 **Complete Asset Lifecycle Flow**

```
┌─────────────────────────────────────────────────────────────────────┐
│                    FIXED ASSET LIFECYCLE                             │
└─────────────────────────────────────────────────────────────────────┘

1. PLANNING
   └─→ Identify needs, budget, market research
       └─→ Decision: Purchase vs. Construct

2. ACQUISITION
   └─→ Create Master Record (Asset Management > Master Data > Fixed Assets)
       └─→ Register Purchase (Invoice Received) OR Track Construction (CIP)

3. ACCEPTANCE FOR ACCOUNTING
   └─→ Create Fixed Asset Entry (Asset Management > Fixed Asset Documents)
       └─→ Set: Initial Cost, Salvage Value, Depreciation Method, Useful Life
       └─→ POST → Status: NEW → ACTIVE

4. USAGE & DEPRECIATION
   └─→ Monthly Depreciation Calculation
       ├─→ Straight Line Method
       └─→ Units of Production (requires Monthly Usage entry)

5. MAINTENANCE & IMPROVEMENTS
   └─→ Capital Improvements Document
       └─→ Update: Gross Cost, Useful Life, Salvage Value

6. REASSESSMENT
   └─→ Changes of Fixed Asset Parameters
       └─→ Update for impairment or value gain

7. DISPOSAL / SALE
   ├─→ Preparation for Sale
   ├─→ Fixed Asset Sale
   └─→ Fixed Asset Write-Off

8. REPORTING
   └─→ Statement of Depreciation
   └─→ Inventory Card
   └─→ Asset Summary Dashboard
```

---

## 🎯 **Stage 1: Planning**

### Objectives
- Identify business needs
- Establish budget
- Conduct market research
- Decide: Purchase vs. Construct

### Actions
- Business case analysis
- Budget approval
- Vendor selection (if purchasing)
- Construction planning (if building)

---

## 🎯 **Stage 2: Acquisition**

### API Endpoints

#### Create Fixed Asset Master Record
```http
POST /api/fixed-assets
```

**Request Body:**
```json
{
  "assetNumber": "FA-2026-001",
  "description": "Office Building",
  "assetClassId": 1,
  "folder": "Real Estate",
  "location": "Main Campus",
  "department": "Administration",
  "responsiblePerson": "John Doe",
  "serialNumber": "SN-12345",
  "manufacturer": "ABC Construction",
  "purchaseDate": "2026-01-15",
  "warrantyExpiryDate": "2027-01-15"
}
```

#### Register Purchase Invoice
```http
POST /api/invoices
```

**Request Body:**
```json
{
  "invoiceNumber": "INV-ASSET-001",
  "invoiceDate": "2026-01-15",
  "dueDate": "2026-02-15",
  "documentType": "SUPPLIER_INVOICE",
  "companyId": 1,
  "notes": "Fixed asset purchase - Office Building",
  "lines": [
    {
      "lineNumber": 1,
      "itemId": 5,
      "description": "Office Building Purchase",
      "quantity": 1.00,
      "unitPrice": 500000.00,
      "vatRate": 0.00,
      "discountRate": 0.00
    }
  ]
}
```

### Construction in Progress (CIP)
For constructed assets, costs accumulate in CIP account until ready for use.

---

## 🎯 **Stage 3: Acceptance for Accounting**

### Create Fixed Asset Entry

#### API Endpoint
```http
POST /api/fixed-asset-entries
```

**Purpose:**
- Activates the asset (NEW → ACTIVE)
- Sets depreciation parameters
- Creates initial GL entry

**Request Body:**
```json
{
  "assetId": 1,
  "entryDate": "2026-02-01",
  "initialCost": 500000.00,
  "salvageValue": 50000.00,
  "depreciationMethod": "STRAIGHT_LINE",
  "usefulLifeMonths": 240,
  "depreciationStartDate": "2026-02-01",
  "assetGLAccountId": 101,
  "depreciationGLAccountId": 201,
  "accumulatedDepreciationGLAccountId": 202
}
```

**Depreciation Methods:**
- `STRAIGHT_LINE` - Equal amounts per period
- `DECLINING_BALANCE` - Percentage of book value
- `UNITS_OF_PRODUCTION` - Based on usage

**GL Accounts Required:**
- **Asset Account** (e.g., 1500 - Fixed Assets)
- **Depreciation Expense** (e.g., 6100 - Depreciation Expense)
- **Accumulated Depreciation** (e.g., 1590 - Accumulated Depreciation)

#### Post the Entry
```http
POST /api/fixed-asset-entries/{entryId}/post
```

**Result:**
- Asset status: ACTIVE
- Depreciation begins
- Initial journal entry created

---

## 🎯 **Stage 4: Usage & Depreciation**

### Monthly Depreciation Calculation

#### Calculate Depreciation
```http
POST /api/depreciation/calculate
```

**Request Body:**
```json
{
  "periodDate": "2026-02-28"
}
```

**Process:**
1. Identifies all ACTIVE assets
2. Calculates depreciation based on method
3. Creates journal entries
4. Updates accumulated depreciation

#### Get Depreciation Schedule
```http
GET /api/depreciation/schedule/{assetId}
```

### Units of Production Method

For assets using Units of Production depreciation:

#### Record Monthly Usage
```http
POST /api/monthly-usage
```

**Request Body:**
```json
{
  "assetId": 1,
  "usageMonth": "2026-02",
  "unitsProduced": 1000,
  "notes": "February production"
}
```

**Calculation:**
```
Depreciation = (Cost - Salvage) × (Units This Period / Total Expected Units)
```

---

## 🎯 **Stage 5: Maintenance & Capital Improvements**

### Capital Improvements

#### Create Capital Improvement
```http
POST /api/capital-improvements
```

**Request Body:**
```json
{
  "assetId": 1,
  "improvementDate": "2026-06-15",
  "description": "Building renovation - new HVAC system",
  "improvementCost": 50000.00,
  "extendedUsefulLifeMonths": 24,
  "newSalvageValue": 60000.00,
  "invoiceId": 25,
  "notes": "Major HVAC upgrade"
}
```

**Effects:**
- Increases gross cost
- May extend useful life
- May increase salvage value
- Recalculates depreciation going forward

**GL Entry Created:**
```
DR: Fixed Asset Account        50,000
    CR: Cash/Accounts Payable         50,000
```

---

## 🎯 **Stage 6: Reassessment**

### Parameter Changes

#### Update Asset Parameters
```http
POST /api/parameter-changes
```

**Request Body:**
```json
{
  "assetId": 1,
  "changeDate": "2026-12-31",
  "changeType": "IMPAIRMENT",
  "newGrossCost": 480000.00,
  "newSalvageValue": 45000.00,
  "newUsefulLifeMonths": 200,
  "reason": "Impairment due to market conditions",
  "notes": "Annual impairment review"
}
```

**Change Types:**
- `IMPAIRMENT` - Value decrease
- `REVALUATION` - Value increase
- `USEFUL_LIFE_CHANGE` - Useful life adjustment
- `SALVAGE_VALUE_CHANGE` - Salvage value adjustment

**Effects:**
- Updates asset parameters
- Recalculates future depreciation
- May create impairment loss entry

---

## 🎯 **Stage 7: Disposal / Sale**

### Option A: Sale Preparation

#### Prepare Asset for Sale
```http
POST /api/sale-preparations
```

**Request Body:**
```json
{
  "assetId": 1,
  "preparationDate": "2027-01-15",
  "estimatedSalePrice": 400000.00,
  "estimatedSaleDate": "2027-02-01",
  "reason": "Upgrading to new facility",
  "notes": "Asset in good condition"
}
```

**Status Change:** ACTIVE → PREPARED_FOR_SALE

#### Record Sale
```http
POST /api/sales
```

**Request Body:**
```json
{
  "assetId": 1,
  "saleDate": "2027-02-01",
  "salePrice": 420000.00,
  "buyerCompanyId": 10,
  "paymentMethod": "BANK_TRANSFER",
  "invoiceId": 150,
  "notes": "Sale completed successfully"
}
```

**GL Entries Created:**
```
DR: Cash/Receivable                420,000
DR: Accumulated Depreciation       XXX,XXX
    CR: Fixed Asset                        500,000
    CR: Gain on Sale (or DR: Loss)         XX,XXX
```

**Status Change:** PREPARED_FOR_SALE → SOLD

### Option B: Write-Off

#### Write Off Asset
```http
POST /api/write-offs
```

**Request Body:**
```json
{
  "assetId": 1,
  "writeOffDate": "2027-01-15",
  "reason": "Damaged beyond repair",
  "disposalType": "SCRAPPED",
  "salvageProceeds": 5000.00,
  "notes": "Fire damage - total loss"
}
```

**Disposal Types:**
- `SCRAPPED` - Scrapped/destroyed
- `DONATED` - Donated to charity
- `LOST` - Lost/stolen
- `OBSOLETE` - Technologically obsolete

**GL Entries Created:**
```
DR: Accumulated Depreciation       XXX,XXX
DR: Loss on Write-Off              XX,XXX
DR: Cash (if salvage proceeds)     5,000
    CR: Fixed Asset                        500,000
```

**Status Change:** ACTIVE → WRITTEN_OFF

### Conservation (Temporary Removal)

#### Conserve Asset
```http
POST /api/conservations
```

**Request Body:**
```json
{
  "assetId": 1,
  "conservationDate": "2026-06-01",
  "reason": "Seasonal shutdown",
  "expectedReturnDate": "2026-09-01",
  "notes": "Summer maintenance period"
}
```

**Effects:**
- Status: ACTIVE → CONSERVED
- Depreciation paused
- Can be reactivated later

---

## 🎯 **Stage 8: Reporting**

### Summary Dashboard
```http
GET /api/fixed-assets/reports/summary
```

**Response:**
```json
{
  "totalAssets": 150,
  "totalGrossCost": 5000000.00,
  "totalAccumulatedDepreciation": 1200000.00,
  "totalNetBookValue": 3800000.00,
  "assetsByStatus": {
    "NEW": 5,
    "ACTIVE": 120,
    "CONSERVED": 10,
    "SOLD": 10,
    "WRITTEN_OFF": 5
  },
  "monthlyDepreciation": 25000.00
}
```

### Depreciation Schedule
```http
GET /api/fixed-assets/reports/depreciation-schedule
```

### Depreciation Statement
```http
GET /api/fixed-assets/reports/depreciation-statement
```

**Groups by:**
- Department
- Asset Class
- Location

### Document Expirations
```http
GET /api/fixed-assets/reports/document-expirations?onDate=2026-12-31
```

**Tracks:**
- Warranty expiration
- Insurance expiration
- Maintenance due dates
- Certificate renewals

---

## 📊 **Chart of Accounts for Fixed Assets**

### Required GL Accounts

#### Asset Accounts (Balance Sheet)
```
1500 - Fixed Assets - Land
1510 - Fixed Assets - Buildings
1520 - Fixed Assets - Machinery & Equipment
1530 - Fixed Assets - Vehicles
1540 - Fixed Assets - Furniture & Fixtures
1550 - Fixed Assets - Computer Equipment
1560 - Construction in Progress (CIP)
```

#### Accumulated Depreciation (Contra-Asset)
```
1590 - Accumulated Depreciation - Buildings
1591 - Accumulated Depreciation - Machinery
1592 - Accumulated Depreciation - Vehicles
1593 - Accumulated Depreciation - Furniture
1594 - Accumulated Depreciation - Computers
```

#### Expense Accounts (Income Statement)
```
6100 - Depreciation Expense - Buildings
6101 - Depreciation Expense - Machinery
6102 - Depreciation Expense - Vehicles
6103 - Depreciation Expense - Furniture
6104 - Depreciation Expense - Computers
6105 - Impairment Loss
6106 - Loss on Disposal of Fixed Assets
```

#### Revenue/Gain Accounts
```
4900 - Gain on Sale of Fixed Assets
```

---

## 🔄 **Complete API Workflow Example**

### Scenario: Purchase and Depreciate Office Equipment

```bash
# Step 1: Create Asset Master
POST /api/fixed-assets
{
  "assetNumber": "FA-2026-100",
  "description": "Dell Server Rack",
  "assetClassId": 5,
  "folder": "IT Equipment",
  "department": "IT"
}

# Step 2: Record Purchase Invoice
POST /api/invoices
{
  "invoiceNumber": "SINV-2026-050",
  "documentType": "SUPPLIER_INVOICE",
  "companyId": 15,
  "lines": [{
    "itemId": 20,
    "description": "Dell Server Rack",
    "quantity": 1,
    "unitPrice": 50000.00
  }]
}

# Step 3: Create & Post Asset Entry
POST /api/fixed-asset-entries
{
  "assetId": 100,
  "initialCost": 50000.00,
  "salvageValue": 5000.00,
  "depreciationMethod": "STRAIGHT_LINE",
  "usefulLifeMonths": 60,
  "assetGLAccountId": 1550,
  "depreciationGLAccountId": 6104,
  "accumulatedDepreciationGLAccountId": 1594
}

POST /api/fixed-asset-entries/1/post

# Step 4: Calculate Monthly Depreciation
POST /api/depreciation/calculate
{
  "periodDate": "2026-02-28"
}

# Monthly Depreciation = (50,000 - 5,000) / 60 = 750/month

# Step 5: After 3 years, add improvement
POST /api/capital-improvements
{
  "assetId": 100,
  "improvementCost": 10000.00,
  "extendedUsefulLifeMonths": 12
}

# Step 6: After 5 years, sell the asset
POST /api/sale-preparations
{
  "assetId": 100,
  "estimatedSalePrice": 15000.00
}

POST /api/sales
{
  "assetId": 100,
  "salePrice": 16000.00,
  "buyerCompanyId": 25
}
```

---

## 📈 **Key Performance Indicators (KPIs)**

### Asset Management KPIs
- **Total Asset Value** - Sum of all active assets
- **Depreciation Rate** - Monthly depreciation / Total asset value
- **Asset Utilization** - Active assets / Total assets
- **Maintenance Cost Ratio** - Maintenance costs / Asset value
- **Asset Turnover** - Revenue / Average asset value

### Depreciation Metrics
- **Average Asset Age** - Average time since acquisition
- **Remaining Useful Life** - Average remaining depreciation period
- **Depreciation Accuracy** - Actual vs. planned depreciation

---

## ✅ **Best Practices**

### 1. Asset Numbering
- Use consistent numbering scheme (e.g., FA-YYYY-NNN)
- Include asset class prefix
- Sequential numbering within class

### 2. Classification
- Establish clear asset classes
- Define depreciation defaults per class
- Map GL accounts per class

### 3. Depreciation
- Review useful life assumptions annually
- Conduct impairment tests regularly
- Document all parameter changes

### 4. Documentation
- Maintain purchase documentation
- Track warranties and insurance
- Record all improvements and repairs

### 5. Physical Verification
- Conduct annual physical counts
- Reconcile with accounting records
- Update location and condition

---

## 🔐 **Security & Compliance**

### Audit Trail
All fixed asset transactions are logged:
- User who created/modified
- Timestamp of changes
- Before/after values
- Reason for changes

### Segregation of Duties
- Asset creation: Asset Manager
- Entry posting: Accounting
- Depreciation calculation: System/Accounting
- Disposal approval: Management

### Compliance
- GAAP/IFRS depreciation methods
- Tax depreciation tracking
- Asset capitalization thresholds
- Disposal authorization requirements

---

## 📞 **Support & Resources**

### API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/api-docs`

### Postman Collection
- Import: `Accounts-Service-Postman-Collection.json`
- Environment: Set `baseUrl` to `http://localhost:8080`

---

**Last Updated:** 2026-01-06  
**Version:** 1.0  
**Module:** Fixed Assets Management
# fixed-assets-module-GTR
