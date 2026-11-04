#!/usr/bin/env python3
"""Apply the Vision-alignment edits to uc-ai-features.md.

Usage:  python apply_uc_ai_edits.py [path/to/uc-ai-features.md]

Every edit must match exactly once; otherwise nothing is written and the failing edits are listed.
A backup is saved as <file>.bak before writing.
"""
import shutil, sys
from pathlib import Path

EDITS = [
 # 1. Section 1.2, BR-AI-05: align with NFR-P4 (30 seconds in total)
 ("| BR-AI-05 | The call to the LLM API has a 30-second timeout and one automatic retry. After that, the basic rule-based analysis is used. |",
  "| BR-AI-05 | A request to the LLM API is limited to 30 seconds in total (NFR-P4): each attempt times out after 15 seconds and one automatic retry is allowed. If both attempts fail, the basic rule-based analysis is used. |"),
 # 2. Section 1.2, new BR-AI-10 after BR-AI-09 (offline reading, as in the PA2 Vision)
 ("| BR-AI-09 | Every stored result carries its source (\"AI\" or \"Basic analysis\") and, when applicable, the \"Limited history\" label. |",
  "| BR-AI-09 | Every stored result carries its source (\"AI\" or \"Basic analysis\") and, when applicable, the \"Limited history\" label. |\n"
  "| BR-AI-10 | A previously synchronized analysis (summary, anomalies, recommended budget, savings evaluation) and the analysis history remain readable offline with their last-synchronization time. Generating a new analysis and applying budgets require a connection. |"),
 # 3. Section 1.2 note on parameters
 ("The numeric parameters (30 seconds, 5 requests per day, 30%) are configurable team parameters.",
  "The numeric parameters (30 seconds in total with 15 seconds per attempt, 5 requests per day, 30%) are configurable team parameters."),
 # 4. UC-AI-01 overview table
 ("performance (30-second AI timeout)", "performance (30-second total AI limit, NFR-P4)"),
 # 5. UC-AI-01 basic flow step 4
 ("with a 30-second timeout (BR-AI-05). Receives the answer in JSON format.",
  "with a 15-second timeout per attempt and a 30-second total limit (BR-AI-05). Receives the answer in JSON format."),
 # 6. UC-AI-01 AF-04
 ("1. System retries once (BR-AI-05).<br>2. If the retry fails",
  "1. System retries once within the 30-second total limit (BR-AI-05).<br>2. If the retry fails"),
 # 7. UC-AI-01 AF-05
 ("1. System discards the answer and retries once.<br>2. If the new answer is still invalid",
  "1. System discards the answer and retries once within the same 30-second total limit.<br>2. If the new answer is still invalid"),
 # 8. UC-AI-01 special requirements
 ("Reliability: the user always receives a usable result through the basic analysis. Amounts are computed",
  "Reliability: the user always receives a usable result through the basic analysis, within 30 seconds in total (NFR-P4). BR-AI-10 applies: a result saved earlier stays readable offline, while a new analysis needs a connection. Amounts are computed"),
 # 9-10. UC-AI-03
 ("| Related business rules / NFRs | BR-AI-01, BR-AI-07, BR-AI-09; NFR: usability",
  "| Related business rules / NFRs | BR-AI-01, BR-AI-07, BR-AI-09, BR-AI-10; NFR: usability"),
 ("| AF-05 / Connectivity error | Step 1: no network connection or server error while loading the analysis. |",
  "| AF-05 / Connectivity error | Step 1: no network connection or server error while loading the analysis, and no saved copy exists on the device. |"),
 ("more than 30% above the 3-month baseline). Only the user's own data is shown.",
  "more than 30% above the 3-month baseline). Only the user's own data is shown. BR-AI-10 applies: the saved analysis and the cached personal transactions remain readable offline; the error in AF-05 appears only when no saved copy exists."),
 # 11-12. UC-AI-04
 ("| Related business rules / NFRs | BR-AI-04, BR-AI-08, BR-AI-09; NFRs:",
  "| Related business rules / NFRs | BR-AI-04, BR-AI-08, BR-AI-09, BR-AI-10; NFRs:"),
 ("The update of all selected budgets is atomic. Amounts use decimal arithmetic in the user's reporting currency.",
  "The update of all selected budgets is atomic. BR-AI-10 applies: the saved recommendation is readable offline, but applying the budgets requires a connection; a failed attempt is handled by AF-06. Amounts use decimal arithmetic in the user's reporting currency."),
 # 13-15. UC-AI-05
 ("| Related business rules / NFRs | BR-AI-08, BR-AI-09; NFR: usability |",
  "| Related business rules / NFRs | BR-AI-08, BR-AI-09, BR-AI-10; NFR: usability |"),
 ("| AF-07 / Connectivity error | Step 1: no network connection or server error. |",
  "| AF-07 / Connectivity error | Step 1: no network connection or server error, and no saved copy of the analysis exists on the device. |"),
 ("follow the currency rules of the Savings Goal Management group.",
  "follow the currency rules of the Savings Goal Management group. BR-AI-10 applies: the saved evaluation is readable offline; the error in AF-07 appears only when no saved copy exists."),
 # 16-18. UC-AI-06
 ("| Related business rules / NFRs | BR-AI-06, BR-AI-09; NFR: usability |",
  "| Related business rules / NFRs | BR-AI-06, BR-AI-09, BR-AI-10; NFR: usability |"),
 ("| AF-04 / Load failure | Step 1 or 2: no network connection or server error. |",
  "| AF-04 / Load failure | Step 1 or 2: no network connection or server error, and the requested data is not saved on the device. |"),
 ("Only the user's own analyses are shown.",
  "Only the user's own analyses are shown. BR-AI-10 applies: previously synchronized history is readable offline with its last-synchronization time."),
]

def main():
    path = Path(sys.argv[1] if len(sys.argv) > 1 else "uc-ai-features.md")
    text = path.read_text(encoding="utf-8")
    bad = [(i, text.count(f)) for i, (f, _) in enumerate(EDITS, 1) if text.count(f) != 1]
    if bad:
        for i, n in bad:
            print(f"Edit {i}: expected exactly 1 match, found {n}; text starts: {EDITS[i-1][0][:70]!r}")
        sys.exit("Nothing written. Fix the file or the edit list and retry.")
    shutil.copy(path, str(path) + ".bak")
    for f, r in EDITS:
        text = text.replace(f, r)
    path.write_text(text, encoding="utf-8")
    print(f"Applied {len(EDITS)} edits to {path} (backup: {path}.bak)")

if __name__ == "__main__":
    main()