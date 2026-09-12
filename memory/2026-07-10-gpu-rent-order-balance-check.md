# GPU Rent Order Balance Check

## Symptom

On the `computeRent` page, the UI showed account balance 984.96 and config fee 320.00, but clicking "创建并开机" returned "余额不足".

## Root Cause

The frontend carried the confirmed quote in `podCreateRequest.pricing.discountTotalCost`, but `/pc/gpu/rent/order` called `calculate(dto)` again and used the recalculated database price for balance validation and order payment. In this case the recalculated amount did not match the quoted 320.00 from `computeListNew`.

## Fix

`PcGpuRentController.order` now resolves the order fee from the pricing snapshot in `GpuPodCreateRequest` when `discountTotalCost` is present, while still calling `calculate(dto)` first for existing stock and price validation. Balance validation, returned order amount, and created order payment now use the same fee object.

## Verification

The backend module compile command is currently blocked by unrelated existing Lombok/enum errors elsewhere in `ai-cloud-system`. The edited controller has no reported compilation error before the build fails in unrelated files.
