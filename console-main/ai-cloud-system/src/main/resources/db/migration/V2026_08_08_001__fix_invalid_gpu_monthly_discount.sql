-- Resource 6 monthly price is 320 CNY/month. The historical discount value 2880
-- is higher than the unit price and must not be treated as the payable unit price.
UPDATE gpu_resource_price
SET discount_price = NULL,
    discount_rate = NULL
WHERE resource_id = 6
  AND billing_type = 'monthly'
  AND unit_price = 320.00
  AND discount_price = 2880.00;
