-- Show all units with their size and rate
SELECT su.unit_number, ut.size, ut.base_rate, su.occupied, su.customer_id
FROM storage_units su
         JOIN unit_types ut ON su.type_id = ut.type_id;

-- Show vacant units only
SELECT su.unit_number, ut.size, ut.base_rate
FROM storage_units su
         JOIN unit_types ut ON su.type_id = ut.type_id
WHERE su.occupied = FALSE;

-- Show occupied units only
SELECT su.unit_number, ut.size, ut.base_rate, su.customer_id
FROM storage_units su
         JOIN unit_types ut ON su.type_id = ut.type_id
WHERE su.occupied = TRUE;

-- Count units by size
SELECT ut.size, COUNT(*) AS total_units
FROM storage_units su
         JOIN unit_types ut ON su.type_id = ut.type_id
GROUP BY ut.size;

-- Potential monthly revenue if all units are occupied
SELECT SUM(ut.base_rate) AS potential_monthly_revenue
FROM storage_units su
         JOIN unit_types ut ON su.type_id = ut.type_id;