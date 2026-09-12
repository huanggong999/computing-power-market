# Compute Rent Draft Price Sync

## Symptom

On `computeListNew`, confirming an order showed a monthly total of 320. After navigating to `computeRent`, the bottom fee summary used 320 but the host table still showed 2880/month.

## Root Cause

`computeListNew` stores the selected order draft in `sessionStorage` before navigating. `computeRent` read the draft and used it for the fee summary, but the host table price came from `currentPriceItem`, which was still based on `/pc/gpu/market/detail/{resourceId}` prices. The draft price was not merged back into the selected billing type's `prices` entry.

## Fix

`computeRent` now passes the order draft into `normalizePriceOptions`. When the draft has a selected `billingType` and `unitAmount`, that price overwrites or inserts the matching price entry, so host table price, fee summary, and create request pricing all use the same order-confirmed amount.

## Verification

`pnpm run build` in `ux-user-main` passed on 2026-07-10. Browser verification clicked `确认下单` from `computeListNew` and confirmed the `computeRent` page showed `¥320.00/月` in the host table and bottom summary, with no `2880.00` remaining.
