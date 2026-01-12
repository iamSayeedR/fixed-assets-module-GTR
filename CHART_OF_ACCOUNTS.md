# Chart of Accounts - Fixed Assets Module

## Overview
This document provides the complete Chart of Accounts structure required for the Fixed Assets module, including all GL accounts needed for asset lifecycle management.

---

## 📊 Account Structure

### **1. ASSET ACCOUNTS (Balance Sheet - Non-Current Assets)**

| Account Code | Account Name | Type | Section | Purpose |
|--------------|--------------|------|---------|---------|
| **1500** | Fixed Assets - Land | DEBIT | NON_CURRENT_ASSETS | Land and land improvements |
| **1510** | Fixed Assets - Buildings | DEBIT | NON_CURRENT_ASSETS | Buildings and structures |
| **1520** | Fixed Assets - Machinery & Equipment | DEBIT | NON_CURRENT_ASSETS | Manufacturing and production equipment |
| **1530** | Fixed Assets - Vehicles | DEBIT | NON_CURRENT_ASSETS | Company vehicles and transportation |
| **1540** | Fixed Assets - Furniture & Fixtures | DEBIT | NON_CURRENT_ASSETS | Office furniture and fixtures |
| **1550** | Fixed Assets - Computer Equipment | DEBIT | NON_CURRENT_ASSETS | Computers, servers, IT equipment |
| **1560** | Construction in Progress (CIP) | DEBIT | NON_CURRENT_ASSETS | Assets under construction |

---

### **2. ACCUMULATED DEPRECIATION (Contra-Asset Accounts)**

| Account Code | Account Name | Type | Section | Purpose |
|--------------|--------------|------|---------|---------|
| **1590** | Accumulated Depreciation - Buildings | CREDIT | NON_CURRENT_ASSETS | Contra-asset for buildings |
| **1591** | Accumulated Depreciation - Machinery | CREDIT | NON_CURRENT_ASSETS | Contra-asset for machinery |
| **1592** | Accumulated Depreciation - Vehicles | CREDIT | NON_CURRENT_ASSETS | Contra-asset for vehicles |
| **1593** | Accumulated Depreciation - Furniture | CREDIT | NON_CURRENT_ASSETS | Contra-asset for furniture |
| **1594** | Accumulated Depreciation - Computers | CREDIT | NON_CURRENT_ASSETS | Contra-asset for computers |

---

### **3. DEPRECIATION EXPENSE (Income Statement - Operating Expenses)**

| Account Code | Account Name | Type | Section | Purpose |
|--------------|--------------|------|---------|---------|
| **6100** | Depreciation Expense - Buildings | DEBIT | OPERATING_EXPENSES | Monthly depreciation on buildings |
| **6101** | Depreciation Expense - Machinery | DEBIT | OPERATING_EXPENSES | Monthly depreciation on machinery |
| **6102** | Depreciation Expense - Vehicles | DEBIT | OPERATING_EXPENSES | Monthly depreciation on vehicles |
| **6103** | Depreciation Expense - Furniture | DEBIT | OPERATING_EXPENSES | Monthly depreciation on furniture |
| **6104** | Depreciation Expense - Computers | DEBIT | OPERATING_EXPENSES | Monthly depreciation on computers |
| **6105** | Impairment Loss | DEBIT | OTHER_EXPENSES | Asset impairment losses |
| **6106** | Loss on Disposal of Fixed Assets | DEBIT | OTHER_EXPENSES | Losses from asset sales/disposals |

---

### **4. GAIN/REVENUE ACCOUNTS (Income Statement)**

| Account Code | Account Name | Type | Section | Purpose |
|--------------|--------------|------|---------|---------|
| **4900** | Gain on Sale of Fixed Assets | CREDIT | OTHER_INCOME | Gains from asset sales |

---

## 📋 Account Setup Scripts

### Postman Collection Order

Execute these requests in order from the Postman collection:

1. **Master Data Setup** → **Chart of Accounts - Fixed Assets**
   - Create Asset Account - Land (1500)
   - Create Asset Account - Buildings (1510)
   - Create Asset Account - Machinery (1520)
   - Create Asset Account - Vehicles (1530)
   - Create Asset Account - Computers (1550)
   - Create CIP Account (1560)
   - Create Accumulated Depreciation accounts (1590-1594)
   - Create Depreciation Expense accounts (6100-6106)
   - Create Gain/Loss accounts (4900, 6106)

---

## 💼 Journal Entry Examples

### **1. Asset Acquisition**

**Purchase of Building for $500,000:**
```
DR: 1510 - Fixed Assets - Buildings          500,000
    CR: 2000 - Accounts Payable                     500,000
```

### **2. Asset Activation (Entry Posting)**

**No entry** - Just changes asset status from NEW to ACTIVE

### **3. Monthly Depreciation**

**Building depreciation $1,875/month:**
```
DR: 6100 - Depreciation Expense - Buildings   1,875
    CR: 1590 - Accumulated Depreciation - Buildings  1,875
```

**Calculation:**
- Cost: $500,000
- Salvage: $50,000
- Useful Life: 240 months (20 years)
- Monthly Depreciation: ($500,000 - $50,000) / 240 = $1,875

### **4. Capital Improvement**

**HVAC upgrade for $50,000:**
```
DR: 1510 - Fixed Assets - Buildings           50,000
    CR: 2000 - Accounts Payable                      50,000
```

### **5. Asset Sale**

**Sale of building for $420,000 (Book Value: $400,000):**
```
DR: 1000 - Cash                               420,000
DR: 1590 - Accumulated Depreciation           100,000
    CR: 1510 - Fixed Assets - Buildings              500,000
    CR: 4900 - Gain on Sale                           20,000
```

### **6. Asset Write-Off**

**Write off damaged equipment (Cost: $50,000, Accumulated Dep: $30,000):**
```
DR: 1591 - Accumulated Depreciation            30,000
DR: 6106 - Loss on Disposal                    20,000
    CR: 1520 - Fixed Assets - Machinery               50,000
```

### **7. Impairment Loss**

**Impairment of $20,000:**
```
DR: 6105 - Impairment Loss                     20,000
    CR: 1590 - Accumulated Depreciation               20,000
```

---

## 🔍 Account Relationships

### Asset Class to GL Account Mapping

| Asset Class | Asset Account | Depreciation Expense | Accumulated Depreciation |
|-------------|---------------|---------------------|--------------------------|
| Buildings | 1510 | 6100 | 1590 |
| Machinery | 1520 | 6101 | 1591 |
| Vehicles | 1530 | 6102 | 1592 |
| Furniture | 1540 | 6103 | 1593 |
| Computers | 1550 | 6104 | 1594 |

---

## 📈 Financial Statement Presentation

### **Balance Sheet**

```
NON-CURRENT ASSETS
  Fixed Assets:
    Land                                    $XXX,XXX
    Buildings                    $XXX,XXX
      Less: Accumulated Depreciation  (XXX,XXX)   XXX,XXX
    Machinery & Equipment        $XXX,XXX
      Less: Accumulated Depreciation  (XXX,XXX)   XXX,XXX
    Vehicles                     $XXX,XXX
      Less: Accumulated Depreciation  (XXX,XXX)   XXX,XXX
    Computer Equipment           $XXX,XXX
      Less: Accumulated Depreciation  (XXX,XXX)   XXX,XXX
    Construction in Progress                XXX,XXX
                                          ---------
  Total Fixed Assets (Net)                $XXX,XXX
```

### **Income Statement**

```
OPERATING EXPENSES
  Depreciation Expense:
    Buildings                               $XX,XXX
    Machinery                                XX,XXX
    Vehicles                                 XX,XXX
    Furniture                                XX,XXX
    Computers                                XX,XXX
                                          ---------
  Total Depreciation Expense               $XXX,XXX

OTHER INCOME/(EXPENSES)
  Gain on Sale of Fixed Assets              $XX,XXX
  (Loss on Disposal of Fixed Assets)        (XX,XXX)
  (Impairment Loss)                         (XX,XXX)
```

---

## ✅ Account Setup Checklist

- [ ] Create all Asset accounts (1500-1560)
- [ ] Create all Accumulated Depreciation accounts (1590-1594)
- [ ] Create all Depreciation Expense accounts (6100-6106)
- [ ] Create Gain/Loss accounts (4900, 6106)
- [ ] Set up Asset Classes with GL account mappings
- [ ] Verify account types (DEBIT vs CREDIT)
- [ ] Verify account sections
- [ ] Test journal entry creation
- [ ] Verify financial statement presentation

---

## 🔐 Account Controls

### Posting Restrictions

| Account | Manual Posting Allowed | Auto-Posted By |
|---------|----------------------|----------------|
| 1500-1560 | ✅ Yes (via invoices) | Fixed Asset Entry |
| 1590-1594 | ❌ No | Depreciation Calculation |
| 6100-6104 | ❌ No | Depreciation Calculation |
| 6105 | ❌ No | Parameter Change (Impairment) |
| 6106 | ❌ No | Write-Off / Sale |
| 4900 | ❌ No | Sale (when gain) |

### Reconciliation

**Monthly:**
- Reconcile Fixed Asset subledger to GL
- Verify depreciation calculations
- Review new acquisitions and disposals

**Annually:**
- Physical asset verification
- Impairment testing
- Useful life review

---

## 📞 Support

For questions about Chart of Accounts setup:
- See: `README.md` in fixed-assets-module folder
- API Documentation: http://localhost:8080/swagger-ui.html
- Postman Collection: `Fixed-Assets-Postman-Collection.json`

---

**Last Updated:** 2026-01-06  
**Version:** 1.0  
**Module:** Fixed Assets - Chart of Accounts
