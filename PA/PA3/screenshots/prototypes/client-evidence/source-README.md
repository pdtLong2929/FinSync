# FinSync-PA3-Evidence

Scope: All 12 Client Use Cases

Figma Make Prototype — Simulated UI

Each UC-CLI-XX folder contains real PNG captures of the simulated prototype, grouped by Basic (BF), Alternative (AF), or Exception (EF) flow. Screenshot-Matrix.csv maps descriptions and source labels to filenames. Evidence-Manifest.json records image sizes, checksums, captured routes, and timestamps. Evidence-Report.md and Missing-Scenarios.md state coverage and limitations.

## Reproduce

From the project with its supervised preview running: pnpm pa3:validate, then pnpm pa3:capture. Set PA3_BASE_URL if the preview is not at localhost:3000; set CHROMIUM_PATH for the Chromium executable if needed. Capture refuses to run against a failed, incomplete, or stale validation result. Open /pa3-evidence to preview/download PNGs or generate a browser ZIP from the verified captures.

The static exporter does not launch headless Chromium inside the browser. Reliable automatic capture is performed by the local CLI; the page packages those verified artifacts with progress. Regenerate with the CLI after changing the prototype.

All financial operations are in-memory simulations. Invitations use a local demo code only. No API, JWT, PostgreSQL, real money movement, or database persistence is asserted. These are NOT actual Android Emulator screenshots. Secondary Android screenshots and the full handoff archive were unavailable locally; new screens follow the supplied specifications and existing Liquid Metal components. Native Android pixel fidelity, physical keyboards, and real network/server failures are not certified by these browser checks.
