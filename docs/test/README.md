# Test Documentation

This directory contains all testing-related documents.

## Contents

| Document | Description | Responsible |
|---|---|---|
| `test-plan.md` | Overall test strategy, scope, tools, and schedule | QA Lead (Vương Đắc Gia Khiêm) |
| `test-cases/` | Detailed test case specifications per feature | All members |
| `test-results/` | Test execution results and reports | QA Lead |

## Test Case File Naming

```
TC_[FeatureGroup]_[ShortName].md
```

Example: `TC_Auth_Login.md`, `TC_Group_SplitDebt.md`

## Test Case Template

```markdown
## Test Case ID: TC_XXX_YYY

> *Performed by: [Name] | Reviewed by: [Name]*

### Preconditions
- ...

### Test Steps
| Step | Action | Expected Result |
|------|--------|----------------|
| 1    | ...    | ...            |

### Postconditions
- ...
```
