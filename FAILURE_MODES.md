# Failure Modes — Distributed Job Engine Operational Architecture

## 1. Worker Threads Crash Mid-Execution
* **The Scenario:** A worker background thread picks up a high-priority transaction, changes its row status to `RUNNING`, and a sudden server hardware power loss kills the application before execution completes.
* **System Defenses:** The job remains inside PostgreSQL as `RUNNING` with an expired visibility lease timestamp (`lease_expires_at`). The Lease Reclaim Scheduler loop wakes up every 10 seconds, sweeps the disk table, identifies the expired lease, wipes the thread locks, and resets the status back to `PENDING`.

## 2. Job Strategy Handler Throws Runtime Errors
* **The Scenario:** A payment handler encounters a 3rd-party banking gateway connection drop or timeout.
* **System Defenses:** The `JobWorker` protective catch-fuse intercepts the error, halting success writes. It delegates states to the `RetryService`. If current attempts are below the maximum cap, it calculates a widening delay slope via `BackoffCalculator` and schedules a future processing target by stamping the `RETRYING` state flag.

## 3. Duplicate Transaction Intake Submissions
* **The Scenario:** A client submits the exact same payment request twice due to a network dropout or a fast double-click.
* **System Defenses:** The ingestion controller service layer intercepts the request and checks the unique `idempotencyKey` column. If a match is found in database history, the engine refuses to manufacture a new job or duplicate execution rows—it instantly hands back the existing historical tracking object data.

## 4. PostgreSQL Database Instance Dropping Offline
* **The Scenario:** The database container crashes or faces a network split partition failure.
* **System Defenses:** Every database transaction automatically drops out and triggers a core rollback execution. No memory state cache corruption happens inside RAM, because the data properties are isolated cleanly across transactional thread boundaries.