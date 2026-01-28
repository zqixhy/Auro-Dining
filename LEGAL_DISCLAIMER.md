# Legal Disclaimer — Demo / No Real Payment or Delivery

## Overview

**Auro Dining** is a **demo / portfolio project**. It does **not** process real payments and does **not** fulfill or deliver real orders. If you deploy it publicly, you must clearly disclose this to avoid misleading users and to reduce legal risk.

---

## Risks If You Don't Disclose

1. **Consumer protection**
   - Users may believe they are paying or will receive delivery.
   - Regulators (e.g. FTC in the US) can treat misleading claims as unfair or deceptive.

2. **Contract / terms**
   - Implied “offer to sell” or “contract to deliver” could create unintended legal exposure if users reasonably believe they are transacting.

3. **Reputation**
   - Confusion or complaints (“I paid but nothing arrived”) can damage trust even if no real payment occurred.

---

## Mitigations Implemented in This Project

### 1. **User-facing (frontend)**

- **Banners / notices**
  - Checkout: *"Demo only. No payment will be charged. No delivery will be made."*
  - Success: *"Order Submitted"* + *"This is a demo. No payment was charged. No delivery will be made."*
  - Home & order list: *"Demo only. No real payment or delivery."*

- **Copy**
  - “Place Order” → **“Submit (Demo)”**
  - “Check Out” → **“Check Out (Demo)”**
  - “Estimated Delivery” → **“Delivery: N/A (demo — no real delivery)”**
  - “Order Success” → **“Order Submitted”** with demo disclaimer.

### 2. **Admin / backend**

- Order management page: notice that orders are **demo only**, and that **“Send” / “Finish”** are for **demo workflow only** (no real fulfillment).
- Buttons labeled **“mark sent (demo)”** and **“mark complete (demo)”**.

### 3. **No real payment**

- No payment gateway (Stripe, PayPal, etc.) is integrated.
- No card or billing data is collected.
- `payMethod` is stored for demo workflow only; no actual charge occurs.

### 4. **No real delivery**

- No delivery partner or shipping integration.
- Address is stored for demo purposes only; nothing is shipped.

---

## Recommendations When Deploying

### 1. **Keep disclaimers visible**

- Do **not** remove or hide the demo banners and **(Demo)** labels on checkout, success, and order pages.
- Ensure they are visible on mobile and desktop.

### 2. **Add a Terms of Use / Disclaimer page (optional but recommended)**

Example wording you could adapt:

- *"This site is a demonstration. No real orders are fulfilled. No payment is processed. No delivery is made. Prices and products are for illustration only."*

Link it from the footer or a clear “Disclaimer” / “Terms” link.

### 3. **Avoid collecting sensitive data**

- Do **not** collect real payment details.
- If you collect emails/phones for verification, state they are for demo/login only and not used for marketing or real orders.

### 4. **Domain / branding**

- Prefer a clear demo context (e.g. `demo.aurodining.com` or `auro-dining-demo.vercel.app`).
- Avoid names or copy that imply a real, operating restaurant or e‑commerce service unless you explicitly add “Demo” or “Portfolio”.

### 5. **Monitor and respond**

- If users report “I paid” or “I didn’t receive my order,” respond promptly that the app is demo-only, no payment is taken, and no delivery is made.

---

## Summary

| Item | Status |
|------|--------|
| No real payment | ✅ No gateway; no billing data |
| No real delivery | ✅ No shipping; address for demo only |
| Clear “demo” disclaimers | ✅ Banners and (Demo) labels |
| Non-misleading copy | ✅ “Submit (Demo)”, “Order Submitted”, etc. |
| Admin notice | ✅ Demo notice + “mark sent/complete (demo)” |

**Bottom line:** The project includes in-app disclaimers and labeling to reduce risk. When you deploy, keep them, avoid real payments/delivery, and consider adding a short Terms/Disclaimer page. This is not legal advice; consult a lawyer for your specific situation.
